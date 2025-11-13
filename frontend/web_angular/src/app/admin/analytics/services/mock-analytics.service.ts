import { Injectable } from '@angular/core';
import { Observable, of, BehaviorSubject } from 'rxjs';
import { delay, map } from 'rxjs/operators';
import {
  KpiData,
  JobsPostedData,
  ActiveVsExpiredData,
  UsersGrowthData,
  ApplicationFunnelData,
  AppsPerJobData,
  TopCompaniesData,
  TopCategoriesData,
  GeoHeatmapData,
  AlertData,
  AnalyticsFilters,
  AnalyticsResponse
} from '../analytics.types';

@Injectable({
  providedIn: 'root'
})
export class MockAnalyticsService {
  private filtersSubject = new BehaviorSubject<AnalyticsFilters>({
    dateRange: { startDate: '2025-10-01', endDate: '2025-10-31' },
    granularity: 'day'
  });

  filters$ = this.filtersSubject.asObservable();

  constructor() {}

  /**
   * A) KPI Cards data
   */
  getKpiData(): Observable<AnalyticsResponse<KpiData>> {
    const kpiData: KpiData = {
      totalJobs: 324,
      jobsLast7Days: 48,
      totalUsers: 5021,
      newUsersLast7Days: 132,
      totalApplications: 12980,
      avgApplicationsPerJob: 40.06
    };

    return of({
      data: kpiData,
      timestamp: new Date().toISOString()
    }).pipe(delay(300));
  }

  /**
   * B) Jobs Posted Over Time - Area Chart
   * Uses job.createdAt field from JobOffer model
   */
  getJobsPostedOverTime(): Observable<AnalyticsResponse<JobsPostedData[]>> {
    const data: JobsPostedData[] = [
      { date: '2025-10-01', count: 12 },
      { date: '2025-10-02', count: 18 },
      { date: '2025-10-03', count: 9 },
      { date: '2025-10-04', count: 22 },
      { date: '2025-10-05', count: 15 },
      { date: '2025-10-06', count: 28 },
      { date: '2025-10-07', count: 19 },
      { date: '2025-10-08', count: 31 },
      { date: '2025-10-09', count: 14 },
      { date: '2025-10-10', count: 25 },
      { date: '2025-10-11', count: 20 },
      { date: '2025-10-12', count: 27 },
      { date: '2025-10-13', count: 16 },
      { date: '2025-10-14', count: 35 },
      { date: '2025-10-15', count: 24 },
      { date: '2025-10-16', count: 29 },
      { date: '2025-10-17', count: 18 },
      { date: '2025-10-18', count: 32 },
      { date: '2025-10-19', count: 21 },
      { date: '2025-10-20', count: 26 },
      { date: '2025-10-21', count: 19 },
      { date: '2025-10-22', count: 38 },
      { date: '2025-10-23', count: 23 },
      { date: '2025-10-24', count: 30 },
      { date: '2025-10-25', count: 17 },
      { date: '2025-10-26', count: 34 },
      { date: '2025-10-27', count: 22 },
      { date: '2025-10-28', count: 28 },
      { date: '2025-10-29', count: 25 },
      { date: '2025-10-30', count: 41 }
    ];

    return of({
      data,
      timestamp: new Date().toISOString()
    }).pipe(delay(400));
  }

  /**
   * C) Active vs Expired Jobs - Line Chart
   * Uses job.status enum from JobOffer model
   */
  getActiveVsExpiredJobs(): Observable<AnalyticsResponse<ActiveVsExpiredData[]>> {
    const data: ActiveVsExpiredData[] = [
      { date: '2025-10-01', active: 230, expired: 12 },
      { date: '2025-10-02', active: 240, expired: 14 },
      { date: '2025-10-03', active: 248, expired: 16 },
      { date: '2025-10-04', active: 260, expired: 19 },
      { date: '2025-10-05', active: 270, expired: 22 },
      { date: '2025-10-06', active: 285, expired: 25 },
      { date: '2025-10-07', active: 290, expired: 28 },
      { date: '2025-10-08', active: 305, expired: 32 },
      { date: '2025-10-09', active: 310, expired: 35 },
      { date: '2025-10-10', active: 320, expired: 38 },
      { date: '2025-10-11', active: 325, expired: 40 },
      { date: '2025-10-12', active: 330, expired: 42 },
      { date: '2025-10-13', active: 335, expired: 45 },
      { date: '2025-10-14', active: 345, expired: 48 },
      { date: '2025-10-15', active: 350, expired: 50 }
    ];

    return of({
      data,
      timestamp: new Date().toISOString()
    }).pipe(delay(400));
  }

  /**
   * D) Users Growth - New Users + Daily Active Users
   * Uses user.createdAt field
   */
  getUsersGrowth(): Observable<AnalyticsResponse<UsersGrowthData[]>> {
    const data: UsersGrowthData[] = [
      { date: '2025-10-01', newUsers: 35, dau: 420 },
      { date: '2025-10-02', newUsers: 40, dau: 450 },
      { date: '2025-10-03', newUsers: 32, dau: 465 },
      { date: '2025-10-04', newUsers: 48, dau: 520 },
      { date: '2025-10-05', newUsers: 42, dau: 545 },
      { date: '2025-10-06', newUsers: 55, dau: 580 },
      { date: '2025-10-07', newUsers: 38, dau: 600 },
      { date: '2025-10-08', newUsers: 62, dau: 640 },
      { date: '2025-10-09', newUsers: 45, dau: 670 },
      { date: '2025-10-10', newUsers: 58, dau: 710 },
      { date: '2025-10-11', newUsers: 50, dau: 740 },
      { date: '2025-10-12', newUsers: 65, dau: 780 },
      { date: '2025-10-13', newUsers: 48, dau: 810 },
      { date: '2025-10-14', newUsers: 72, dau: 860 },
      { date: '2025-10-15', newUsers: 55, dau: 900 }
    ];

    return of({
      data,
      timestamp: new Date().toISOString()
    }).pipe(delay(400));
  }

