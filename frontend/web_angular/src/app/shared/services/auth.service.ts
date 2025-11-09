import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { Fullscreen } from 'lucide-angular';

// Interfaces for type safety
export interface LoginCredentials {
  email: string;
  password: string;
}

export interface SignupData {
  fullName?: string;
  
  email: string;
  password: string;
  role?: string; 
}

export interface AuthResponse {
  success: boolean;
  message?: string;
  token?: string;
  user?: any;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:3000/api'; // Update with your backend URL
  
  // Mock user storage (remove when backend is ready)
  private mockUsers = [
    { email: 'test@example.com', password: 'password123', Fullname: 'Test User', role: 'jobseeker' }
  ];

  constructor(private http: HttpClient) {}

  /**
   * Login user
   */
  login(credentials: LoginCredentials): Observable<AuthResponse> {
    // TODO: Replace with actual API call
    console.log('Login attempt:', credentials);
    
    // Mock implementation
    const user = this.mockUsers.find(u => 
      u.email === credentials.email && u.password === credentials.password
    );

    if (user) {
      const mockResponse: AuthResponse = {
        success: true,
        message: 'Login successful',
        token: 'mock-jwt-token',
        user: { ...user, password: undefined } // Remove password from response
      };
      return of(mockResponse).pipe(
        tap(response => this.storeAuthData(response))
      );
    } else {
      const errorResponse: AuthResponse = {
        success: false,
        message: 'Invalid email or password'
      };
      return throwError(() => errorResponse);
    }

    // Real implementation (uncomment when backend is ready):
    // return this.http.post<AuthResponse>(`${this.apiUrl}/auth/login`, credentials)
    //   .pipe(
    //     tap(response => {
    //       if (response.success && response.token) {
    //         this.storeAuthData(response);
    //       }
    //     }),
    //     catchError(error => {
    //       console.error('Login error:', error);
    //       return throwError(() => error);
    //     })
    //   );
  }

  /**
 * Register new user and automatically log them in
 */
signup(userData: SignupData): Observable<AuthResponse> {
  // TODO: Replace with actual API call
  console.log('Signup attempt:', userData);
  
  // Mock implementation
  const userExists = this.mockUsers.find(u => u.email === userData.email);
  
  if (userExists) {
    const errorResponse: AuthResponse = {
      success: false,
      message: 'User already exists with this email'
    };
    return throwError(() => errorResponse);
  }

  // Add to mock users
  const newUser = {
    email: userData.email,
    password: userData.password,
    Fullname: userData.fullName || 'New User', // ✅ Use consistent property name
    role: userData.role || 'jobseeker'
  };
  
  this.mockUsers.push(newUser);
  
  // ✅ Automatically log in the new user
  const loginCredentials: LoginCredentials = {
    email: userData.email,
    password: userData.password
  };
  
  return this.login(loginCredentials); // This will return the login response with token
}

  /**
   * Forgot password
   */
  forgotPassword(email: string): Observable<AuthResponse> {
    // TODO: Replace with actual API call
    console.log('Forgot password request for:', email);
    
    // Mock implementation
    const successResponse: AuthResponse = {
      success: true,
      message: 'Password reset instructions sent to your email'
    };
    
    return of(successResponse);

    // Real implementation (uncomment when backend is ready):
    // return this.http.post<AuthResponse>(`${this.apiUrl}/auth/forgot-password`, { email })
    //   .pipe(
    //     catchError(error => {
    //       console.error('Forgot password error:', error);
    //       return throwError(() => error);
    //     })
    //   );
  }

  /**
   * Store authentication data
   */
  private storeAuthData(authData: AuthResponse): void {
    if (authData.token) {
      localStorage.setItem('authToken', authData.token);
    }
    if (authData.user) {
      localStorage.setItem('userData', JSON.stringify(authData.user));
    }
  }

  /**
   * Get stored token
   */
  getToken(): string | null {
    return localStorage.getItem('authToken');
  }

  /**
   * Get user data
   */
  getUser(): any {
    const userData = localStorage.getItem('userData');
    return userData ? JSON.parse(userData) : null;
  }

  /**
   * Check if user is logged in
   */
  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  /**
   * Logout user
   */
  logout(): void {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userData');
  }
}