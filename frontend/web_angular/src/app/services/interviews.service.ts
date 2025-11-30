import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable, catchError, throwError, tap } from 'rxjs';
import { Interview, InterviewStatus, InterviewType } from '../types';

@Injectable({
  providedIn: 'root'
})
export class InterviewsService {
  private apiUrl = 'http://localhost:8888/interview-service/api/interviews';
  private interviews: Interview[] = [];
  private interviewsSubject = new BehaviorSubject<Interview[]>(this.interviews);
  
  public interviews$ = this.interviewsSubject.asObservable();

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('access_token') || '';
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // POST /api/interviews - Schedule interview (RECRUITER only)
  scheduleInterview(interviewData: any): Observable<any> {
    const headers = this.getAuthHeaders();

    return this.http.post<any>(`${this.apiUrl}`, interviewData, { headers }).pipe(
      tap((response) => {
        console.log('Interview scheduled successfully:', response);
        // Optionally update local state
        this.loadInterviews(); // Reload interviews after scheduling
      }),
      catchError(err => {
        console.error('Error scheduling interview:', err);
        return throwError(() => err);
      })
    );
  }

  // GET /api/interviews/{id} - Get interview by ID (RECRUITER, JOB_SEEKER)
  getInterviewById(id: number): Observable<any> {
    const headers = this.getAuthHeaders();

    return this.http.get<any>(`${this.apiUrl}/${id}`, { headers }).pipe(
      catchError(err => {
        console.error('Error fetching interview:', err);
        return throwError(() => err);
      })
    );
  }

  // GET /api/interviews/application/{applicationId} - Get interviews by application ID
  getInterviewsByApplicationId(applicationId: string): Observable<any[]> {
    const headers = this.getAuthHeaders();

    return this.http.get<any[]>(`${this.apiUrl}/application/${applicationId}`, { headers }).pipe(
      catchError(err => {
        console.error('Error fetching interviews by application:', err);
        return throwError(() => err);
      })
    );
  }

  // GET /api/interviews/my-interviews - Get job seeker's interviews (JOB_SEEKER only)
  getMyInterviews(): Observable<any[]> {
    const headers = this.getAuthHeaders();

    return this.http.get<any[]>(`${this.apiUrl}/my-interviews`, { headers }).pipe(
      tap(interviews => {
        console.log('Loaded my interviews:', interviews);
        this.updateLocalInterviews(interviews);
      }),
      catchError(err => {
        console.error('Error fetching my interviews:', err);
        return throwError(() => err);
      })
    );
  }

  // GET /api/interviews/my-interviews/upcoming - Get job seeker's upcoming interviews
  getMyUpcomingInterviews(): Observable<any[]> {
    const headers = this.getAuthHeaders();

    return this.http.get<any[]>(`${this.apiUrl}/my-interviews/upcoming`, { headers }).pipe(
      catchError(err => {
        console.error('Error fetching upcoming interviews:', err);
        return throwError(() => err);
      })
    );
  }

  // GET /api/interviews/recruiter/my-interviews - Get recruiter's interviews
  getRecruiterInterviews(): Observable<any[]> {
    const headers = this.getAuthHeaders();

    return this.http.get<any[]>(`${this.apiUrl}/recruiter/my-interviews`, { headers }).pipe(
      tap(interviews => {
        console.log('Loaded recruiter interviews:', interviews);
        this.updateLocalInterviews(interviews);
      }),
      catchError(err => {
        console.error('Error fetching recruiter interviews:', err);
        return throwError(() => err);
      })
    );
  }

  // GET /api/interviews/recruiter/my-interviews/upcoming - Get recruiter's upcoming interviews
  getRecruiterUpcomingInterviews(): Observable<any[]> {
    const headers = this.getAuthHeaders();

    return this.http.get<any[]>(`${this.apiUrl}/recruiter/my-interviews/upcoming`, { headers }).pipe(
      catchError(err => {
        console.error('Error fetching recruiter upcoming interviews:', err);
        return throwError(() => err);
      })
    );
  }

  // PUT /api/interviews/{id} - Update interview (RECRUITER only)
  updateInterview(id: number, updateData: any): Observable<any> {
    const headers = this.getAuthHeaders();

    return this.http.put<any>(`${this.apiUrl}/${id}`, updateData, { headers }).pipe(
      tap((response) => {
        console.log('Interview updated successfully:', response);
        this.loadInterviews(); // Reload interviews after update
      }),
      catchError(err => {
        console.error('Error updating interview:', err);
        return throwError(() => err);
      })
    );
  }

