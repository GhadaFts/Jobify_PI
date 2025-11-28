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

// Interfaces pour l'AI Ranking
export interface JobSeekerAIRequest {
  id: number;
  email: string;
  fullName: string;
  description: string;
  nationality: string;
  skills: string[];
  experience: string;
  education: string;
  title: string;
  date_of_birth: string;
  gender: string;
}

export interface ApplicationAIRequest {
  id: number;
  applicationDate: string;
  status: string;
  motivation_lettre: string;
  jobSeeker: JobSeekerAIRequest;
  jobOfferId: string;
}

export interface JobOfferAIRequest {
  id: string;
  title: string;
  company: string;
  location: string;
  type: string;
  experience: string;
  salary: string;
  description: string;
  skills: string[];
  requirements: string[];
  applications: ApplicationAIRequest[];
}

export interface ApplicationScore {
  id: number;
  score: number;
}

export interface AIRankingResponse {
  id: string;
  applications: ApplicationScore[];
}

@Injectable({
  providedIn: 'root'
})
export class AiService {
  private apiUrl = 'http://localhost:8888/ai-service';

  constructor(private http: HttpClient) {}

  // Méthode existante pour CV correction
  analyzeCv(cvContent: string, jobDescription?: string): Observable<any> {
    const payload: any = { cvContent };
    
    if (jobDescription && jobDescription.trim() !== '') {
      payload.jobDescription = jobDescription;
    }
    
    return this.http.post(`${this.apiUrl}/cv-correction/analyze`, payload);
  }

  // Méthode existante pour Interview Bot
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

  // NOUVELLE MÉTHODE POUR AI RANKING
  rankApplications(jobOfferData: JobOfferAIRequest): Observable<AIRankingResponse> {
    return this.http.post<AIRankingResponse>(
      `${this.apiUrl}/application-ranking/rank`, 
      jobOfferData
    );
  }

  // Méthode pour valider les données avant ranking
  validateRankingRequest(jobOfferData: JobOfferAIRequest): Observable<{
    valid: boolean;
    errors: string[];
    warnings: string[];
  }> {
    return this.http.post<{
      valid: boolean;
      errors: string[];
      warnings: string[];
    }>(`${this.apiUrl}/application-ranking/validate`, jobOfferData);
  }

  // Méthode de santé du service
  checkRankingHealth(): Observable<{
    status: string;
    timestamp: string;
    service: string;
    version?: string;
  }> {
    return this.http.get<{
      status: string;
      timestamp: string;
      service: string;
      version?: string;
    }>(`${this.apiUrl}/application-ranking/health`);
  }
}