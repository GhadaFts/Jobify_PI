import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, filter, take, switchMap, tap } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Add access token to request if available
    const accessToken = this.authService.getAccessToken();
    
    // LOG 1: Vérifier si on a un token
    console.log('🔍 Interceptor - URL:', request.url);
    console.log('🔍 Interceptor - Has token:', !!accessToken);
    
    if (accessToken) {
      console.log('🔍 Interceptor - Token preview:', accessToken.substring(0, 50) + '...');
      request = this.addToken(request, accessToken);
    } else {
      console.warn('⚠️ Interceptor - No access token found!');
    }

    return next.handle(request).pipe(
      tap(event => {
        console.log('✅ Request successful:', request.url);
      }),
      catchError(error => {
        console.error('❌ Request failed:', request.url);
        console.error('❌ Error status:', error.status);
        console.error('❌ Error message:', error.message);
        console.error('❌ Full error:', error);
        
        if (error instanceof HttpErrorResponse && error.status === 401) {
          console.log('🔄 Attempting token refresh...');
          return this.handle401Error(request, next);
        }
        return throwError(() => error);
      })
    );
  }

  private addToken(request: HttpRequest<any>, token: string): HttpRequest<any> {
    const clonedRequest = request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    
    console.log('📤 Headers added:', clonedRequest.headers.get('Authorization')?.substring(0, 30) + '...');
    return clonedRequest;
  }

  private handle401Error(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);

      const refreshToken = this.authService.getRefreshToken();

      if (refreshToken) {
        console.log('🔄 Refreshing token...');
        return this.authService.refreshToken().pipe(
          switchMap((response: any) => {
            this.isRefreshing = false;
            this.refreshTokenSubject.next(response.accessToken);
            console.log('✅ Token refreshed successfully');
            return next.handle(this.addToken(request, response.accessToken));
          }),
          catchError((err) => {
            this.isRefreshing = false;
            console.error('❌ Token refresh failed:', err);
            // If refresh fails, logout user
            this.authService.logout().subscribe();
            this.router.navigate(['/login']);
            return throwError(() => err);
          })
        );
      } else {
        // No refresh token, logout user
        this.isRefreshing = false;
        console.error('❌ No refresh token available');
        this.authService.logout().subscribe();
        this.router.navigate(['/login']);
        return throwError(() => new Error('No refresh token available'));
      }
    } else {
      // Wait for refresh to complete
      console.log('⏳ Waiting for token refresh...');
      return this.refreshTokenSubject.pipe(
        filter(token => token != null),
        take(1),
        switchMap(token => {
          return next.handle(this.addToken(request, token));
        })
      );
    }
  }
}