import { Component, EventEmitter, Output, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

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

  email: string = '';
  password: string = '';

  onLogin() {
    this.loginSubmit.emit({
      email: this.email,
      password: this.password
    });
  }

  closeErrorModal() {
    this.errors = [];
  }
}