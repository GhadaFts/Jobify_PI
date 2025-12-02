import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService, LoginCredentials } from '../../services/auth.service';
import { ErrorService } from '../../services/error.service';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.scss']
})
export class LoginComponent {
  // Login form data
  email: string = '';
  password: string = '';
  isLoading: boolean = false;
  errors: string[] = [];

  // Focus states for floating labels
  emailFocused: boolean = false;
  passwordFocused: boolean = false;
  showPassword: boolean = false;

  // Return URL for redirect after login
  private returnUrl: string = '';

  constructor(
    private authService: AuthService,
    private errorService: ErrorService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    // Get return URL from route parameters or default to '/'
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  ngOnInit(): void {
    // Don't clear auth data here - let the auth service handle it
    // If user is already logged in, redirect them
    if (this.authService.isAuthenticated()) {
      const user = this.authService.getCurrentUser();
      this.redirectBasedOnRole(user?.role);
    }
  }

  onLogin(): void {
    // Clear previous errors
    this.errors = [];

    // Basic validation
    if (!this.email || !this.password) {
      this.errors = ['Please enter both email and password'];
      return;
    }

    // Email format validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.email)) {
      this.errors = ['Please enter a valid email address'];
      return;
    }

    // Clear any stale auth data before attempting new login
    console.log('🧹 Clearing any stale auth data before login');
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('user');
    localStorage.removeItem('userProfile');

    this.isLoading = true;

    const credentials: LoginCredentials = {
      email: this.email.trim(),
      password: this.password
    };

    console.log('🔐 Attempting login with:', credentials.email);

    this.authService.login(credentials).subscribe({
      next: (response) => {
        this.isLoading = false;
        console.log('Login successful:', response);

        // Fetch user profile and redirect
        this.authService.getUserProfile().subscribe({
          next: (user) => {
            console.log('User profile loaded:', user);
            this.redirectBasedOnRole(user.role);
          },
          error: (error) => {
            console.error('Failed to load user profile:', error);
            // Still redirect even if profile load fails
            this.router.navigate(['/']);
          }
        });
      },
      error: (error) => {
        this.isLoading = false;
        this.errors = this.errorService.handleAuthError(error);
        console.error('Login failed:', error);
      }
    });
  }

  onForgotPassword(): void {
    if (!this.email) {
      this.errors = ['Please enter your email address first'];
      return;
    }

    // Email format validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.email)) {
      this.errors = ['Please enter a valid email address'];
      return;
    }

    this.isLoading = true;
    this.errors = [];

    this.authService.forgotPassword(this.email.trim()).subscribe({
      next: (response) => {
        this.isLoading = false;
        alert(response.message || 'Password reset instructions have been sent to your email');
      },
      error: (error) => {
        this.isLoading = false;
        this.errors = this.errorService.handleAuthError(error);
      }
    });
    
  }

  navigateToSignup(): void {
    this.router.navigate(['/signup'], {
      queryParams: this.returnUrl !== '/' ? { returnUrl: this.returnUrl } : {}
    });
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  /**
   * Redirect user based on their role
   */
  private redirectBasedOnRole(role?: string): void {
    if (this.returnUrl && this.returnUrl !== '/') {
      // If there's a return URL, use it
      this.router.navigate([this.returnUrl]);
    } else {
      // Otherwise, redirect based on role
      switch (role?.toLowerCase()) {
        case 'recruiter':
          this.router.navigate(['/recruiter/dashboard/publish-job']);
          break;
        case 'job_seeker':
        case 'jobseeker':
          this.router.navigate(['/job-seeker/dashboard/find-job']);
          break;
        case 'admin':
          this.router.navigate(['/admin/analytics']);
          break;
        default:
          this.router.navigate(['/dashboard']);
          break;
      }
    }
  }
}