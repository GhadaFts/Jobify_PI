import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CVUploadResponse {
  cvLink: string;
  fileName: string;
  fileSize: number;
  uploadedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class CvUploadService {
  private apiUrl = 'http://localhost:8888/application-service/api/cv';

  constructor(private http: HttpClient) {}

  /**
   * Upload CV file to backend
   */
  uploadCV(file: File, jobSeekerId: string, jobOfferId: number): Observable<CVUploadResponse> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('jobSeekerId', jobSeekerId);
    formData.append('jobOfferId', jobOfferId.toString());

    return this.http.post<CVUploadResponse>(`${this.apiUrl}/upload`, formData);
  }

  /**
   * Upload generated CV as PDF blob
   */
  uploadGeneratedCV(
    pdfBlob: Blob, 
    jobSeekerId: string, 
    jobOfferId: number, 
    fileName: string
  ): Observable<CVUploadResponse> {
    const formData = new FormData();
    formData.append('file', pdfBlob, fileName);
    formData.append('jobSeekerId', jobSeekerId);
    formData.append('jobOfferId', jobOfferId.toString());

    return this.http.post<CVUploadResponse>(`${this.apiUrl}/upload`, formData);
  }

  /**
   * Delete CV file from backend
   */
  deleteCV(cvLink: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete`, {
      params: { cvLink }
    });
  }

  /**
   * Get CV file URL for viewing (inline)
   */
  getCVUrl(cvLink: string): string {
    return `${this.apiUrl}/view/${cvLink}`;
  }

  /**
   * Get CV file URL for downloading (force download)
   */
  getCVDownloadUrl(cvLink: string): string {
    return `${this.apiUrl}/download/${cvLink}`;
  }

  /**
   * Download CV file as Blob (for programmatic download/view)
   * This goes through HttpClient and uses the auth interceptor automatically
   */
  downloadCVBlob(cvLink: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/download/${cvLink}`, {
      responseType: 'blob'
    });
  }

  /**
   * View CV file as Blob (alternative method for viewing)
   */
  viewCVBlob(cvLink: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/view/${cvLink}`, {
      responseType: 'blob'
    });
  }
}