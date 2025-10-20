import { Component } from '@angular/core';
import { CommonModule } from '@angular/common'; // For structural directives like *ngIf
import { UserProfile } from '../../../types'; // Updated import path based on your structure

@Component({
  selector: 'app-cv-correction',
  templateUrl: './cv-correction.html',
  styleUrls: ['./cv-correction.scss'],
  standalone: false,
})
export class CvCorrection {
  currentStep = 1;
  progress = 0;
  cvFile: File | null = null;
  isDragging = false;
  isAnalyzed = false;
  cvScore: number | null = null;
  cvSuggestions: { type: string; title: string; message: string }[] = [];
  profile: UserProfile = {
    name: 'John Doe',
    title: 'Software Engineer',
    email: 'john.doe@example.com',
    phone: '+1-555-123-4567',
    nationality: 'American', // Remplace location
    dateOfBirth: '1990-05-15', // Ajouté
    summary: 'Experienced software engineer with a focus on web development and team collaboration.',
    skills: ['JavaScript', 'React', 'Node.js'],
    experience: [
      {
        position: 'Senior Developer',
        company: 'Tech Corp',
        startDate: '2022-01',
        endDate: '2025-10',
        description: 'Led a team of developers to build scalable web applications.'
      },
      {
        position: 'Junior Developer',
        company: 'StartUp Inc',
        startDate: '2020-06',
        endDate: '2021-12',
        description: 'Contributed to frontend development using React and Redux.'
      }
    ],
    education: [
      {
        degree: 'BSc',
        field: 'Computer Science',
        school: 'University of Tech',
        graduationDate: '2020-06'
      }
    ]
  };

  onDragOver(event: DragEvent) {
    event.preventDefault();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    this.isDragging = false;
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    this.isDragging = false;
    if (event.dataTransfer?.files && event.dataTransfer.files.length > 0) {
      this.cvFile = event.dataTransfer.files[0];
    }
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.cvFile = input.files[0];
    }
  }

  removeFile() {
    this.cvFile = null;
    this.isAnalyzed = false;
    this.cvScore = null;
    this.cvSuggestions = [];
  }

  analyzeCV() {
    if (this.cvFile) {
      // Placeholder for AI analysis logic
      this.isAnalyzed = true;
      this.cvScore = 75; // Default score, to be replaced with actual analysis
      this.cvSuggestions = [
        { type: 'success', title: 'Strong Skills Section', message: 'You have a comprehensive list of relevant technical skills.' },
        { type: 'warning', title: 'Add More Quantifiable Achievements', message: 'Consider adding metrics and numbers to your experience descriptions (e.g., "Improved performance by 40%").' },
        { type: 'warning', title: 'Expand Professional Summary', message: 'Your summary could be more detailed. Aim for 3-4 sentences highlighting your key achievements.' },
        { type: 'success', title: 'Recent Experience', message: 'Your work experience is up-to-date and relevant.' },
        { type: 'info', title: 'Consider Adding Certifications', message: 'Industry certifications can strengthen your profile. Consider adding any relevant certifications.' }
      ];
      // Update profile with mock data (to be replaced with CV parsing)
      this.profile = {
        ...this.profile,
        name: 'John Doe',
        title: 'Software Engineer',
        email: 'john.doe@example.com',
        phone: '+1-555-123-4567',
        nationality: 'American', // Changé de location
        dateOfBirth: '1990-05-15', // Ajouté
        summary: 'Experienced software engineer with a focus on web development and team collaboration.',
        skills: ['JavaScript', 'React', 'Node.js'],
        experience: [
          {
            position: 'Senior Developer',
            company: 'Tech Corp',
            startDate: '2022-01',
            endDate: '2025-10',
            description: 'Led a team of developers to build scalable web applications.'
          },
          {
            position: 'Junior Developer',
            company: 'StartUp Inc',
            startDate: '2020-06',
            endDate: '2021-12',
            description: 'Contributed to frontend development using React and Redux.'
          }
        ],
        education: [
          {
            degree: 'BSc',
            field: 'Computer Science',
            school: 'University of Tech',
            graduationDate: '2020-06'
          }
        ]
      };
    }
  }
}