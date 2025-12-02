import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, BehaviorSubject, tap, map, catchError, throwError } from 'rxjs';
import { JobSeeker, Recruiter } from '../types';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8888/auth-service/user';
  private backendUrl = 'http://localhost:8888/auth-service';
  
  private currentProfileSubject = new BehaviorSubject<JobSeeker | Recruiter | null>(null);
  public currentProfile$ = this.currentProfileSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadProfileFromStorage();
  }

  /**
   * Get authentication headers with Bearer token
   */
  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('access_token') || '';
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  /**
   * Get authentication headers for JSON content
   */
  private getAuthHeadersJson(): HttpHeaders {
    const token = localStorage.getItem('access_token') || '';
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  /**
   * Convert relative image path to full URL
   */
  private getFullImageUrl(photoPath: string | undefined): string {
    if (!photoPath) return '';
    
    // If already a full URL, return as is
    if (photoPath.startsWith('http://') || photoPath.startsWith('https://')) {
      return photoPath;
    }
    
    // If starts with /, prepend backend URL
    if (photoPath.startsWith('/')) {
      return `${this.backendUrl}${photoPath}`;
    }
    
    // Otherwise prepend with /
    return `${this.backendUrl}/${photoPath}`;
  }

  /**
   * Convert full URL to relative path for storage
   */
  private getRelativePath(photoUrl: string | undefined): string {
    if (!photoUrl) return '';
    
    // If it's a full URL from our backend, extract the relative path
    if (photoUrl.startsWith(this.backendUrl)) {
      return photoUrl.replace(this.backendUrl, '');
    }
    
    // Otherwise return as is (might already be relative)
    return photoUrl;
  }

  /**
   * Fix photo URLs in profile object (convert to full URLs)
   */
  private fixProfilePhotoUrls(profile: any): any {
    if (!profile) return profile;
    
    return {
      ...profile,
      photo_profil: this.getFullImageUrl(profile.photo_profil)
    };
  }

  /**
   * Prepare profile for storage (convert to relative paths)
   */
  private prepareProfileForStorage(profile: any): any {
    if (!profile) return profile;
    
    return {
      ...profile,
      photo_profil: this.getRelativePath(profile.photo_profil)
    };
  }

  /**
   * GET /user/:keycloakId
   */
  getUserById(keycloakId: string): Observable<any> {
    const headers = this.getAuthHeadersJson();
    
    return this.http.get<any>(`${this.apiUrl}/${keycloakId}`, { headers }).pipe(
      map(profile => this.fixProfilePhotoUrls(profile)),
      catchError(this.handleError)
    );
  }

  /**
   * GET /user/profile - Get current user's profile
   */
  getUserProfile(): Observable<JobSeeker | Recruiter> {
    const headers = this.getAuthHeaders();
    
    return this.http.get<JobSeeker | Recruiter>(`${this.apiUrl}/profile`, { headers }).pipe(
      map(profile => this.fixProfilePhotoUrls(profile)),
      tap(profile => {
        this.currentProfileSubject.next(profile);
        const storageProfile = this.prepareProfileForStorage(profile);
        localStorage.setItem('userProfile', JSON.stringify(storageProfile));
      }),
      catchError(this.handleError)
    );
  }

  /**
   * PUT /user/profile - Update user profile
   */
  updateUserProfile(profileData: Partial<JobSeeker | Recruiter>): Observable<JobSeeker | Recruiter> {
    const headers = this.getAuthHeadersJson();
    
    return this.http.put<JobSeeker | Recruiter>(
      `${this.apiUrl}/profile`,
      profileData,
      { headers }
    ).pipe(
      map(updatedProfile => this.fixProfilePhotoUrls(updatedProfile)),
      tap(updatedProfile => {
        this.currentProfileSubject.next(updatedProfile);
        
        // Store with relative path in localStorage
        const storageProfile = this.prepareProfileForStorage(updatedProfile);
        localStorage.setItem('userProfile', JSON.stringify(storageProfile));
        
        // Update user object in localStorage
        this.updateUserInLocalStorage(updatedProfile);
      }),
      catchError(this.handleError)
    );
  }

  /**
   * POST /user/upload-photo - Upload profile photo
   * Note: FormData automatically sets correct Content-Type with boundary
   */
  uploadProfilePhoto(formData: FormData): Observable<{ url: string; message: string }> {
    const token = localStorage.getItem('access_token') || '';
    
    // Don't set Content-Type header - let browser set it with boundary
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    
    return this.http.post<{ url: string; message: string }>(
      `${this.apiUrl}/upload-photo`,
      formData,
      { headers }
    ).pipe(
      map(response => ({
        ...response,
        url: this.getFullImageUrl(response.url)
      })),
      tap(response => {
        console.log('Photo uploaded successfully:', response.url);
      }),
      catchError(this.handleError)
    );
  }

  /**
   * Load profile from localStorage on service initialization
   */
  private loadProfileFromStorage(): void {
    const profileStr = localStorage.getItem('userProfile');
    if (profileStr) {
      try {
        const profile = JSON.parse(profileStr);
        const fixedProfile = this.fixProfilePhotoUrls(profile);
        this.currentProfileSubject.next(fixedProfile);
      } catch (e) {
        console.error('Failed to parse profile from localStorage:', e);
        localStorage.removeItem('userProfile');
      }
    }
  }

  /**
   * Update user object in localStorage (for nav bar, etc.)
   */
  private updateUserInLocalStorage(profile: any): void {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      try {
        const user = JSON.parse(userStr);
        user.fullName = profile.fullName;
        user.email = profile.email;
        if (profile.photo_profil) {
          user.photo_profil = this.getRelativePath(profile.photo_profil);
        }
        localStorage.setItem('user', JSON.stringify(user));
      } catch (e) {
        console.error('Error updating user in localStorage:', e);
      }
    }
  }

  /**
   * Get current profile from BehaviorSubject (synchronous)
   */
  getCurrentProfile(): JobSeeker | Recruiter | null {
    return this.currentProfileSubject.value;
  }

  /**
   * Clear profile data from service and localStorage
   */
  clearProfile(): void {
    localStorage.removeItem('userProfile');
    this.currentProfileSubject.next(null);
  }

  /**
   * Public method to get full image URL (for components)
   */
  getImageUrl(photoPath: string | undefined): string {
    return this.getFullImageUrl(photoPath);
  }

  /**
   * Centralized error handler
   */
  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An error occurred';
    
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Server-side error
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
      
      if (error.error?.message) {
        errorMessage = error.error.message;
      }
    }
    
    console.error('UserService Error:', errorMessage);
    return throwError(() => new Error(errorMessage));
  }

  /**
   * GET /user/recruiters/list - Get list of recruiters (max 5) for job seekers
   */
  getRecruitersForJobSeekers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/recruiters/list`).pipe(
      map(recruiters => 
        recruiters.map(recruiter => ({
          ...recruiter,
          logo: this.getFullImageUrl(recruiter.logo)
        }))
      ),
      catchError(this.handleError)
    );
  }
}