import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject, tap , switchMap , of, catchError } from 'rxjs';
import { Router } from '@angular/router';

export interface LoginCredentials {
    email: string;
    password: string;
}

export interface RegisterData {
    fullName: string;
    email: string;
    password: string;
    role?: 'job_seeker' | 'recruiter' | 'admin';
}

export interface AuthResponse {
    keycloakId?: string;
    success?: boolean;
    accessToken?: string;
    refreshToken?: string;
    expiresIn?: number;
    tokenType?: string;
    user?: any;
    message?: string;
}

export interface User {
    id: string;
    keycloakId: string;
    fullName: string;
    email: string;
    role: string;
}

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private apiUrl = 'http://localhost:8888/auth-service/auth'; // Gateway URL
    private currentUserSubject = new BehaviorSubject<User | null>(null);
    public currentUser$ = this.currentUserSubject.asObservable();

    constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.loadUserFromStorage();
    
    // Si un token existe mais que l'utilisateur n'est pas chargé, le charger
    if (this.isAuthenticated() && !this.currentUserSubject.value) {
      this.getUserProfile().subscribe({
        error: () => {
          // Si le token est invalide, nettoyer
          this.clearAuthData();
        }
      });
    }
  }


    /**
     * Login user with email and password
     */
    login(credentials: LoginCredentials): Observable<AuthResponse> {
        return this.http.post<any>(`${this.apiUrl}/login`, credentials).pipe(
            tap(response => {
                if (response.accessToken) {
                    this.handleAuthResponse(response);
                }
            })
        );
    }

    /**
     * Register new user
     */
    register(data: RegisterData): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.apiUrl}/register`, data).pipe(
            switchMap((response) => {
                if (response.keycloakId) {
                    return this.login({
                        email: data.email,
                        password: data.password
                    });
                }
                return of(response);
            })
        );
    }


    /**
     * Refresh access token
     */
    refreshToken(): Observable<AuthResponse> {
        const refreshToken = this.getRefreshToken();
        if (!refreshToken) {
            throw new Error('No refresh token available');
        }

        return this.http.post<any>(`${this.apiUrl}/refresh`, { refreshToken }).pipe(
            tap(response => {
                if (response.accessToken) {
                    this.setTokens(response.accessToken, response.refreshToken);
                }
            })
        );
    }

    /**
     * Logout user
     */
    logout(): Observable<any> {
        const refreshToken = this.getRefreshToken();

        // Clear local auth state immediately so stale tokens are not reused
        this.clearAuthData();
        this.router.navigate(['/home']);

        // If there's no refresh token or remote logout will likely fail (expired access token),
        // still return a resolved observable so callers can continue.
        if (!refreshToken) {
            return of({ message: 'Logged out locally' });
        }

        return this.http.post(`${this.apiUrl}/logout`, { refreshToken }).pipe(
            tap(() => {
                // remote logout succeeded - nothing more to do
            }),
            catchError(err => {
                // Remote logout failed (likely because access token expired). We've already cleared
                // local auth data above, so swallow the error and return a resolved observable.
                console.warn('Remote logout failed, continuing local logout', err);
                return of({ error: true });
            })
        );
    }

    /**
     * Get user profile
     */
    getUserProfile(): Observable<User> {
        return this.http.get<User>(`${this.apiUrl}/profile`).pipe(
            tap(user => {
                this.currentUserSubject.next(user);
                localStorage.setItem('user', JSON.stringify(user));
            })
        );
    }

    /**
     * Check if user exists by email
     */
    checkUserExists(email: string): Observable<{ exists: boolean }> {
        return this.http.get<{ exists: boolean }>(`${this.apiUrl}/user/exists/${email}`);
    }

    /**
     * Forgot password (placeholder - implement based on your backend)
     */
    forgotPassword(email: string): Observable<any> {
        // This endpoint needs to be implemented in your backend
        return this.http.post(`${this.apiUrl}/forgot-password`, { email });
    }

    /**
     * Handle authentication response
     */
    private handleAuthResponse(response: any): void {
        this.setTokens(response.accessToken, response.refreshToken);

        // Fetch user profile after login
        this.getUserProfile().subscribe({
            next: (user) => {
                console.log('User profile loaded:', user);
            },
            error: (error) => {
                console.error('Failed to load user profile:', error);
            }
        });
    }

    /**
     * Store tokens in localStorage
     */
    private setTokens(accessToken: string, refreshToken?: string): void {
        localStorage.setItem('access_token', accessToken);
        if (refreshToken) {
            localStorage.setItem('refresh_token', refreshToken);
        }
    }

    /**
     * Get access token
     */
    getAccessToken(): string | null {
        return localStorage.getItem('access_token');
    }

    /**
     * Get refresh token
     */
    getRefreshToken(): string | null {
        return localStorage.getItem('refresh_token');
    }

    /**
     * Check if user is authenticated
     */
    isAuthenticated(): boolean {
        return !!this.getAccessToken();
    }

    /**
     * Get current user
     */
    getCurrentUser(): User | null {
        return this.currentUserSubject.value;
    }

    /**
     * Load user from localStorage
     */
    private loadUserFromStorage(): void {
        const userStr = localStorage.getItem('user');
        if (userStr) {
            try {
                const user = JSON.parse(userStr);
                this.currentUserSubject.next(user);
            } catch (e) {
                console.error('Failed to parse user from localStorage:', e);
            }
        }
    }

    /**
     * Clear all authentication data
     */
    private clearAuthData(): void {
        localStorage.removeItem('access_token');
        localStorage.removeItem('refresh_token');
        localStorage.removeItem('user');
        this.currentUserSubject.next(null);
    }
    
}