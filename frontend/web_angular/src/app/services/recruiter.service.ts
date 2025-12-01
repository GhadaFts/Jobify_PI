import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Recruiter } from '../types';

@Injectable({
  providedIn: 'root'
})
export class RecruiterService {
  private apiUrl = 'http://localhost:8888/auth-service/user'; // Gateway URL

  constructor(private http: HttpClient) {}

  /**
   * Get recruiter profile by Keycloak ID
   */
  getRecruiterProfile(keycloakId: string): Observable<Recruiter> {
    return this.http.get<Recruiter>(`${this.apiUrl}/${keycloakId}/public`);
  }

  /**
   * Get recruiter profile by email (if you need this)
   */
  getRecruiterByEmail(email: string): Observable<Recruiter> {
    return this.http.get<Recruiter>(`${this.apiUrl}/by-email/${email}`);
  }
  
}