  /**
   * E) Application Funnel
   * Stages derived from Application + Interview models
   */
  getApplicationFunnel(): Observable<AnalyticsResponse<ApplicationFunnelData>> {
    const data: ApplicationFunnelData = {
      views: 10000,
      applyClicks: 1200,
      applications: 800,
      interviews: 120,
      hires: 28
    };

    return of({
      data,
      timestamp: new Date().toISOString()
    }).pipe(delay(300));
  }

  /**
   * F) Applications per Job - Histogram
   * Uses job.id + application count
   */
  getAppsPerJob(): Observable<AnalyticsResponse<AppsPerJobData[]>> {
    const data: AppsPerJobData[] = [
      { jobId: '1', jobTitle: 'Senior Frontend Developer', count: 45 },
      { jobId: '2', jobTitle: 'Full Stack Engineer', count: 38 },
      { jobId: '3', jobTitle: 'DevOps Specialist', count: 32 },
      { jobId: '4', jobTitle: 'Product Manager', count: 28 },
      { jobId: '5', jobTitle: 'UX/UI Designer', count: 25 },
      { jobId: '6', jobTitle: 'Data Scientist', count: 42 },
      { jobId: '7', jobTitle: 'Backend Developer', count: 35 },
      { jobId: '8', jobTitle: 'QA Engineer', count: 18 },
      { jobId: '9', jobTitle: 'Cloud Architect', count: 22 },
      { jobId: '10', jobTitle: 'Frontend Developer Intern', count: 15 }
    ];

    return of({
      data,
      timestamp: new Date().toISOString()
    }).pipe(delay(400));
  }

  /**
   * G) Top Companies - Bar Chart
   * Uses real Company model (from Recruiter)
   */
  getTopCompanies(): Observable<AnalyticsResponse<TopCompaniesData[]>> {
    const data: TopCompaniesData[] = [
      { companyName: 'Tech Corp', jobCount: 48, applicationCount: 2050 },
      { companyName: 'Microsoft', jobCount: 32, applicationCount: 1700 },
      { companyName: 'StartUp Inc', jobCount: 28, applicationCount: 1450 },
      { companyName: 'Cloud Solutions', jobCount: 25, applicationCount: 1200 },
      { companyName: 'Innovate Labs', jobCount: 22, applicationCount: 980 },
      { companyName: 'Creative Studio', jobCount: 18, applicationCount: 850 },
      { companyName: 'Google', jobCount: 15, applicationCount: 1600 },
      { companyName: 'Amazon', jobCount: 20, applicationCount: 1350 }
    ];

    return of({
      data,
      timestamp: new Date().toISOString()
    }).pipe(delay(400));
  }

  /**
   * H) Top Categories - Bar Chart
   * Uses job.category or job.type from JobOffer model
   */
  getTopCategories(): Observable<AnalyticsResponse<TopCategoriesData[]>> {
    const data: TopCategoriesData[] = [
      { category: 'Frontend Development', jobs: 120, applications: 2800 },
      { category: 'Backend Development', jobs: 105, applications: 2200 },
      { category: 'Data Science', jobs: 75, applications: 2100 },
      { category: 'DevOps', jobs: 52, applications: 1450 },
      { category: 'Product Management', jobs: 38, applications: 980 },
      { category: 'Design (UX/UI)', jobs: 45, applications: 850 },
      { category: 'QA Engineering', jobs: 30, applications: 620 },
      { category: 'Cloud Architecture', jobs: 28, applications: 750 }
    ];

    return of({
      data,
      timestamp: new Date().toISOString()
    }).pipe(delay(400));
  }

  /**
   * I) Geo Heatmap
   * Uses location field from JobOffer model
   */
  getGeoHeatmap(): Observable<AnalyticsResponse<GeoHeatmapData[]>> {
    const data: GeoHeatmapData[] = [
      { region: 'California', jobs: 120, applications: 2800 },
      { region: 'New York', jobs: 95, applications: 2100 },
      { region: 'Texas', jobs: 72, applications: 1450 },
      { region: 'Florida', jobs: 58, applications: 980 },
      { region: 'Illinois', jobs: 45, applications: 850 },
      { region: 'Pennsylvania', jobs: 38, applications: 720 },
      { region: 'Ohio', jobs: 32, applications: 620 },
      { region: 'Georgia', jobs: 28, applications: 540 }
    ];

    return of({
      data,
      timestamp: new Date().toISOString()
    }).pipe(delay(400));
  }

  /**
   * J) Alerts Panel
   * Shows metric changes and alerts
   */
  getAlerts(): Observable<AnalyticsResponse<AlertData[]>> {
    const data: AlertData[] = [
      { metric: 'Total Applications', change: -42, alert: true },
      { metric: 'Job Posts', change: 18, alert: false },
      { metric: 'New User Registrations', change: 25, alert: false },
      { metric: 'Interview Completions', change: -8, alert: true },
      { metric: 'Hiring Rate', change: 5, alert: false },
      { metric: 'Average Applications/Job', change: -3, alert: false }
    ];

    return of({
      data,
      timestamp: new Date().toISOString()
    }).pipe(delay(300));
  }

  /**
   * Update filters
   */
  updateFilters(filters: AnalyticsFilters): void {
    this.filtersSubject.next(filters);
  }

  /**
   * Get current filters
   */
  getFilters(): AnalyticsFilters {
    return this.filtersSubject.getValue();
  }
}
