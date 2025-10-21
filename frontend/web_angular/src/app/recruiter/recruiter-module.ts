import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // Required for ngModel and ngForm
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome'; // Required for fa-icon
import { ProfileInitial} from './profile-initial/profile-initial';
import { Dashboard } from './dashboard/dashboard';
import { RecruiterRoutingModule } from './recruiter-routing-module';

@NgModule({
  declarations: [
    ProfileInitial,
    Dashboard
  ],
  imports: [
    CommonModule,
    FormsModule, // Add this for template-driven forms
    FontAwesomeModule, // Add this for FontAwesome icons
    RecruiterRoutingModule
    // Add SharedModule if needed for ToastService or other shared components
  ],
  exports: []
})
export class RecruiterModule { }