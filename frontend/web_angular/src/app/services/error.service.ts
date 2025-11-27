import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ErrorService {
  constructor() {}

  /**
   * Handle authentication errors
   */
  handleAuthError(error: any): string[] {
    const errors: string[] = [];

    if (error instanceof HttpErrorResponse) {
      // Server-side error
      if (error.status === 0) {
        errors.push('Unable to connect to the server. Please check your internet connection.');
      } else if (error.status === 401) {
        errors.push('Invalid email or password. Please try again.');
      } else if (error.status === 403) {
        errors.push('Access denied. Your account may be disabled.');
      } else if (error.status === 404) {
        errors.push('Service not found. Please contact support.');
      } else if (error.status === 409) {
        errors.push('User already exists with this email.');
      } else if (error.status === 422) {
        // Validation errors
        if (error.error?.message) {
          if (Array.isArray(error.error.message)) {
            errors.push(...error.error.message);
          } else {
            errors.push(error.error.message);
          }
        } else {
          errors.push('Validation error. Please check your input.');
        }
      } else if (error.status === 500) {
        errors.push('Server error. Please try again later.');
      } else {
        // Extract error message from response
        const message = error.error?.message || error.error?.errorMessage || error.message;
        errors.push(message || 'An unexpected error occurred. Please try again.');
      }
    } else if (error.error instanceof ErrorEvent) {
      // Client-side error
      errors.push(`Error: ${error.error.message}`);
    } else if (typeof error === 'string') {
      errors.push(error);
    } else {
      errors.push('An unexpected error occurred. Please try again.');
    }

    return errors;
  }

  /**
   * Handle general API errors
   */
  handleApiError(error: any): string {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 0) {
        return 'Unable to connect to the server. Please check your internet connection.';
      }
      return error.error?.message || error.message || 'An error occurred while processing your request.';
    }
    return 'An unexpected error occurred. Please try again.';
  }

  /**
   * Handle validation errors
   */
  handleValidationErrors(error: any): { [key: string]: string[] } {
    const validationErrors: { [key: string]: string[] } = {};

    if (error instanceof HttpErrorResponse && error.status === 422) {
      const errorData = error.error;
      
      if (errorData?.errors) {
        // Format: { errors: { field: ['error1', 'error2'] } }
        Object.keys(errorData.errors).forEach(field => {
          validationErrors[field] = Array.isArray(errorData.errors[field])
            ? errorData.errors[field]
            : [errorData.errors[field]];
        });
      } else if (Array.isArray(errorData?.message)) {
        // Format: { message: ['error1', 'error2'] }
        validationErrors['general'] = errorData.message;
      }
    }

    return validationErrors;
  }
}