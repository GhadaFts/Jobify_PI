import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { CompanyProfileDialog } from '../find-job/job-card/company-profile-dialog/company-profile-dialog';

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
  id: string;
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
export class JobSeekerSidebar {
  activeTab: 'companies' | 'interviews' = 'companies';

  featuredCompanies: Company[] = [
    {
      id: '1',
      name: 'TekUp',
      description: 'Leading technology company specializing in web and mobile development solutions.',
      logo: 'https://images.unsplash.com/photo-1559136555-9303baea8ebd?w=100&h=100&fit=crop',
      domain: 'technology',
      employees: '150 employees',
      location: 'Tunis Center'
    },
    {
      id: '2',
      name: 'Tebourba Farming Coop',
      description: 'Agricultural cooperative specializing in organic farming and sustainable agriculture.',
      logo: 'https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=100&h=100&fit=crop',
      domain: 'agriculture',
      employees: '50 employees',
      location: 'Tebourba'
    },
    {
      id: '3',
      name: 'Tebourba Secondary School',
      description: 'Public secondary school committed to providing quality education.',
      domain: 'education',
      employees: '35 employees',
      location: 'Tebourba Center'
    },
    {
      id: '4',
      name: 'Tech Solutions SARL',
      description: 'Innovative software development and IT consulting services.',
      domain: 'technology',
      employees: '80 employees',
      location: 'Ariana'
    },
    {
      id: '5',
      name: 'MedCare Hospital',
      description: 'Leading healthcare provider with modern medical facilities.',
      domain: 'healthcare',
      employees: '200 employees',
      location: 'Tunis'
    }
  ];

  interviewNotifications: InterviewNotification[] = [
    {
      id: '1',
      companyName: 'TekUp',
      interviewDate: '2025-11-21',
      interviewTime: '13:00 AM',
      location: 'https://meet.google.com/xxx-xxxx-xxx',
      additionalNotes: 'Please have your portfolio ready. Technical assessment will include live coding.',
      duration: '60 mins',
      interviewType: 'Online'
    },
    {
      id: '2',
      companyName: 'Tech Solutions SARL',
      interviewDate: '2026-01-12',
      interviewTime: '2:30 PM',
      location: 'Tech Park, Ariana, Tunisia',
      additionalNotes: 'Bring your ID and previous work samples. Dress code: Business casual.',
      duration: '45 mins',
      interviewType: 'local'
    },
    {
      id: '3',
      companyName: 'MedCare Hospital',
      interviewDate: '2024-01-20',
      interviewTime: '9:00 AM',
      location: 'https://teams.microsoft.com/l/meetup-join/xxx',
      additionalNotes: 'Discussion about IT infrastructure and support procedures.',
      duration: '90 mins',
      interviewType: 'Technical Assessment - Online'
    },
    {
      id: '4',
      companyName: 'MedCare Hospital',
      interviewDate: '2024-01-20',
      interviewTime: '9:00 AM',
      location: 'https://teams.microsoft.com/l/meetup-join/xxx',
      additionalNotes: 'Discussion about IT infrastructure and support procedures.',
      duration: '90 mins',
      interviewType: 'Technical Assessment - Online'
    }
  ];

  constructor(private dialog: MatDialog) {}

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