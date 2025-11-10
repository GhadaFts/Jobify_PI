import { Component, OnInit } from '@angular/core';
import { Interview } from '../../../types';
import { InterviewsService } from '../../../services/interviews.service';

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

  constructor(private interviewsService: InterviewsService) {}

  ngOnInit(): void {
    // S'abonner aux mises à jour des interviews
    this.interviewsService.interviews$.subscribe(interviews => {
      this.interviews = interviews;
    });
  }

  get allInterviews(): Interview[] {
    return this.interviews;
  }

  get upcomingInterviews(): Interview[] {
    const now = new Date();
    return this.interviews.filter(interview => {
      const interviewDateTime = new Date(interview.interviewDate + 'T' + interview.interviewTime);
      return interviewDateTime > now && interview.interviewStatus !== 'completed';
    });
  }

  get completedInterviews(): Interview[] {
    return this.interviews.filter(interview => interview.interviewStatus === 'completed');
  }

  get filteredAllInterviews(): Interview[] {
    return this.allInterviews.filter(interview =>
      interview.candidateName.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
      interview.jobOfferTitle.toLowerCase().includes(this.searchQuery.toLowerCase())
    );
  }

  get filteredUpcomingInterviews(): Interview[] {
    return this.upcomingInterviews.filter(interview =>
      interview.candidateName.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
      interview.jobOfferTitle.toLowerCase().includes(this.searchQuery.toLowerCase())
    );
  }

  get filteredCompletedInterviews(): Interview[] {
    return this.completedInterviews.filter(interview =>
      interview.candidateName.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
      interview.jobOfferTitle.toLowerCase().includes(this.searchQuery.toLowerCase())
    );
  }
}