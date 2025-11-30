import { Component, OnInit } from '@angular/core';
import { Interview, InterviewStatus } from '../../../types';
import { InterviewsService } from '../../../services/interviews.service';
import { FinalInterview } from './final_interview_type';
import { ApplicationResponseDTO, ApplicationService } from '../../../services/application.service';
import { UserService } from '../../../services/user.service';
import { JobService } from '../../../services/job.service';

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
    // S'abonner aux mises à jour des interviews
    this.interviewsService.interviews$.subscribe(interviews => {
      this.interviews = interviews;
    });
    this.interviews.forEach(interview => {
      var application: ApplicationResponseDTO | null = null;
      var recruiter: any | null = null
      var job_seeker: any | null = null
      var job_title: string = ""
      this.appService.getApplicationById(interview.applicationId).subscribe(app =>
        {application = app;
          this.jobService.getJobById(app.jobOfferId).subscribe(job => job_title = job.title)
        }
      )
      this.userService.getUserById(interview.jobSeekerId).subscribe(js => job_seeker = js)
      this.userService.getUserById(interview.recruiterId).subscribe(rec => recruiter = rec)
      const finalInterview: FinalInterview = {
        id: interview.id,
        application: application,
        jobSeeker: job_seeker,
        recruiter: recruiter,
        job_title: job_title,
        scheduledDate: interview.scheduledDate,
        duration: interview.duration,
        interviewType: interview.interviewType,
        status: interview.status,
        createdAt: interview.createdAt,
        updatedAt: interview.updatedAt
      }
      this.final_interviews.push(finalInterview)
    })
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