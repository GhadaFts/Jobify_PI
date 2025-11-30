import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap, map } from 'rxjs';
import { JobSeeker, Recruiter } from '../types';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8888/auth-service/user'; // Gateway URL
  private backendUrl = 'http://localhost:8888/auth-service'; 
  private currentProfileSubject = new BehaviorSubject<JobSeeker | Recruiter | null>(null);
  public currentProfile$ = this.currentProfileSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadProfileFromStorage();
  }

  /**
   * Helper: Convert relative image path to full URL
   */
  private getFullImageUrl(photoPath: string | undefined): string {
    if (!photoPath) return '';
    
    // If already a full URL, return as is
    if (photoPath.startsWith('http://') || photoPath.startsWith('https://')) {
      return photoPath;
    }
    
    // Otherwise prepend backend URL
    return `${this.backendUrl}${photoPath}`;
  }

  /**
   * Helper: Fix photo URLs in profile object
   */
  private fixProfilePhotoUrls(profile: any): any {
    if (!profile) return profile;
    
    return {
      ...profile,
      photo_profil: this.getFullImageUrl(profile.photo_profil)
    };
  }

  /**
   * Get user profile from backend
   */
  getUserProfile(): Observable<JobSeeker | Recruiter> {
    return this.http.get<JobSeeker | Recruiter>(`${this.apiUrl}/profile`).pipe(
      map(profile => this.fixProfilePhotoUrls(profile)),
      tap(profile => {
        this.currentProfileSubject.next(profile);
        // Store with relative path in localStorage
        const storageProfile = { ...profile, photo_profil: profile.photo_profil?.replace(this.backendUrl, '') };
        localStorage.setItem('userProfile', JSON.stringify(storageProfile));
      })
    );
  }

  /**
   * Update user profile
   */
  updateUserProfile(profileData: Partial<JobSeeker | Recruiter>): Observable<JobSeeker | Recruiter> {
    return this.http.put<JobSeeker | Recruiter>(`${this.apiUrl}/profile`, profileData).pipe(
      map(updatedProfile => this.fixProfilePhotoUrls(updatedProfile)),
      tap(updatedProfile => {
        this.currentProfileSubject.next(updatedProfile);
        // Store with relative path in localStorage
        const storageProfile = { ...updatedProfile, photo_profil: updatedProfile.photo_profil?.replace(this.backendUrl, '') };
        localStorage.setItem('userProfile', JSON.stringify(storageProfile));
        
        // Also update the user object in localStorage if it exists
        const userStr = localStorage.getItem('user');
        if (userStr) {
          try {
            const user = JSON.parse(userStr);
            user.fullName = updatedProfile.fullName;
            user.email = updatedProfile.email;
            localStorage.setItem('user', JSON.stringify(user));
          } catch (e) {
            console.error('Error updating user in localStorage:', e);
          }
        }
      })
    );
  }

  /**
   * Upload profile photo
   */
  uploadProfilePhoto(formData: FormData): Observable<{ url: string; message: string }> {
    return this.http.post<{ url: string; message: string }>(
      `${this.apiUrl}/upload-photo`,
      formData
    ).pipe(
      map(response => ({
        ...response,
        url: this.getFullImageUrl(response.url) // Convert to full URL
      }))
    );
  }

  /**
   * Load profile from localStorage
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
      }
    }
  }

  /**
   * Get current profile from BehaviorSubject
   */
  getCurrentProfile(): JobSeeker | Recruiter | null {
    return this.currentProfileSubject.value;
  }

  /**
   * Clear profile data
   */
  clearProfile(): void {
    localStorage.removeItem('userProfile');
    this.currentProfileSubject.next(null);
  }

  /**
   * Get full image URL (public method for components to use)
   */
  getImageUrl(photoPath: string | undefined): string {
    return this.getFullImageUrl(photoPath);
  }
}