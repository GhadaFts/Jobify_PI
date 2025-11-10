import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ErrorService {
  /**
   * Handle authentication errors
   */
  handleAuthError(error: any): string[] {
    const errors: string[] = [];

    if (error.error?.message) {
      errors.push(error.error.message);
    } else if (error.message) {
      errors.push(error.message);
    } else if (error.status === 0) {
      errors.push('Network error: Please check your internet connection');
    } else if (error.status === 401) {
      errors.push('Unauthorized: Please check your credentials');
    } else if (error.status === 500) {
      errors.push('Server error: Please try again later');
    } else {
      errors.push('An unexpected error occurred');
    }

    return errors;
  }

  /**
   * Handle form validation errors
   */
  handleValidationError(error: any): string[] {
    const errors: string[] = [];

    if (error.error?.errors) {
      // Handle backend validation errors
      const validationErrors = error.error.errors;
      for (const field in validationErrors) {
        if (validationErrors.hasOwnProperty(field)) {
          errors.push(...validationErrors[field]);
        }
      }
    } else {
      errors.push('Please check your input and try again');
    }

    return errors;
  }
}