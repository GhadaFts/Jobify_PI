import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const GATEWAY = 'http://localhost:8888';
const JOBS_BASE = `${GATEWAY}/joboffer-service/api/jobs`;

@Injectable({
  providedIn: 'root'
})
export class JobOfferService {
  constructor(private http: HttpClient) {}

  createJob(job: any): Observable<any> {
    return this.http.post(JOBS_BASE, job);
  }

  updateJob(id: string | number, job: Partial<any>): Observable<any> {
    return this.http.put(`${JOBS_BASE}/${id}`, job);
  }

  getMyJobs(): Observable<any> {
    return this.http.get(`${JOBS_BASE}/my-jobs`);
  }

  uploadLogo(form: FormData): Observable<any> {
    const UPLOAD_URL = `${GATEWAY}/joboffer-service/api/uploads/company-logo`;
    return this.http.post(UPLOAD_URL, form);
  }
}
