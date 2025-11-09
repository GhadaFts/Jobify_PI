import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService, LoginCredentials } from '../services/auth.service';
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
  // Login form data
  email: string = '';
  password: string = '';
  isLoading: boolean = false;
  errors: string[] = [];

  // Focus states for floating labels
  emailFocused: boolean = false;
  passwordFocused: boolean = false;
  showPassword: boolean = false;

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

  onForgotPassword() {
    if (this.email) {
      this.isLoading = true;
      this.authService.forgotPassword(this.email).subscribe({
        next: (response) => {
          this.isLoading = false;
          if (response.success) {
            alert(response.message);
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

  navigateToSignup() {
    this.router.navigate(['/signup']);
  }
  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }
}