import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Recruiter } from './recruiter';
import { ProfileInitial } from './profile-initial/profile-initial';

const routes: Routes = [
  { path: 'profile-initial', component: ProfileInitial },
  { path: '', component: Recruiter }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RecruiterRoutingModule { }
