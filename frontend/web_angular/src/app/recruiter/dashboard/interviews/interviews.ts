import { Component, OnInit } from '@angular/core';
import { Interview, InterviewStatus } from '../../../types';
import { InterviewsService } from '../../../services/interviews.service';
import { FinalInterview } from './final_interview_type';
import { ApplicationResponseDTO, ApplicationService } from '../../../services/application.service';
import { UserService } from '../../../services/user.service';
import { JobService } from '../../../services/job.service';
import { forkJoin } from 'rxjs';
import { switchMap, map } from 'rxjs/operators';


@Component({
  selector: 'app-interviews',
  standalone: false,
  templateUrl: './interviews.html',
  styleUrls: ['./interviews.scss']
})
export class Interviews implements OnInit {
  activeTab: string = 'all';
  searchQuery: string = '';
  interviews: Interview[] = [];
  final_interviews: FinalInterview[] = [];

  constructor(private interviewsService: InterviewsService,
    private appService: ApplicationService,
    private userService: UserService,
    private jobService: JobService
  ) {}

  ngOnInit(): void {
  this.interviewsService.getRecruiterInterviews()
    .pipe(
      switchMap(interviews => {
        this.interviews = interviews;

        // Build array of async requests
        const requests = interviews.map(interview => {
          return forkJoin({
            application: this.appService.getApplicationById(interview.applicationId),
            jobSeeker: this.userService.getUserById(interview.jobSeekerId),
            recruiter: this.userService.getUserById(interview.recruiterId),
          }).pipe(
            switchMap(result =>
              this.jobService.getJobById(result.application.jobOfferId).pipe(
              map(job => ({
                  id: interview.id,
                  application: result.application,
                  jobSeeker: result.jobSeeker,
                  recruiter: result.recruiter,
                  job_title: job.title,
                  scheduledDate: interview.scheduledDate,
                  duration: interview.duration,
                  interviewType: interview.interviewType,
                  status: interview.status,
                  createdAt: interview.createdAt,
                  updatedAt: interview.updatedAt
                }))
              )
            )
          );
        });

        return forkJoin(requests);
      })
    )
    .subscribe(finalInterviews => {
      this.final_interviews = finalInterviews;
    });
}


  get allInterviews(): FinalInterview[] {
    return this.final_interviews;
  }

  get upcomingInterviews(): FinalInterview[] {
    const now = new Date();
    return this.final_interviews.filter(interview => {
      const interviewDateTime = new Date(interview.scheduledDate);
      return interviewDateTime > now && interview.status !== InterviewStatus.COMPLETED;
    });
  }

  get completedInterviews(): FinalInterview[] {
    return this.final_interviews.filter(interview => interview.status === InterviewStatus.COMPLETED);
  }

  get filteredAllInterviews(): FinalInterview[] {
    return this.allInterviews.filter(interview =>
      interview.jobSeeker.fullName.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
      interview.job_title.toLowerCase().includes(this.searchQuery.toLowerCase())
    );
  }

  get filteredUpcomingInterviews(): FinalInterview[] {
    return this.upcomingInterviews.filter(interview =>
      interview.jobSeeker.fullName.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
      interview.job_title.toLowerCase().includes(this.searchQuery.toLowerCase())
    );
  }

  get filteredCompletedInterviews(): FinalInterview[] {
    return this.completedInterviews.filter(interview =>
      interview.jobSeeker.fullName.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
      interview.job_title.toLowerCase().includes(this.searchQuery.toLowerCase())
    );
  }
}