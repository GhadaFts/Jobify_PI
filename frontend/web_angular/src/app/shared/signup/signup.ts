import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

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

  signupData = {
    firstName: '',
    lastName: '',
    address: '',
    phoneNumber: '',
    email: '',
    password: ''
  };

  onSignup() {
    this.signupSubmit.emit(this.signupData);
  }

  onLoginNavigate() {
    this.navigateToLogin.emit();
  }
}