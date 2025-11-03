import { Component, EventEmitter, Output, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService, LoginCredentials, SignupData } from '../services/auth.service'; // ✅ Import SignupData
import { ErrorService } from '../services/error.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.scss']
})
export class LoginComponent {
  @Input() errors: string[] = [];
  @Output() loginSubmit = new EventEmitter<{ email: string; password: string }>();
  @Output() forgotPassword = new EventEmitter<string>();

  // Login form data
  email: string = '';
  password: string = '';
  isLoading: boolean = false;

  // ✅ Add signup form data for the hidden form
  signupData = {
    fullName: '',
    email: '',
    password: '',
    confirmPassword: '',
    role: 'jobseeker'
  };
  signupErrors: string[] = [];
  isSignupLoading: boolean = false;

  constructor(
    private authService: AuthService,
    private errorService: ErrorService,
    private router: Router
  ) {}

  onLogin() {
    this.isLoading = true;
    this.errors = [];

    const credentials: LoginCredentials = {
      email: this.email,
      password: this.password
    };

    this.authService.login(credentials).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success) {
          console.log('Login successful:', response);
          if (response.user.role == 'recruiter') {
            this.router.navigate(['/recruiter/dashboard/publish-job']);
          } else {
            this.router.navigate(['/job-seeker/dashboard/find-job']);
          }
        }
      },
      error: (error) => {
        this.isLoading = false;
        this.errors = this.errorService.handleAuthError(error);
        console.error('Login failed:', error);
      }
    });
  }

  // ✅ Add signup method for the hidden form
  onSignup() {
    this.isSignupLoading = true;
    this.signupErrors = [];

    // Frontend validation
    if (this.signupData.password !== this.signupData.confirmPassword) {
      this.signupErrors.push('Passwords do not match');
      this.isSignupLoading = false;
      return;
    }

    if (this.signupData.password.length < 6) {
      this.signupErrors.push('Password must be at least 6 characters long');
      this.isSignupLoading = false;
      return;
    }

    const backendData: SignupData = {
      fullName: this.signupData.fullName,
      email: this.signupData.email,
      password: this.signupData.password,
      role: this.signupData.role
    };

    this.authService.signup(backendData).subscribe({
      next: (response) => {
        this.isSignupLoading = false;
        if (response.success) {
          console.log('Signup successful:', response);
          if (this.signupData.role === 'jobseeker') {
            this.router.navigate(['/job-seeker/profile-initial']);
          } else if (this.signupData.role === 'recruiter') {
            this.router.navigate(['/recruiter/profile-initial']);
          }
        }
      },
      error: (error) => {
        this.isSignupLoading = false;
        this.signupErrors = this.errorService.handleAuthError(error);
        console.error('Signup failed:', error);
      }
    });
  }

  onForgotPassword() {
    if (this.email) {
      this.isLoading = true;
      this.authService.forgotPassword(this.email).subscribe({
        next: (response) => {
          this.isLoading = false;
          if (response.success) {
            alert(response.message);
            this.forgotPassword.emit(this.email);
          }
        },
        error: (error) => {
          this.isLoading = false;
          this.errors = this.errorService.handleAuthError(error);
        }
      });
    } else {
      this.errors = ['Please enter your email address first'];
    }
  }

  closeErrorModal() {
    this.errors = [];
  }
}