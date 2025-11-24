import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService, SignupData } from '../services/auth.service';
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

  // Focus states for floating labels
  fullNameFocused: boolean = false;
  emailFocused: boolean = false;
  passwordFocused: boolean = false;
  confirmPasswordFocused: boolean = false;
  showPassword: boolean = false;
  showConfirmPassword: boolean = false;

  constructor(
    private authService: AuthService,
    private errorService: ErrorService,
    private router: Router
  ) {}

   togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPasswordVisibility() {
    this.showConfirmPassword = !this.showConfirmPassword;
  }
  
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

  navigateToLogin() {
    this.router.navigate(['/login']);
  }

  private passwordMismatch(): boolean {
    return this.signupData.password !== this.signupData.confirmPassword;
  }

  private passwordTooShort(): boolean {
    return this.signupData.password.length < 6;
  }
}