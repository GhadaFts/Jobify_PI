import { NgModule } from '@angular/core';
import { CommonModule as AngularCommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Landing } from './landing/landing'; // Adjusted import
; // Adjusted import
import { Login } from './login/login';
import { Signup } from './signup/signup';
import { Sidebar } from './sidebar/sidebar';

@NgModule({
  declarations: [Landing, Login, Signup, Sidebar],
  imports: [AngularCommonModule, FormsModule],
  exports: [Landing, Login, Signup, Sidebar] // Export for use in AppRoutingModule
})
export class SharedModule { }