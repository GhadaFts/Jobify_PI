import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProfileInitial } from './profile-initial/profile-initial';
import { Dashboard } from './dashboard/dashboard';
import { CvCorrection } from './dashboard/cv-correction/cv-correction';
import { FindJob } from './dashboard/find-job/find-job';
import { JobAnalyse } from './dashboard/job-analyse/job-analyse';
import { EditProfile } from './dashboard/edit-profile/edit-profile';
import { InterviewPreparation } from './dashboard/interview-preparation/interview-preparation';
import { AuthGuard } from '../guards/auth.guard';

const routes: Routes = [
  { 
    path: 'profile-initial', 
    component: ProfileInitial,
    canActivate: [AuthGuard],
    data: { role: 'job_seeker' }
  },
  { 
    path: 'dashboard', 
    component: Dashboard,
    canActivate: [AuthGuard],
    data: { role: 'job_seeker' },
    children: [
      { path: 'cv-correction', component: CvCorrection },
      { path: 'find-job', component: FindJob },
      { path: 'job-analyse', component: JobAnalyse },
      { path: 'edit-profile', component: EditProfile },
      { path: 'interview-preparation', component: InterviewPreparation },
      { path: '', redirectTo: 'find-job', pathMatch: 'full' }
    ]
  },
  { path: '', redirectTo: 'profile-initial', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class JobSeekerRoutingModule { }