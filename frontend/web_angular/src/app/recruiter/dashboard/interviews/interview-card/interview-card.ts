import { Component, Input } from '@angular/core';
import { InterviewStatus } from '../../../../types';
import { FinalInterview } from '../final_interview_type';

@Component({
  selector: 'app-interview-card',
  standalone: false,
  templateUrl: './interview-card.html',
  styleUrls: ['./interview-card.scss']
})
export class InterviewCard {
  @Input() interview!: FinalInterview;

  getTimeBadgeClass(): string {
    const interviewDateTime = new Date(this.interview.scheduledDate);
    const now = new Date();
    const timeDiff = interviewDateTime.getTime() - now.getTime();

    if (this.interview.status === InterviewStatus.COMPLETED) {
      return 'bg-gray-100 text-gray-800';
    } else if (timeDiff <= 0) {
      return 'bg-red-100 text-red-800';
    } else if (timeDiff <= 24 * 60 * 60 * 1000) { // Moins de 24h
      return 'bg-orange-100 text-orange-800';
    } else {
      return 'bg-green-100 text-green-800';
    }
  }

 getTimeBadgeText(): string {
  const interviewDateTime = new Date(this.interview.scheduledDate);
  const now = new Date();
  const timeDiff = interviewDateTime.getTime() - now.getTime();

  if (this.interview.status === InterviewStatus.COMPLETED) {
    return 'Completed';
  } else if (timeDiff <= 0) {
    return 'In Progress';
  } else {
    const days = Math.floor(timeDiff / (1000 * 60 * 60 * 24));
    const hours = Math.floor((timeDiff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    
    if (days > 0) {
      return `${days}d and ${hours}h left`;
    } else if (hours > 0) {
      return `${hours}h left`;
    } else {
      const minutes = Math.floor((timeDiff % (1000 * 60 * 60)) / (1000 * 60));
      return `${minutes}m left`;
    }
  }
}

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      weekday: 'short',
      month: 'short',
      day: 'numeric'
    });
  }

  formatTime(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleTimeString('en-US', {
      hour: "2-digit",
      minute: "2-digit"
    });
  }
}