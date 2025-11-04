import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService, SignupData, LoginCredentials } from '../services/auth.service'; // ✅ Import LoginCredentials
import { ErrorService } from '../services/error.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './signup.html',
  styleUrls: ['./signup.scss']
})
export class SignupComponent {
  @Output() signupSubmit = new EventEmitter<any>();
  @Output() navigateToLogin = new EventEmitter<void>();

  // Signup form data
  signupData = {
    fullName: '',
    email: '',
    password: '',
    confirmPassword: '',
    role: 'jobseeker'
  };
  errors: string[] = [];
  isLoading: boolean = false;

  // ✅ Add login form data for the hidden form
  loginData = {
    email: '',
    password: ''
  };
  loginErrors: string[] = [];
  isLoginLoading: boolean = false;

  constructor(
    private authService: AuthService,
    private errorService: ErrorService,
    private router: Router
  ) {}

  onSignup() {
    this.isLoading = true;
    this.errors = [];

    if (this.passwordMismatch()) {
      this.errors.push('Passwords do not match');
      this.isLoading = false;
      return;
    }

    if (this.passwordTooShort()) {
      this.errors.push('Password must be at least 6 characters long');
      this.isLoading = false;
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
        this.isLoading = false;
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
        this.isLoading = false;
        this.errors = this.errorService.handleAuthError(error);
        console.error('Signup failed:', error);
      }
    });
  }

  // ✅ Add login method for the hidden form
  onLogin() {
    this.isLoginLoading = true;
    this.loginErrors = [];

    const credentials: LoginCredentials = {
      email: this.loginData.email,
      password: this.loginData.password
    };

    this.authService.login(credentials).subscribe({
      next: (response) => {
        this.isLoginLoading = false;
        if (response.success) {
          console.log('Login successful:', response);
          if (response.user.role === 'recruiter') {
            this.router.navigate(['/recruiter/dashboard/publish-job']);
          } else {
            this.router.navigate(['/jobseeker/dashboard/find-job']);
          }
        }
      },
      error: (error) => {
        this.isLoginLoading = false;
        this.loginErrors = this.errorService.handleAuthError(error);
        console.error('Login failed:', error);
      }
    });
  }

  onLoginNavigate() {
    this.navigateToLogin.emit();
  }

  private passwordMismatch(): boolean {
    return this.signupData.password !== this.signupData.confirmPassword;
  }

  private passwordTooShort(): boolean {
    return this.signupData.password.length < 6;
  }
}