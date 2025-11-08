import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { JobSeeker } from '../../../types';
import { AiService } from '../../../ai-service/ai-service';
import * as pdfjsLib from 'pdfjs-dist';
import { marked } from 'marked';

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

interface GeminiResponse {
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
  
  private readonly defaultPrompt = `
You are a professional CV analyzer. 
You will receive the raw text extracted from a user's CV.

Your goal is to analyze it and return a JSON object (NO explanations, NO markdown, NO text outside JSON).
The JSON must strictly follow this structure:

{
  "cvScore": number, // overall score out of 100
  "cvSuggestions": [
    {
      "id": string, // unique id like "weak_1" or "missing_3"
      "type": "success" | "warning" | "info" | "missing",
      "title": string,
      "message": string
    }
  ],
  "improvedSummary": {
    "overallAssessment": string,
    "strengths": string[],
    "improvements": string[]
  },
  "profile": {
    "id": number,
    "email": string,
    "password": string,
    "fullName": string,
    "role": string,
    "photo_profil": string,
    "twitter_link": string,
    "web_link": string,
    "github_link": string,
    "facebook_link": string,
    "description": string,
    "phone_number": string,
    "nationality": string,
    "skills": string[],
    "experience": [
      {
        "position": string,
        "company": string,
        "startDate": string,
        "endDate": string,
        "description": string
      }
    ],
    "education": [
      {
        "degree": string,
        "field": string,
        "school": string,
        "graduationDate": string
      }
    ],
    "title": string,
    "date_of_birth": string,
    "gender": string
  }
}

Guidelines for the analysis:
- Identify STRONG sections (good content) → type = "success"
- Identify WEAK or unclear sections → type = "warning"
- Identify INFO or general improvement tips → type = "info"
- Identify MISSING sections (e.g., missing contact info, summary, education) → type = "missing"
- Score between 0–100 based on completeness, clarity, and structure.

Return only JSON.
Now analyze this CV:
`;

  constructor(private ai: AiService) {
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
    const fullPrompt = `${this.defaultPrompt}\n\n${this.pdfText}`;

    try {
      const result = await this.ai.ask(fullPrompt);
      if (typeof result === 'string') {
        try {
          // First try parsing as direct JSON
          const response: GeminiResponse = JSON.parse(result);
          this.cvScore = response.cvScore;
          this.cvSuggestions = response.cvSuggestions;
          this.improvedSummary = response.improvedSummary;
          this.profile = response.profile;
          this.isAnalyzed = true;
        } catch (parseError) {
          console.warn('JSON parse failed, trying to extract JSON from response');
          
          // Try to extract JSON from the response
          const jsonMatch = result.match(/\{[\s\S]*\}/);
          if (jsonMatch) {
            try {
              const response: GeminiResponse = JSON.parse(jsonMatch[0]);
              this.cvScore = response.cvScore;
              this.cvSuggestions = response.cvSuggestions;
              this.improvedSummary = response.improvedSummary;
              this.profile = response.profile;
              this.isAnalyzed = true;
            } catch (error) {
              console.error('Error parsing extracted JSON:', error);
              this.handleNonJsonResponse(result);
            }
          } else {
            // Handle as non-JSON response
            this.handleNonJsonResponse(result);
          }
        }
      }
    } catch (error) {
      console.error('Error calling Gemini API:', error);
      alert('Error communicating with AI service. Please try again.');
    } finally {
      this.isLoading = false;
    }
  }

  private handleNonJsonResponse(result: string) {
    // Try to extract score if present
    const scoreMatch = result.match(/(?:score|rating):\s*(\d+)/i);
    if (scoreMatch) {
      this.cvScore = parseInt(scoreMatch[1], 10);
    }

    // Try to extract suggestions
    const sections = result.split(/[\n\r]+/);
    const suggestions: CvSuggestion[] = [];
    let currentType: 'success' | 'warning' | 'info' | 'missing' = 'info';
    
    sections.forEach((section, index) => {
      if (section.toLowerCase().includes('strength') || section.includes('✓')) {
        currentType = 'success';
      } else if (section.toLowerCase().includes('improve') || section.toLowerCase().includes('weak') || section.includes('⚠')) {
        currentType = 'warning';
      } else if (section.toLowerCase().includes('missing') || section.includes('❌')) {
        currentType = 'missing';
      }

      if (section.trim() && !section.toLowerCase().includes('section') && section.length > 10) {
        suggestions.push({
          id: `suggestion_${index}`,
          type: currentType,
          title: currentType.charAt(0).toUpperCase() + currentType.slice(1),
          message: section.trim()
        });
      }
    });

    if (suggestions.length > 0) {
      this.cvSuggestions = suggestions;
    }

    this.improvedSummary = {
      overallAssessment: result.split('\n')[0] || 'Analysis completed',
      strengths: suggestions.filter(s => s.type === 'success').map(s => s.message),
      improvements: suggestions.filter(s => s.type === 'warning' || s.type === 'missing').map(s => s.message)
    };

    this.isAnalyzed = true;
  }
}