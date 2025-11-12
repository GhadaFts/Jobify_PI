import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogModule } from '@angular/material/dialog';
import { LucideAngularModule,  Save, Plus, X, Camera, Trash2  } from 'lucide-angular';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';


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
import { EditProfile } from './dashboard/edit-profile/edit-profile';
import { AppliedJobCard } from './dashboard/find-job/applied-job-card/applied-job-card';
import { InterviewPreparation } from './dashboard/interview-preparation/interview-preparation';
import { ChatbotLogo } from './dashboard/interview-preparation/chatbot-logo/chatbot-logo';
import { Chat } from './dashboard/interview-preparation/chat/chat';




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
    ApplicationDialog,
    EditProfile,
    AppliedJobCard,
    InterviewPreparation,
    ChatbotLogo,
    Chat,
    
    
  ],
  imports: [
    CommonModule,
    JobSeekerRoutingModule,
    FormsModule,
    SharedModule,
    MatDialogModule,
    LucideAngularModule,  
    FontAwesomeModule,
    LucideAngularModule.pick({ Camera , Save , Plus , X , Trash2 }),
  ]
})
export class JobSeekerModule { }
