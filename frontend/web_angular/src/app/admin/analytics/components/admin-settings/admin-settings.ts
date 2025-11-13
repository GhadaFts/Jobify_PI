import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-settings',
  templateUrl: './admin-settings.html',
  styleUrls: ['./admin-settings.scss'],
  standalone: false
})
export class AdminSettingsComponent implements OnInit {
  passwordForm!: FormGroup;
  showPassword = false;
  showNewPassword = false;
  showConfirmPassword = false;
  submitted = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private formBuilder: FormBuilder,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initializeForm();
  }

  initializeForm(): void {
    this.passwordForm = this.formBuilder.group({
      currentPassword: ['', [Validators.required]],
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]]
    }, {
      validators: this.passwordMatchValidator
    });
  }

  passwordMatchValidator(group: FormGroup): { [key: string]: boolean } | null {
    const newPassword = group.get('newPassword');
    const confirmPassword = group.get('confirmPassword');

    if (newPassword && confirmPassword && newPassword.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    return null;
  }

  togglePasswordVisibility(field: string): void {
    if (field === 'current') this.showPassword = !this.showPassword;
    if (field === 'new') this.showNewPassword = !this.showNewPassword;
    if (field === 'confirm') this.showConfirmPassword = !this.showConfirmPassword;
  }

  onSubmit(): void {
    this.submitted = true;
    this.successMessage = '';
    this.errorMessage = '';

    if (this.passwordForm.invalid) {
      this.errorMessage = 'Please fill in all fields correctly.';
      return;
    }

    // Simulate password change API call
    const formData = this.passwordForm.value;
    console.log('Password change request:', {
      currentPassword: formData.currentPassword,
      newPassword: formData.newPassword
    });

    // Simulate success
    this.successMessage = 'Password changed successfully!';
    this.passwordForm.reset();
    this.submitted = false;

    // Redirect after 2 seconds
    setTimeout(() => {
      this.router.navigate(['/admin/analytics']);
    }, 2000);
  }

  goBack(): void {
    this.router.navigate(['/admin/analytics']);
  }
}
