import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { JobSeekerRoutingModule } from './job-seeker-routing-module';
import { JobSeeker } from './job-seeker';
import { ProfileInitial } from './profile-initial/profile-initial';
import { FormsModule } from '@angular/forms';
import { Dashboard } from './dashboard/dashboard';
import { CvCorrection } from './dashboard/cv-correction/cv-correction';
import { FindJob } from './dashboard/find-job/find-job';
import { JobAnalyse } from './dashboard/job-analyse/job-analyse';
import { SharedModule } from '../shared/shared-module';


@NgModule({
  declarations: [
    JobSeeker,
    ProfileInitial,
    Dashboard,
    CvCorrection,
    FindJob,
    JobAnalyse
  ],
  imports: [
    CommonModule,
    JobSeekerRoutingModule,
    FormsModule,
    SharedModule
  ]
})
export class JobSeekerModule { }
