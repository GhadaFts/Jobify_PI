import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Dashboard } from './dashboard/dashboard';
import { ProfileInitial } from './profile-initial/profile-initial';



@NgModule({
  declarations: [
    Dashboard,
    ProfileInitial
  ],
  imports: [
    CommonModule
  ]
})
export class RecruiterModule { }
