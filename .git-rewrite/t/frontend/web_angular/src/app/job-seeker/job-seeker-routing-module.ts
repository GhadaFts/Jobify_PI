import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProfileInitial } from './profile-initial/profile-initial';
import { Dashboard } from './dashboard/dashboard';
import { CvCorrection } from './dashboard/cv-correction/cv-correction';
import { FindJob } from './dashboard/find-job/find-job';
import { JobAnalyse } from './dashboard/job-analyse/job-analyse';
import { EditProfile } from './dashboard/edit-profile/edit-profile';

const routes: Routes = [
  { path: 'profile-initial', component: ProfileInitial },
  { path: 'dashboard', component: Dashboard, children: [
    { path: 'cv-correction', component: CvCorrection },
    { path: 'find-job', component: FindJob },
    { path: 'job-analyse', component: JobAnalyse },
    { path: 'edit-profile', component: EditProfile },

    { path: '', redirectTo: 'find-job', pathMatch: 'full' } // Default child route
  ]},
  { path: '', redirectTo: 'profile-initial', pathMatch: 'full' } // Default route for job-seeker
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class JobSeekerRoutingModule { }