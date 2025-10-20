import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogModule } from '@angular/material/dialog';


import { JobSeekerRoutingModule } from './job-seeker-routing-module';
import { JobSeeker } from './job-seeker';
import { ProfileInitial } from './profile-initial/profile-initial';
import { FormsModule } from '@angular/forms';
import { Dashboard } from './dashboard/dashboard';
import { CvCorrection } from './dashboard/cv-correction/cv-correction';
import { FindJob } from './dashboard/find-job/find-job';
import { JobAnalyse } from './dashboard/job-analyse/job-analyse';
import { SharedModule } from '../shared/shared-module';
import { JobCard } from './dashboard/find-job/job-card/job-card';
import { JobSeekerSidebar } from './dashboard/job-seeker-sidebar/job-seeker-sidebar';
import { JobDetailsDialog } from './dashboard/find-job/job-card/job-details-dialog/job-details-dialog';
import { ApplicationDialog } from './dashboard/find-job/job-card/application-dialog/application-dialog';


@NgModule({
  declarations: [
    JobSeeker,
    ProfileInitial,
    Dashboard,
    CvCorrection,
    FindJob,
    JobAnalyse,
    JobCard,
    JobSeekerSidebar,
    JobDetailsDialog,
    ApplicationDialog
  ],
  imports: [
    CommonModule,
    JobSeekerRoutingModule,
    FormsModule,
    SharedModule,
    MatDialogModule
  ]
})
export class JobSeekerModule { }
