import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { CompanyProfileDialog } from '../find-job/job-card/company-profile-dialog/company-profile-dialog';
import { Interview } from '../../../types';
import { InterviewsService } from '../../../services/interviews.service';
import { ApplicationService } from '../../../services/application.service';
import { JobService } from '../../../services/job.service';
import { UserService } from '../../../services/user.service';
import { switchMap, map } from 'rxjs/operators';
import { forkJoin } from 'rxjs';

interface Company {
  id: string;
  name: string;
  description: string;
  logo?: string;
  domain: string;
  employees: string;
  location?: string;
}

interface InterviewNotification {
  id: number;
  companyName: string;
  interviewDate: string; // ISO string
  interviewTime: string;
  location: string;
  additionalNotes: string;
  duration: string;
  interviewType: string;
}

@Component({
  selector: 'app-job-seeker-sidebar',
  templateUrl: './job-seeker-sidebar.html',
  styleUrls: ['./job-seeker-sidebar.scss'],
  standalone: false,
})
export class JobSeekerSidebar implements OnInit {
  private myInterviews: Interview[] = []
  activeTab: 'companies' | 'interviews' = 'companies';
  interviewNotifications: InterviewNotification[] = []

  featuredCompanies: Company[] = [];

  ngOnInit(): void {
    // Fetch recruiters as featured companies
    this.userService.getRecruitersForJobSeekers()
      .subscribe({
        next: (recruiters) => {
          this.featuredCompanies = recruiters.map(recruiter => ({
            id: recruiter.id,
            name: recruiter.name,
            description: recruiter.description,
            logo: recruiter.logo,
            domain: recruiter.domain,
            employees: recruiter.employees,
            location: recruiter.location
          }));
        },
        error: (error) => {
          console.error('Error fetching recruiters:', error);
          // Fallback to empty array if fetch fails
          this.featuredCompanies = [];
        }
      });

    // Fetch interviews
    let interviews=this.interviewService.getMyUpcomingInterviews()
      .pipe(
        switchMap(interviews => {
          this.myInterviews = interviews;

          // Create one observable per interview
          const requests = interviews.map(interview =>
            this.appService.getApplicationById(interview.applicationId).pipe(
              switchMap(app =>
                this.jobService.getJobById(app.jobOfferId).pipe(
                  map(job => ({
                    id: interview.id,
                    interviewDate: this.formatInterviewDate(interview.scheduledDate),
                    interviewTime: this.formatInterviewTime(interview.scheduledDate),
                    location: interview.location as string,
                    additionalNotes: interview.notes as string,
                    duration: interview.duration + " mins",
                    interviewType: interview.interviewType,
                    companyName: job.company,
                  }))
                )
              )
            )
          );
          console.log('Interview notification interviews:', interviews);
         

          return forkJoin(requests);
        })
      )
      .subscribe(notifs => {
        this.interviewNotifications = notifs.sort((a, b) => b.id - a.id);
      });
  }


  constructor(private dialog: MatDialog, private interviewService: InterviewsService,
    private appService: ApplicationService, private jobService: JobService, private userService: UserService
  ) { }

  // Company Profile Methods
  openCompanyProfile(company: Company) {
    const dialogRef = this.dialog.open(CompanyProfileDialog, {
      width: '800px',
      maxWidth: '90vw',
      maxHeight: '90vh',
      data: { companyName: company.name }
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('Company profile dialog closed');
    });
  }

  // Interview Notification Methods
  getUpcomingInterviewsCount(): number {
    const today = new Date();
    return this.interviewNotifications.filter(interview =>
      new Date(interview.interviewDate) >= today
    ).length;
  }

  getInterviewCardClass(interview: InterviewNotification): string {
    const interviewDate = new Date(interview.interviewDate);
    const today = new Date();
    const timeDiff = interviewDate.getTime() - today.getTime();
    const daysDiff = Math.ceil(timeDiff / (1000 * 3600 * 24));

    if (daysDiff < 0) {
      return 'bg-gray-50 border-gray-200 opacity-60'; // Past interviews
    } else if (daysDiff === 0) {
      return 'bg-red-50 border-red-200 hover:bg-red-100'; // Today - Urgent
    } else if (daysDiff <= 2) {
      return 'bg-orange-50 border-orange-200 hover:bg-orange-100'; // Next 2 days - Important
    } else {
      return 'bg-blue-50 border-blue-200 hover:bg-blue-100'; // Future
    }
  }

  getTimeRemaining(interview: InterviewNotification): string {
    const interviewDate = new Date(interview.interviewDate);
    const today = new Date();
    const timeDiff = interviewDate.getTime() - today.getTime();
    const daysDiff = Math.ceil(timeDiff / (1000 * 3600 * 24));

    if (daysDiff < 0) {
      return 'Completed';
    } else if (daysDiff === 0) {
      return 'Today';
    } else if (daysDiff === 1) {
      return 'Tomorrow';
    } else if (daysDiff <= 7) {
      return `In ${daysDiff} days`;
    } else {
      const weeks = Math.floor(daysDiff / 7);
      return `${weeks} week${weeks > 1 ? 's' : ''}`;
    }
  }

  getTimeRemainingBadgeClass(interview: InterviewNotification): string {
    const interviewDate = new Date(interview.interviewDate);
    const today = new Date();
    const timeDiff = interviewDate.getTime() - today.getTime();
    const daysDiff = Math.ceil(timeDiff / (1000 * 3600 * 24));

    if (daysDiff < 0) {
      return 'bg-gray-100 text-gray-800';
    } else if (daysDiff === 0) {
      return 'bg-red-100 text-red-800';
    } else if (daysDiff <= 2) {
      return 'bg-orange-100 text-orange-800';
    } else {
      return 'bg-blue-100 text-blue-800';
    }
  }

  formatInterviewDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric'
    });
  }

  formatInterviewTime(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleTimeString('en-US', {
      "hour": "2-digit",
      "minute": "2-digit"
    });
  }

  getLocationDisplay(interview: InterviewNotification): string {
    if (this.isOnlineInterview(interview)) {
      return 'Online Meeting';
    } else {
      // Truncate long addresses
      const maxLength = 25;
      return interview.location.length > maxLength
        ? interview.location.substring(0, maxLength) + '...'
        : interview.location;
    }
  }

  isOnlineInterview(interview: InterviewNotification): boolean {
    return interview.interviewType.toLowerCase().includes('online') ||
      interview.location.startsWith('http');
  }

  joinInterview(interview: InterviewNotification) {
    if (this.isOnlineInterview(interview) && interview.location.startsWith('http')) {
      window.open(interview.location, '_blank');
    } else {
      alert(`Meeting link: ${interview.location}`);
    }
  }

  viewInterviewDetails(interview: InterviewNotification) {
    // You can implement a detailed view modal here
    const details = `
Company: ${interview.companyName}
Date: ${this.formatInterviewDate(interview.interviewDate)}
Time: ${interview.interviewTime}
Duration: ${interview.duration}
Type: ${interview.interviewType}
Location: ${interview.location}
Notes: ${interview.additionalNotes}
    `;
    alert(details);
  }
}