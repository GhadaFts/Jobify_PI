import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const GATEWAY = 'http://localhost:8888';

@Injectable({
  providedIn: 'root'
})
export class ApplicationService {
  constructor(private http: HttpClient) {}

  // Get applications for a given job offer id
  getByJobOffer(jobOfferId: number | string): Observable<any[]> {
    return this.http.get<any[]>(`${GATEWAY}/application-service/api/applications/joboffer/${jobOfferId}`);
  }

  // Create new application (job seeker must be authenticated)
  createApplication(payload: any) {
    return this.http.post(`${GATEWAY}/application-service/api/applications`, payload);
  }

  // Other helpers can be added later (getMyApplications, updateStatus, etc.)
}
