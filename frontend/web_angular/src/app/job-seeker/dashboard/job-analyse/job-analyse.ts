import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CareerService, CareerAnalysisRequest, ProcessedAdvice } from '../../../ai-service/career-service-backend';

@Component({
  selector: 'app-job-analyse',
  templateUrl: './job-analyse.html',
  styleUrls: ['./job-analyse.scss'],
  standalone: false,
})
export class JobAnalyse {
  country: string = '';
  education: string = '';
  certificate: string = '';
  skills: string = '';
  advice: ProcessedAdvice | null = null;
  isGenerating: boolean = false;
  errorMessage: string = '';

  constructor(private careerService: CareerService) {}

  /**
   * Vérifie si le formulaire est valide
   */
  isFormValid(): boolean {
    return !!this.country.trim() && 
           !!this.education.trim() && 
           !!this.certificate.trim();
    // Les skills sont optionnels dans le backend
  }

  /**
   * Génère les conseils de carrière via le microservice
   */
  generateAdvice() {
    if (!this.isFormValid()) {
      this.errorMessage = 'Please fill in all required fields (Country, Education, Certificate)';
      return;
    }

    this.isGenerating = true;
    this.errorMessage = '';
    this.advice = null;

    // Préparer la requête pour le backend
    const request: CareerAnalysisRequest = {
      country: this.country,
      education: this.education,
      certificate: this.certificate,
      skills: this.skills.trim() || undefined
    };

    // Appeler le service career
    this.careerService.analyzeCareer(request).subscribe({
      next: (aiResponse) => {
        console.log('AI Response received:', aiResponse);
        
        // Traiter la réponse pour l'affichage frontend
        this.advice = this.careerService.processAIAdvice(aiResponse, request);
        this.isGenerating = false;
      },
      error: (error) => {
        console.error('Error generating career advice:', error);
        this.errorMessage = error.message || 'Failed to generate career advice. Please try again.';
        this.isGenerating = false;
        
        // Fallback: afficher des données mockées en cas d'erreur
        this.showFallbackAdvice(request);
      }
    });
  }

  /**
   * Fallback en cas d'erreur du service
   */
  private showFallbackAdvice(request: CareerAnalysisRequest) {
    const fallbackAdvice: ProcessedAdvice = {
      summary: `Based on your ${request.education} education and ${request.certificate} certification, here's a career development plan for ${request.country}.`,
      recommendations: [
        {
          title: 'Enhance Your Technical Skills',
          description: `Focus on developing skills relevant to ${request.country}'s job market and consider additional certifications.`,
          priority: 'high'
        },
        {
          title: 'Build Professional Network',
          description: `Connect with professionals in ${request.country} through LinkedIn and industry events.`,
          priority: 'medium'
        },
        {
          title: 'Tailor Your Application Materials',
          description: `Customize your resume and cover letter for the ${request.country} market requirements.`,
          priority: 'medium'
        }
      ],
      skills: [
        { 
          name: request.skills?.split(',')[0] || 'Technical Expertise', 
          reason: `Core requirement for positions in ${request.country}` 
        },
        { 
          name: 'Cross-cultural Communication', 
          reason: 'Essential for working in international environments' 
        },
        { 
          name: 'Industry-specific Tools', 
          reason: 'In-demand skills for career advancement' 
        }
      ],
      careerPath: `With your background, you can target relevant positions in ${request.country} and advance your career through continuous learning and networking.`
    };

    this.advice = fallbackAdvice;
  }

  /**
   * Vérifie la santé du service career
   */
  checkServiceHealth() {
    this.careerService.healthCheck().subscribe({
      next: (health) => {
        console.log('Career service health:', health);
        alert(`Career service is ${health.status}`);
      },
      error: (error) => {
        console.error('Health check failed:', error);
        alert('Career service is unavailable');
      }
    });
  }

  /**
   * Couleur pour les priorités
   */
  getPriorityColor(priority: string): string {
    switch (priority) {
      case 'high':
        return 'bg-orange-500 text-white';
      case 'medium':
        return 'bg-blue-500 text-white';
      case 'low':
        return 'bg-green-500 text-white';
      default:
        return 'bg-gray-500 text-white';
    }
  }

  /**
   * Réinitialise le formulaire
   */
  resetForm() {
    this.country = '';
    this.education = '';
    this.certificate = '';
    this.skills = '';
    this.advice = null;
    this.errorMessage = '';
  }
}