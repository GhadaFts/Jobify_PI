import { Component, OnInit } from '@angular/core';
import { Interview, InterviewStatus } from '../../../types';
import { InterviewsService } from '../../../services/interviews.service';
import { FinalInterview } from './final_interview_type';
import { ApplicationResponseDTO, ApplicationService } from '../../../services/application.service';
import { UserService } from '../../../services/user.service';
import { JobService } from '../../../services/job.service';
import { forkJoin, of } from 'rxjs';
import { switchMap, map, catchError } from 'rxjs/operators';

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
  isLoading: boolean = true;
  errorMessage: string = '';

  constructor(
    private interviewsService: InterviewsService,
    private appService: ApplicationService,
    private userService: UserService,
    private jobService: JobService
  ) {}

  ngOnInit(): void {
    this.loadInterviews();
  }

  private loadInterviews(): void {
    console.log('🔄 Loading recruiter interviews...');
    
    this.interviewsService.getRecruiterInterviews()
      .pipe(
        switchMap(interviews => {
          console.log('✅ Fetched interviews:', interviews);
          this.interviews = interviews;

          if (!interviews || interviews.length === 0) {
            console.log('ℹ️ No interviews found');
            this.isLoading = false;
            return of([]);
          }

          // Build array of async requests with error handling
          const requests = interviews.map(interview => {
            console.log('🔍 Processing interview:', interview);
            console.log('  - Application ID:', interview.applicationId);
            console.log('  - JobSeeker ID:', interview.jobSeekerId);
            console.log('  - Recruiter ID:', interview.recruiterId);

            return forkJoin({
              application: this.appService.getApplicationById(interview.applicationId).pipe(
                catchError(err => {
                  console.error(`❌ Failed to fetch application ${interview.applicationId}:`, err);
                  return of(null);
                })
              ),
              jobSeeker: this.userService.getUserById(interview.jobSeekerId).pipe(
                catchError(err => {
                  console.error(`❌ Failed to fetch jobSeeker ${interview.jobSeekerId}:`, err);
                  return of({ fullName: 'Unknown User', email: '', photo_profil: null });
                })
              ),
              recruiter: this.userService.getUserById(interview.recruiterId).pipe(
                catchError(err => {
                  console.error(`❌ Failed to fetch recruiter ${interview.recruiterId}:`, err);
                  return of({ fullName: 'Unknown Recruiter', email: '' });
                })
              ),
            }).pipe(
              switchMap(result => {
                // Now fetch the photo URL after we have the jobSeeker data
                let photoUrl: string | null = null;
                
                if (result.jobSeeker?.photo_profil) {
                  try {
                    photoUrl = this.userService.getImageUrl(result.jobSeeker.photo_profil);
                  } catch (err) {
                    console.error(`❌ Failed to fetch photo for jobSeeker ${interview.jobSeekerId}:`, err);
                  }
                }

                return of(photoUrl).pipe(
                  switchMap(photoUrl => {
                    // Attach the photo URL to the jobSeeker object
                    const jobSeekerWithPhoto = {
                      ...result.jobSeeker,
                      photoUrl: photoUrl,
                      profilePicture: photoUrl
                    };

                    if (!result.application) {
                      console.warn('⚠️ Application not found, creating placeholder');
                      return of({
                        id: interview.id,
                        application: null,
                        jobSeeker: jobSeekerWithPhoto,
                        recruiter: result.recruiter,
                        job_title: 'Unknown Position',
                        scheduledDate: interview.scheduledDate,
                        duration: interview.duration,
                        interviewType: interview.interviewType,
                        status: interview.status,
                        createdAt: interview.createdAt,
                        updatedAt: interview.updatedAt
                      } as FinalInterview);
                    }

                    return this.jobService.getJobById(result.application.jobOfferId).pipe(
                      map(job => ({
                        id: interview.id,
                        application: result.application,
                        jobSeeker: jobSeekerWithPhoto,
                        recruiter: result.recruiter,
                        job_title: job?.title || 'Unknown Position',
                        scheduledDate: interview.scheduledDate,
                        duration: interview.duration,
                        interviewType: interview.interviewType,
                        status: interview.status,
                        createdAt: interview.createdAt,
                        updatedAt: interview.updatedAt
                      } as FinalInterview)),
                      catchError(err => {
                        console.error(`❌ Failed to fetch job ${result.application?.jobOfferId}:`, err);
                        return of({
                          id: interview.id,
                          application: result.application,
                          jobSeeker: jobSeekerWithPhoto,
                          recruiter: result.recruiter,
                          job_title: 'Unknown Position',
                          scheduledDate: interview.scheduledDate,
                          duration: interview.duration,
                          interviewType: interview.interviewType,
                          status: interview.status,
                          createdAt: interview.createdAt,
                          updatedAt: interview.updatedAt
                        } as FinalInterview);
                      })
                    );
                  })
                );
              }),
              catchError(err => {
                console.error('❌ Error processing interview:', err);
                return of(null);
              })
            );
          });

          return forkJoin(requests);
        }),
        catchError(err => {
          console.error('❌ Failed to fetch recruiter interviews:', err);
          this.errorMessage = 'Failed to load interviews. Please try again later.';
          this.isLoading = false;
          return of([]);
        })
      )
      .subscribe(finalInterviews => {
        // Filter out null entries
        this.final_interviews = finalInterviews.filter(interview => interview !== null) as FinalInterview[];
        console.log('✅ Final interviews loaded:', this.final_interviews);
        this.isLoading = false;
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
      interview.jobSeeker?.fullName?.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
      interview.job_title?.toLowerCase().includes(this.searchQuery.toLowerCase())
    );
  }

  get filteredUpcomingInterviews(): FinalInterview[] {
    return this.upcomingInterviews.filter(interview =>
      interview.jobSeeker?.fullName?.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
      interview.job_title?.toLowerCase().includes(this.searchQuery.toLowerCase())
    );
  }

  get filteredCompletedInterviews(): FinalInterview[] {
    return this.completedInterviews.filter(interview =>
      interview.jobSeeker?.fullName?.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
      interview.job_title?.toLowerCase().includes(this.searchQuery.toLowerCase())
    );
  }

  retryLoad(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.loadInterviews();
  }
}