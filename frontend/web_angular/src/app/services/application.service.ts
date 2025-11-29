import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export enum ApplicationStatus {
  NEW = 'NEW',
  UNDER_REVIEW = 'UNDER_REVIEW',
  SHORTLISTED = 'SHORTLISTED',
  INTERVIEW_SCHEDULED = 'INTERVIEW_SCHEDULED',
  REJECTED = 'REJECTED',
  ACCEPTED = 'ACCEPTED',
  WITHDRAWN = 'WITHDRAWN'
}

export interface ApplicationRequestDTO {
  jobSeekerId?: string; // Will be set automatically from JWT
  jobOfferId: number;
  cvLink: string;
  motivationLettre?: string;
  status?: ApplicationStatus;
  aiScore?: number;
  isFavorite?: boolean;
}

export interface ApplicationResponseDTO {
  id: string;
  applicationDate: string;
  status: ApplicationStatus;
  cvLink: string;
  motivationLettre?: string;
  jobSeekerId: string;
  jobOfferId: number;
  aiScore?: number;
  isFavorite: boolean;
  lastStatusChange?: string;
  createdAt: string;
  updatedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class ApplicationService {
  private apiUrl = 'http://localhost:8888/application-service/api/applications';

  constructor(private http: HttpClient) {}

  /**
   * Create a new application (JOB_SEEKER only)
   */
  createApplication(dto: ApplicationRequestDTO): Observable<ApplicationResponseDTO> {
    return this.http.post<ApplicationResponseDTO>(this.apiUrl, dto);
  }

  /**
   * Get all applications (RECRUITER/ADMIN only)
   */
  getAllApplications(): Observable<ApplicationResponseDTO[]> {
    return this.http.get<ApplicationResponseDTO[]>(this.apiUrl);
  }

  /**
   * Get application by ID
   */
  getApplicationById(id: string): Observable<ApplicationResponseDTO> {
    return this.http.get<ApplicationResponseDTO>(`${this.apiUrl}/${id}`);
  }

  /**
   * Get applications by job offer ID (RECRUITER/ADMIN only)
   */
  getApplicationsByJobOfferId(jobOfferId: number): Observable<ApplicationResponseDTO[]> {
    return this.http.get<ApplicationResponseDTO[]>(`${this.apiUrl}/joboffer/${jobOfferId}`);
  }

  /**
   * Get my applications (JOB_SEEKER only)
   */
  getMyApplications(): Observable<ApplicationResponseDTO[]> {
    return this.http.get<ApplicationResponseDTO[]>(`${this.apiUrl}/my-applications`);
  }

  /**
   * Update application partially (JOB_SEEKER only - own applications)
   */
  updateApplication(id: string, dto: Partial<ApplicationRequestDTO>): Observable<ApplicationResponseDTO> {
    return this.http.patch<ApplicationResponseDTO>(`${this.apiUrl}/${id}`, dto);
  }

  /**
   * Update application status (RECRUITER only)
   */
  updateApplicationStatus(id: string, status: ApplicationStatus): Observable<ApplicationResponseDTO> {
    return this.http.patch<ApplicationResponseDTO>(
      `${this.apiUrl}/${id}/status`,
      { status: status }
    );
  }

  /**
   * Update AI score (RECRUITER/ADMIN only)
   */
  updateAiScore(id: string, aiScore: number): Observable<ApplicationResponseDTO> {
    return this.http.patch<ApplicationResponseDTO>(
      `${this.apiUrl}/${id}/ai-score`,
      { aiScore: aiScore }
    );
  }

  /**
   * Delete application (JOB_SEEKER only - own applications)
   */
  deleteApplication(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Check if duplicate application exists
   */
  checkDuplicateApplication(jobOfferId: number, jobSeekerId: string): Observable<boolean> {
    const params = new HttpParams()
      .set('jobOfferId', jobOfferId.toString())
      .set('jobSeekerId', jobSeekerId);
    
    return this.http.get<boolean>(`${this.apiUrl}/check-duplicate`, { params });
  }
}