import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, timeout } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface CareerAnalysisRequest {
  country: string;
  education: string;
  certificate: string;
  skills?: string;
}

export interface CareerAnalysisResponse {
  advice: string;
  model?: string;
  version?: string;
}

export interface ProcessedAdvice {
  summary: string;
  recommendations: {
    title: string;
    description: string;
    priority: 'high' | 'medium' | 'low';
  }[];
  skills: {
    name: string;
    reason: string;
  }[];
  careerPath: string;
}

@Injectable({
  providedIn: 'root'
})
export class CareerService {
  private apiUrl = 'http://localhost:5000';

  constructor(private http: HttpClient) {}

  /**
   * Analyse de carrière avec le modèle AI
   */
  analyzeCareer(request: CareerAnalysisRequest): Observable<CareerAnalysisResponse> {
    return this.http.post<CareerAnalysisResponse>(`${this.apiUrl}/analyze`, request)
      .pipe(
        timeout(30000), // 30 secondes timeout
        catchError(this.handleError)
      );
  }

  /**
   * Vérification de la santé du service
   */
  healthCheck(): Observable<{ status: string; service: string }> {
    return this.http.get<{ status: string; service: string }>(`${this.apiUrl}/health`)
      .pipe(
        timeout(5000),
        catchError(this.handleError)
      );
  }

  /**
   * Traitement de la réponse AI pour l'affichage frontend
   */
  processAIAdvice(aiResponse: CareerAnalysisResponse, userData: CareerAnalysisRequest): ProcessedAdvice {
    const adviceText = aiResponse.advice;
    
    // Séparation des phrases (basé sur les points ou retours à la ligne)
    const sentences = this.extractSentences(adviceText);
    
    // Construction de la réponse structurée
    return this.structureAdvice(sentences, userData);
  }

  /**
   * Extraction des phrases depuis le texte AI
   */
  private extractSentences(text: string): string[] {
    // Méthode 1: Séparation par points suivis d'un espace
    let sentences = text.split(/\.\s+/).filter(sentence => sentence.trim().length > 0);
    
    // Si pas assez de phrases, essayer avec les retours à la ligne
    if (sentences.length < 3) {
      sentences = text.split(/\n+/).filter(sentence => sentence.trim().length > 0);
    }
    
    // Nettoyer les phrases (supprimer les points en fin de phrase)
    sentences = sentences.map(sentence => 
      sentence.replace(/\.$/, '').trim()
    );
    
    return sentences;
  }

  /**
   * Structure les phrases en format frontend
   */
  private structureAdvice(sentences: string[], userData: CareerAnalysisRequest): ProcessedAdvice {
    const summary = sentences[0] || `Career advice for ${userData.country} based on your profile.`;
    
    // Création des recommandations à partir des phrases
    const recommendations = sentences.slice(1, 4).map((sentence, index) => ({
      title: this.generateRecommendationTitle(sentence, index),
      description: sentence,
      priority: this.determinePriority(index, sentences.length)
    }));

    // Extraction des compétences mentionnées
    const skills = this.extractSkills(sentences, userData.skills);

    // Dernière phrase comme chemin de carrière
    const careerPath = sentences[sentences.length - 1] || 
      `With your ${userData.education} and ${userData.certificate}, you have strong potential in ${userData.country}.`;

    return {
      summary,
      recommendations,
      skills,
      careerPath
    };
  }

  /**
   * Génère des titres basés sur le contenu des phrases
   */
  private generateRecommendationTitle(sentence: string, index: number): string {
    // Titres prédéfinis basés sur le contenu commun
    const commonTitles = [
      'Skill Development Focus',
      'Career Strategy',
      'Professional Networking',
      'Market Alignment',
      'Certification Path',
      'Experience Building'
    ];

    // Essayez d'extraire un mot-clé pour un titre plus pertinent
    const keywords = ['certification', 'skill', 'network', 'experience', 'market', 'portfolio', 'project'];
    const lowerSentence = sentence.toLowerCase();
    
    for (const keyword of keywords) {
      if (lowerSentence.includes(keyword)) {
        return this.capitalizeFirstLetter(keyword) + ' Development';
      }
    }

    // Fallback aux titres prédéfinis
    return commonTitles[index % commonTitles.length];
  }

  /**
   * Détermine la priorité basée sur la position
   */
  private determinePriority(index: number, totalSentences: number): 'high' | 'medium' | 'low' {
    if (index === 0) return 'high';
    if (index < 3) return 'medium';
    return 'low';
  }

  /**
   * Extrait les compétences mentionnées
   */
  private extractSkills(sentences: string[], userSkills?: string): { name: string; reason: string }[] {
    const skills: { name: string; reason: string }[] = [];
    
    // Utiliser les compétences de l'utilisateur si disponibles
    if (userSkills) {
      const userSkillList = userSkills.split(',').map(skill => skill.trim());
      userSkillList.slice(0, 3).forEach(skill => {
        skills.push({
          name: skill,
          reason: 'Essential for your target market'
        });
      });
    }

    // Si pas assez de compétences, en ajouter des génériques
    if (skills.length < 2) {
      const defaultSkills = [
        { name: 'Technical Certifications', reason: 'Highly valued in international markets' },
        { name: 'Communication Skills', reason: 'Critical for cross-cultural collaboration' },
        { name: 'Project Management', reason: 'Key for career advancement' }
      ];
      
      defaultSkills.slice(0, 3 - skills.length).forEach(skill => {
        skills.push(skill);
      });
    }

    return skills;
  }

  /**
   * Capitalise la première lettre
   */
  private capitalizeFirstLetter(text: string): string {
    return text.charAt(0).toUpperCase() + text.slice(1);
  }

  /**
   * Gestion des erreurs HTTP
   */
  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An unknown error occurred';
    
    if (error.error instanceof ErrorEvent) {
      // Erreur client
      errorMessage = `Client error: ${error.error.message}`;
    } else {
      // Erreur serveur
      switch (error.status) {
        case 0:
          errorMessage = 'Unable to connect to career service. Please check if the service is running.';
          break;
        case 400:
          errorMessage = 'Invalid request data. Please check all required fields.';
          break;
        case 500:
          errorMessage = 'Career analysis service is temporarily unavailable.';
          break;
        case 504:
          errorMessage = 'Request timeout. The analysis is taking longer than expected.';
          break;
        default:
          errorMessage = `Server error: ${error.status} - ${error.message}`;
      }
    }
    
    console.error('Career service error:', error);
    return throwError(() => new Error(errorMessage));
  }
}