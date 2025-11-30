import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private apiUrl = 'http://localhost:8888/auth-service/user'; // adjust as needed

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('access_token') || '';

    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  /**
   * GET /user/:id (secured with Bearer token)
   */
  getUserById(keycloakId: string): Observable<any> {
    const headers = this.getAuthHeaders();

    return this.http.get<any>(`${this.apiUrl}/${keycloakId}`, { headers }).pipe(
      catchError(err => {
        console.error('Error fetching user:', err);
        return throwError(() => err);
      })
    );
  }
}
