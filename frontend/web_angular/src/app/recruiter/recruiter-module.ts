import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // Required for ngModel and ngForm
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome'; // Required for fa-icon
import { ProfileInitial} from './profile-initial/profile-initial';
import { RecruiterRoutingModule } from './recruiter-routing-module';
import { RecruiterDashboard } from './dashboard/dashboard';
import { PublishJob } from './dashboard/publish-job/publish-job';
import { EditProfile } from './dashboard/edit-profile/edit-profile';
import { SharedModule } from '../shared/shared-module';
import { RecruiterSidebar } from './dashboard/recruiter-sidebar/recruiter-sidebar';
import { JobCardRecruiter } from './dashboard/publish-job/job-card-recruiter/job-card-recruiter';
import { RecruiterJobDetailsDialog } from './dashboard/publish-job/job-card-recruiter/recruiter-job-details-dialog/recruiter-job-details-dialog';
import { EditJobDialog } from './dashboard/publish-job/job-card-recruiter/edit-job-dialog/edit-job-dialog';
import { ApplicationDetailsDialog } from './dashboard/publish-job/job-card-recruiter/application-details-dialog/application-details-dialog';
import { InterviewScheduleDialog } from './dashboard/publish-job/job-card-recruiter/interview-schedule-dialog/interview-schedule-dialog';
import { TakeActionDialog } from './dashboard/publish-job/job-card-recruiter/take-action-dialog/take-action-dialog';
import { PdfViewerModule } from 'ng2-pdf-viewer';
import { Interviews } from './dashboard/interviews/interviews';
import { InterviewCard } from './dashboard/interviews/interview-card/interview-card';


@NgModule({
  declarations: [
    ProfileInitial,
    RecruiterDashboard,
    PublishJob,
    EditProfile,
    RecruiterSidebar,
    JobCardRecruiter,
    RecruiterJobDetailsDialog,
    EditJobDialog,
    ApplicationDetailsDialog,
    InterviewScheduleDialog,
    TakeActionDialog,
    Interviews,
    InterviewCard,
  ],
  imports: [
    CommonModule,
    FormsModule, // Add this for template-driven forms
    FontAwesomeModule, // Add this for FontAwesome icons
    RecruiterRoutingModule,
    SharedModule,
    PdfViewerModule,
    
    // Add SharedModule if needed for ToastService or other shared components
  ],
  exports: []
})
export class RecruiterModule { }