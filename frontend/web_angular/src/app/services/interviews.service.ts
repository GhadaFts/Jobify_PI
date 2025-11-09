import { Injectable } from '@angular/core';
import { Interview } from '../types';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class InterviewsService {
  private interviews: Interview[] = [];
  private interviewsSubject = new BehaviorSubject<Interview[]>(this.interviews);
  
  public interviews$ = this.interviewsSubject.asObservable();

  constructor() {}

  // Ajouter une nouvelle interview
  addInterview(interviewData: any): void {
    const newInterview: Interview = {
      id: Date.now().toString(),
      candidateName: interviewData.application.jobSeeker.fullName,
      candidateTitle: interviewData.application.jobSeeker.title,
      candidatePhoto: interviewData.application.jobSeeker.photo_profil,
      jobOfferTitle: this.getJobTitle(interviewData.application.jobOfferId), // À adapter
      jobOfferId: interviewData.application.jobOfferId,
      applicationId: interviewData.application.id,
      interviewDate: interviewData.interviewDate,
      interviewTime: interviewData.interviewTime,
      interviewDuration: interviewData.duration,
      interviewType: interviewData.interviewType,
      interviewLocation: interviewData.interviewLocation,
      interviewStatus: 'scheduled',
      interviewer: 'Recruiter',
      notes: interviewData.additionalNotes,
      meetingLink: interviewData.meetingLink
    };

    this.interviews.unshift(newInterview);
    this.interviewsSubject.next([...this.interviews]);
    
    console.log('Interview added:', newInterview);
  }

  // Récupérer toutes les interviews
  getAllInterviews(): Interview[] {
    return [...this.interviews];
  }

  // Méthode pour simuler le titre du job (à adapter avec vos données)
  private getJobTitle(jobOfferId: string): string {
    // Simuler la récupération du titre du job
    const jobTitles: { [key: string]: string } = {
      '1': 'Senior Frontend Developer',
      '2': 'Product Manager'
    };
    return jobTitles[jobOfferId] || 'Job Offer';
  }
}