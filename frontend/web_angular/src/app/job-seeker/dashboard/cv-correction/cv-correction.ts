import { Component } from '@angular/core';
import { JobSeeker } from '../../../types';
import { AiService } from '../../../ai-service/ai-service-backend'; // Votre nouveau service
import * as pdfjsLib from 'pdfjs-dist';

interface ImprovedSummary {
  overallAssessment: string;
  strengths: string[];
  improvements: string[];
}

interface CvSuggestion {
  id: string;
  type: 'success' | 'warning' | 'info' | 'missing';
  title: string;
  message: string;
}

interface CvAnalysisResponse {
  cvScore: number;
  cvSuggestions: CvSuggestion[];
  improvedSummary: ImprovedSummary;
  profile: JobSeeker;
}

@Component({
  selector: 'app-cv-correction',
  templateUrl: './cv-correction.html',
  styleUrls: ['./cv-correction.scss'],
  standalone: false
})
export class CvCorrection {
  cvFile: File | null = null;
  isDragging = false;
  isAnalyzed = false;
  isLoading = false;
  cvScore: number | null = null;
  improvedSummary: ImprovedSummary = {
    overallAssessment: '',
    strengths: [],
    improvements: []
  };

  // Supprimez le defaultPrompt car il est maintenant dans le backend

  constructor(private aiService: AiService) { // Renommez 'ai' en 'aiService'
    (pdfjsLib as any).GlobalWorkerOptions.workerSrc =
      `https://unpkg.com/pdfjs-dist@${(pdfjsLib as any).version}/build/pdf.worker.min.mjs`;
  }

  cvSuggestions: CvSuggestion[] = [];
  profile: JobSeeker = {
    id: 0,
    email: '',
    password: '',
    fullName: '',
    role: 'jobseeker',
    photo_profil: '',
    twitter_link: '',
    web_link: '',
    github_link: '',
    facebook_link: '',
    description: '',
    phone_number: '',
    nationality: '',
    skills: [],
    experience: [],
    education: [],
    title: '',
    date_of_birth: '',
    gender: ''
  };
  pdfText = '';
  jobDescription: string = ''; // Ajoutez cette propriété si elle n'existe pas

  onDragOver(event: DragEvent) {
    event.preventDefault();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    this.isDragging = false;
  }

  async onDrop(event: DragEvent) {
    event.preventDefault();
    this.isDragging = false;
    const file = event.dataTransfer?.files?.[0];
    if (!file) return;
    
    if (file.type !== 'application/pdf') {
      alert('Please upload a PDF file');
      return;
    }

    this.cvFile = file;
    try {
      const arrayBuffer = await file.arrayBuffer();
      const pdf = await pdfjsLib.getDocument({ data: arrayBuffer }).promise;

      let text = '';
      for (let i = 1; i <= pdf.numPages; i++) {
        const page = await pdf.getPage(i);
        const content = await page.getTextContent();
        text += content.items.map((item: any) => item.str).join(' ') + '\n';
      }

      this.pdfText = text;
      console.log('Extracted PDF text:', this.pdfText.slice(0, 500)); // preview
    } catch (error) {
      console.error('Error extracting PDF text:', error);
      alert('Error reading PDF file. Please try again.');
      this.removeFile();
    }
  }

  async onFileSelected(event: Event) {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;
    
    if (file.type !== 'application/pdf') {
      alert('Please upload a PDF file');
      return;
    }

    this.cvFile = file;
    try {
      const arrayBuffer = await file.arrayBuffer();
      const pdf = await pdfjsLib.getDocument({ data: arrayBuffer }).promise;

      let text = '';
      for (let i = 1; i <= pdf.numPages; i++) {
        const page = await pdf.getPage(i);
        const content = await page.getTextContent();
        text += content.items.map((item: any) => item.str).join(' ') + '\n';
      }

      this.pdfText = text;
      console.log('Extracted PDF text:', this.pdfText.slice(0, 500)); // preview
    } catch (error) {
      console.error('Error extracting PDF text:', error);
      alert('Error reading PDF file. Please try again.');
      this.removeFile();
    }
  }

  removeFile() {
    this.cvFile = null;
    this.isAnalyzed = false;
    this.isLoading = false;
    this.cvScore = null;
    this.cvSuggestions = [];
    this.pdfText = '';
    this.jobDescription = ''; // Reset job description aussi
    this.profile = {
      id: 0,
      email: '',
      password: '',
      fullName: '',
      role: 'jobseeker',
      photo_profil: '',
      twitter_link: '',
      web_link: '',
      github_link: '',
      facebook_link: '',
      description: '',
      phone_number: '',
      nationality: '',
      skills: [],
      experience: [],
      education: [],
      title: '',
      date_of_birth: '',
      gender: ''
    };
  }

  calculateExperienceYears(): number {
    if (!this.profile.experience || this.profile.experience.length === 0) {
      return 0;
    }

    let totalMonths = 0;
    const now = new Date();

    this.profile.experience.forEach(exp => {
      const startDate = new Date(exp.startDate);
      const endDate = exp.endDate ? new Date(exp.endDate) : now;
      
      const months = (endDate.getFullYear() - startDate.getFullYear()) * 12 +
                    (endDate.getMonth() - startDate.getMonth());
      totalMonths += months;
    });

    return Math.round(totalMonths / 12);
  }

  async analyzeCV() {
    if (!this.pdfText) {
      alert('Please upload a PDF CV first.');
      return;
    }

    this.isLoading = true;

    try {
      // Utilisez le nouveau service backend au lieu d'appeler Gemini directement
      const result = await this.aiService.analyzeCv(this.pdfText, this.jobDescription).toPromise();
      
      // Traitez le résultat directement (le backend garantit la structure JSON)
      this.cvScore = result.cvScore;
      this.cvSuggestions = result.cvSuggestions;
      this.improvedSummary = result.improvedSummary;
      this.profile = result.profile;
      this.isAnalyzed = true;

    } catch (error) {
      console.error('Error analyzing CV:', error);
      alert('Error analyzing CV. Please try again.');
    } finally {
      this.isLoading = false;
    }
  }

  // Supprimez handleNonJsonResponse car le backend gère déjà ça
}