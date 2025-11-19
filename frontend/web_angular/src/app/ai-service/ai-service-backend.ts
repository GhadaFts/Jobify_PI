import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface ConversationContext {
  phase: 'collect_info' | 'advice' | 'practice';
  currentStep?: string;
  userProfile?: UserProfile;
}

export interface UserProfile {
  jobTitle?: string;
  interviewType?: 'presentiel' | 'en_ligne' | 'hybride';
  experienceLevel?: string;
  skills?: string[];
  industry?: string;
  companyType?: string;
  specificConcerns?: string[];
}

export interface ChatResponse {
  response: string;
  conversationPhase: 'collect_info' | 'advice' | 'practice';
  nextStep?: string;
  userProfileUpdates?: Partial<UserProfile>;
  suggestions?: string[];
  questions?: string[];
}

@Injectable({
  providedIn: 'root'
})
export class AiService {
  private apiUrl = 'http://localhost:3001';

  constructor(private http: HttpClient) {}

  // Méthode existante pour CV correction
  analyzeCv(cvContent: string, jobDescription?: string): Observable<any> {
    const payload: any = { cvContent };
    
    if (jobDescription && jobDescription.trim() !== '') {
      payload.jobDescription = jobDescription;
    }
    
    return this.http.post(`${this.apiUrl}/cv-correction/analyze`, payload);
  }

  // NOUVELLE MÉTHODE POUR INTERVIEW BOT
  chatWithInterviewBot(
    message: string, 
    conversationContext?: ConversationContext, 
    userProfile?: UserProfile
  ): Observable<ChatResponse> {
    const payload = {
      message,
      conversationContext,
      userProfile
    };
    
    return this.http.post<ChatResponse>(`${this.apiUrl}/interview-bot/chat`, payload);
  }
}