  // DELETE /api/interviews/{id} - Cancel interview (RECRUITER only)
  cancelInterview(id: number): Observable<void> {
    const headers = this.getAuthHeaders();

    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers }).pipe(
      tap(() => {
        console.log('Interview cancelled successfully');
        this.loadInterviews(); // Reload interviews after cancellation
      }),
      catchError(err => {
        console.error('Error cancelling interview:', err);
        return throwError(() => err);
      })
    );
  }

  // POST /api/interviews/reminders/send - Send reminders (ADMIN only)
  sendInterviewReminders(): Observable<void> {
    const headers = this.getAuthHeaders();

    return this.http.post<void>(`${this.apiUrl}/reminders/send`, {}, { headers }).pipe(
      tap(() => {
        console.log('Interview reminders sent successfully');
      }),
      catchError(err => {
        console.error('Error sending interview reminders:', err);
        return throwError(() => err);
      })
    );
  }

  // Helper method to load interviews based on user role
  loadInterviews(): void {
    const token = localStorage.getItem('access_token');
    if (!token) return;

    // Decode token to check roles (simplified - you might want to use a proper JWT library)
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const roles: string[] = payload.realm_access?.roles || [];

      if (roles.includes('JOB_SEEKER')) {
        this.getMyInterviews().subscribe();
      } else if (roles.includes('RECRUITER')) {
        this.getRecruiterInterviews().subscribe();
      }
    } catch (error) {
      console.error('Error decoding token:', error);
    }
  }

  // Update local interviews state
  private updateLocalInterviews(interviews: any[]): void {
    this.interviews = interviews.map(interview => this.mapToInterview(interview));
    this.interviewsSubject.next([...this.interviews]);
  }

  // Map API response to Interview interface
  private mapToInterview(response: any): Interview {
    return {
      id: response.id,
      applicationId: response.applicationId,
      jobSeekerId: response.jobSeekerId,
      recruiterId: response.recruiterId,
      scheduledDate: response.scheduledDate,
      duration: response.duration,
      location: response.location,
      interviewType: response.interviewType,
      status: response.status as InterviewStatus,
      notes: response.notes,
      meetingLink: response.meetingLink,
      createdAt: response.createdAt,
      updatedAt: response.updatedAt
    };
  }

  // Existing local methods (kept for backward compatibility)
  addInterview(interviewData: any): void {
    const newInterview: Interview = {
      id: Date.now(),
      jobSeekerId: interviewData.jobSeekerId,
      recruiterId: interviewData.recruiterId,
      applicationId: interviewData.applicationId,
      scheduledDate: interviewData.scheduledDate,
      duration: interviewData.duration,
      interviewType: interviewData.interviewType,
      location: interviewData.location,
      status: InterviewStatus.SCHEDULED,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };

    this.interviews.unshift(newInterview);
    this.interviewsSubject.next([...this.interviews]);
    console.log('Interview added locally:', newInterview);
  }

  getAllInterviews(): Interview[] {
    return [...this.interviews];
  }

  // Vérifier les conflits d'horaire
  checkTimeConflict(interviewDate: string, interviewTime: string, duration: number): boolean {
    const selectedDateTime = new Date(interviewDate + 'T' + interviewTime);
    const selectedEndTime = new Date(selectedDateTime.getTime() + duration * 60000);

    return this.interviews.some(existingInterview => {
      if (existingInterview.status === InterviewStatus.COMPLETED) return false;

      const existingDateTime = new Date(existingInterview.scheduledDate);
      const existingEndTime = new Date(existingDateTime.getTime() + existingInterview.duration * 60000);

      // Vérifier si les plages horaires se chevauchent
      return (
        (selectedDateTime >= existingDateTime && selectedDateTime < existingEndTime) ||
        (selectedEndTime > existingDateTime && selectedEndTime <= existingEndTime) ||
        (selectedDateTime <= existingDateTime && selectedEndTime >= existingEndTime)
      );
    });
  }

  // Méthode pour simuler le titre du job (à adapter avec vos données)
  private getJobTitle(jobOfferId: string): string {
    const jobTitles: { [key: string]: string } = {
      '1': 'Senior Frontend Developer',
      '2': 'Product Manager'
    };
    return jobTitles[jobOfferId] || 'Job Offer';
  }
}