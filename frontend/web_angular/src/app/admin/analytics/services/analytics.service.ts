import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { AnalyticsResponse, KpiData } from '../analytics.types';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private jobsApiUrl = 'http://localhost:8888/joboffer-service/api/analytics/kpi';
  private usersApiUrl = 'http://localhost:8888/auth-service/analytics/users';
  private applicationsApiUrl = 'http://localhost:8888/application-service/api/analytics/applications';
  private apiUrl = 'http://localhost:8888/joboffer-service/api/analytics';

  constructor(private http: HttpClient) {}

  /**
   * Get KPI data from backend
   */
  getKpiData(): Observable<AnalyticsResponse<KpiData>> {
    return forkJoin({
      jobs: this.http.get<any>(this.jobsApiUrl),
      users: this.http.get<any>(this.usersApiUrl),
      applications: this.http.get<any>(this.applicationsApiUrl)
    }).pipe(
      map(result => ({
        data: {
          totalJobs: result.jobs.totalJobs || 0,
          jobsLast7Days: result.jobs.jobsLast7Days || 0,
          totalUsers: result.users.totalUsers || 0,
          newUsersLast7Days: result.users.newUsersLast7Days || 0,
          totalApplications: result.applications.totalApplications || 0,
          avgApplicationsPerJob: result.applications.avgApplicationsPerJob || 0
        },
        timestamp: new Date().toISOString()
      }))
    );
  }

  /**
   * Get jobs posted over time
   */
  getJobsOverTime(startDate?: string, endDate?: string): Observable<any> {
    let url = `${this.apiUrl}/jobs-over-time`;
    const params: any = {};
    
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;
    
    return this.http.get<any>(url, { params }).pipe(
      map(response => response.data || response)
    );
  }

  /**
   * Get application funnel data
   */
  getApplicationFunnel(): Observable<any> {
    return this.http.get<any>('http://localhost:8888/application-service/api/analytics/funnel').pipe(
      map(response => ({
        data: {
          applications: response.applications || 0,
          interviews: response.interviews || 0,
          hires: response.hires || 0
        }
      }))
    );
  }

  /**
   * Get applications per job data
   */
  getAppsPerJob(): Observable<any> {
    return forkJoin({
      appsData: this.http.get<any>('http://localhost:8888/application-service/api/analytics/apps-per-job'),
      allJobs: this.http.get<any>('http://localhost:8888/joboffer-service/api/jobs')
    }).pipe(
      map(result => {
        // Create a map of jobId -> jobTitle
        const jobMap = new Map<number, string>();
        if (result.allJobs && Array.isArray(result.allJobs)) {
          result.allJobs.forEach((job: any) => {
            jobMap.set(job.id, job.title);
          });
        }

        // Enrich appsData with job titles
        const enrichedData = result.appsData.data.map((item: any) => ({
          jobId: item.jobId.toString(),
          jobTitle: jobMap.get(item.jobId) || `Job #${item.jobId}`,
          count: item.count
        }));

        return {
          data: enrichedData,
          timestamp: new Date().toISOString()
        };
      })
    );
  }

  /**
   * Get top companies by activity
   */
  getTopCompanies(): Observable<any> {
    // First, get company data from joboffer-service
    return this.http.get<any>('http://localhost:8888/joboffer-service/api/analytics/top-companies').pipe(
      switchMap(response => {
        const companies = response.data || [];
        
        if (companies.length === 0) {
          return of({
            data: [],
            timestamp: new Date().toISOString()
          });
        }
        
        // For each company, we need to count total applications
        const enrichmentRequests = companies.map((company: any) => {
          // Get application counts for all jobs from this company
          return this.http.post<any>(
            'http://localhost:8888/application-service/api/analytics/count-by-jobs',
            { jobIds: company.jobIds }
          ).pipe(
            map(appResponse => {
              const counts = appResponse.counts || {};
              const totalApplications = Object.values(counts).reduce((sum: number, count: any) => sum + count, 0);
              const avgAppsPerJob = company.jobCount > 0 ? totalApplications / company.jobCount : 0;
              
              return {
                company: company.company,
                jobCount: company.jobCount,
                totalApplications: totalApplications,
                avgAppsPerJob: Math.round(avgAppsPerJob * 10) / 10 // Round to 1 decimal
              };
            })
          );
        });

        // Execute all requests in parallel and return combined data
        return forkJoin(enrichmentRequests).pipe(
          map(enrichedCompanies => ({
            data: enrichedCompanies,
            timestamp: new Date().toISOString()
          }))
        );
      })
    );
  }

  /**
   * Get top job categories by job position
   */
  getTopCategories(): Observable<any> {
    // First, get category data from joboffer-service
    return this.http.get<any>('http://localhost:8888/joboffer-service/api/analytics/top-categories').pipe(
      switchMap(response => {
        const categories = response.data || [];
        
        if (categories.length === 0) {
          return of({
            data: [],
            timestamp: new Date().toISOString()
          });
        }
        
        // For each category, we need to count total applications
        const enrichmentRequests = categories.map((category: any) => {
          // Get application counts for all jobs in this category
          return this.http.post<any>(
            'http://localhost:8888/application-service/api/analytics/count-by-jobs',
            { jobIds: category.jobIds }
          ).pipe(
            map(appResponse => {
              const counts = appResponse.counts || {};
              const totalApplications = Object.values(counts).reduce((sum: number, count: any) => sum + count, 0);
              const ratio = category.jobCount > 0 ? totalApplications / category.jobCount : 0;
              
              return {
                category: category.category,
                jobCount: category.jobCount,
                totalApplications: totalApplications,
                ratio: Math.round(ratio * 10) / 10 // Round to 1 decimal
              };
            })
          );
        });

        // Execute all requests in parallel and return combined data
        return forkJoin(enrichmentRequests).pipe(
          map(enrichedCategories => ({
            data: enrichedCategories,
            timestamp: new Date().toISOString()
          }))
        );
      })
    );
  }

  /**
   * Get geographic distribution (jobs by location with application counts)
   */
  getGeographicDistribution(): Observable<any> {
    // First, get location data from joboffer-service
    return this.http.get<any>('http://localhost:8888/joboffer-service/api/analytics/geographic-distribution').pipe(
      switchMap(response => {
        const locations = response.data || [];
        
        if (locations.length === 0) {
          return of({
            data: [],
            timestamp: new Date().toISOString()
          });
        }
        
        // For each location, we need to count total applications
        const enrichmentRequests = locations.map((location: any) => {
          // Get application counts for all jobs in this location
          return this.http.post<any>(
            'http://localhost:8888/application-service/api/analytics/count-by-jobs',
            { jobIds: location.jobIds }
          ).pipe(
            map(appResponse => {
              const counts = appResponse.counts || {};
              const totalApplications = Object.values(counts).reduce((sum: number, count: any) => sum + count, 0);
              
              return {
                location: location.location,
                jobCount: location.jobCount,
                totalApplications: totalApplications
              };
            })
          );
        });

        // Execute all requests in parallel and return combined data
        return forkJoin(enrichmentRequests).pipe(
          map(enrichedLocations => ({
            data: enrichedLocations,
            timestamp: new Date().toISOString()
          }))
        );
      })
    );
  }

  /**
   * Get job posts alert data (current vs previous period)
   */
  getJobPostsAlert(days: number = 7): Observable<any> {
    return this.http.get<any>(`http://localhost:8888/joboffer-service/api/analytics/job-posts-alert?days=${days}`);
  }

  /**
   * Get applications alert data (current vs previous period)
   */
  getApplicationsAlert(days: number = 7): Observable<any> {
    return this.http.get<any>(`http://localhost:8888/application-service/api/analytics/applications-alert?days=${days}`);
  }

  /**
   * Get new users alert data (current vs previous period)
   */
  getNewUsersAlert(days: number = 7): Observable<any> {
    return this.http.get<any>(`http://localhost:8888/auth-service/analytics/users-alert?days=${days}`);
  }

  /**
   * Get interviews alert data (current vs previous period)
   */
  getInterviewsAlert(days: number = 7): Observable<any> {
    return this.http.get<any>(`http://localhost:8888/application-service/api/analytics/interviews-alert?days=${days}`);
  }

  /**
   * Get hiring rate alert data (current vs previous period)
   */
  getHiringAlert(days: number = 7): Observable<any> {
    return this.http.get<any>(`http://localhost:8888/application-service/api/analytics/hiring-alert?days=${days}`);
  }

  /**
   * Get all alert metrics in one call
   */
  getAllAlerts(days: number = 7): Observable<any> {
    return forkJoin({
      applications: this.getApplicationsAlert(days),
      jobPosts: this.getJobPostsAlert(days),
      newUsers: this.getNewUsersAlert(days),
      interviews: this.getInterviewsAlert(days),
      hiring: this.getHiringAlert(days)
    }).pipe(
      map(alerts => ({
        data: {
          totalApplications: {
            current: alerts.applications.current || 0,
            percentageChange: alerts.applications.percentageChange || 0,
            isAlert: alerts.applications.isAlert || false
          },
          jobPosts: {
            current: alerts.jobPosts.current || 0,
            percentageChange: alerts.jobPosts.percentageChange || 0,
            isAlert: alerts.jobPosts.isAlert || false
          },
          newUsers: {
            current: alerts.newUsers.current || 0,
            percentageChange: alerts.newUsers.percentageChange || 0,
            isAlert: alerts.newUsers.isAlert || false
          },
          interviews: {
            current: alerts.interviews.current || 0,
            percentageChange: alerts.interviews.percentageChange || 0,
            isAlert: alerts.interviews.isAlert || false
          },
          hiringRate: {
            current: alerts.hiring.currentRate || 0,
            percentageChange: alerts.hiring.percentageChange || 0,
            isAlert: alerts.hiring.isAlert || false
          }
        },
        timestamp: new Date().toISOString()
      }))
    );
  }
}
