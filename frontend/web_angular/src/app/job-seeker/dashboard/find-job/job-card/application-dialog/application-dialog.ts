import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { JobOffer, JobSeeker } from '../../../../../types';
import { CvGenerationService, GenerateCVRequest, GenerateCVResponse } from '../../../../../services/cv-generation.service';
import { MockProfileService } from '../../../../../services/mock-profile.service';

@Component({
  selector: 'app-application-dialog',
  standalone: false,
  templateUrl: './application-dialog.html',
  styleUrls: ['./application-dialog.scss']
})
export class ApplicationDialog {
  generatedCV: GenerateCVResponse | null = null;
  coverLetter: string = '';
  isGenerating: boolean = false;
  uploadedFile: File | null = null;
  errorMessage: string = '';
  usedMockProfile: boolean = false;
  currentProfile: JobSeeker;

  constructor(
    public dialogRef: MatDialogRef<ApplicationDialog>,
    @Inject(MAT_DIALOG_DATA) public data: { job: JobOffer, profile: JobSeeker | null },
    private cvGenerationService: CvGenerationService,
    private mockProfileService: MockProfileService
  ) {
    // Déterminer quel profil utiliser (réel ou fictif)
    this.currentProfile = this.mockProfileService.getProfileForCV(data.job, data.profile);
    this.usedMockProfile = !this.mockProfileService.isProfileComplete(data.profile);
    
    console.log('Profile used:', this.usedMockProfile ? 'MOCK' : 'REAL', this.currentProfile);
  }

  /**
   * Génère un CV ATS optimisé via le microservice AI
   */
  async generateTailoredCV() {
    this.isGenerating = true;
    this.errorMessage = '';
    this.generatedCV = null;

    const request: GenerateCVRequest = {
      jobSeeker: this.currentProfile,
      jobOffer: this.data.job,
      format: 'ats'
    };

    console.log('Sending CV generation request:', request);

    try {
      this.cvGenerationService.generateATSCV(request).subscribe({
        next: (response) => {
          console.log('ATS CV generated successfully:', response);
          this.generatedCV = response;
          this.isGenerating = false;
          
          // Générer automatiquement le PDF
          this.generatePDF();
        },
        error: (error) => {
          console.error('CV generation error:', error);
          this.errorMessage = error.message;
          this.isGenerating = false;
          this.showFallbackCV();
        }
      });
    } catch (error) {
      console.error('Unexpected error:', error);
      this.errorMessage = 'Unexpected error during CV generation';
      this.isGenerating = false;
      this.showFallbackCV();
    }
  }

  /**
   * Génère le PDF à partir du CV ATS
   */
  generatePDF() {
    if (!this.generatedCV) {
      this.errorMessage = 'No CV generated yet. Please generate a CV first.';
      return;
    }

    try {
      this.cvGenerationService.generatePDF(
        this.generatedCV, 
        this.currentProfile, 
        this.data.job
      );
    } catch (error) {
      console.error('PDF generation error:', error);
      this.errorMessage = 'Failed to generate PDF. Please try again.';
    }
  }

  /**
   * Fallback en cas d'erreur
   */
  private showFallbackCV() {
    const fallbackCV: GenerateCVResponse = {
      sections: [
        {
          title: "Professional Summary",
          content: `Experienced ${this.currentProfile.title || 'professional'} with skills in ${this.currentProfile.skills.slice(0, 3).join(', ')}. Seeking ${this.data.job.title} position.`,
          order: 1
        },
        {
          title: "Work Experience",
          content: this.currentProfile.experience.map(exp => 
            `${exp.position} at ${exp.company}\n${exp.startDate} - ${exp.endDate}\n${exp.description}`
          ).join('\n\n'),
          order: 2
        },
        {
          title: "Education",
          content: this.currentProfile.education.map(edu =>
            `${edu.degree} in ${edu.field}\n${edu.school}, ${edu.graduationDate}`
          ).join('\n\n'),
          order: 3
        },
        {
          title: "Skills",
          content: this.currentProfile.skills.map(skill => `• ${skill}`).join('\n'),
          order: 4
        }
      ],
      summary: `Strong candidate for ${this.data.job.title} with relevant experience.`,
      optimizedSkills: this.currentProfile.skills,
      atsScore: 65,
      keywords: this.data.job.skills,
      rawContent: this.generateFallbackContent()
    };

    this.generatedCV = fallbackCV;
  }

  /**
   * Contenu fallback
   */
  private generateFallbackContent(): string {
    return `
${this.currentProfile.fullName}
${this.currentProfile.email} | ${this.currentProfile.phone_number || ''}

PROFESSIONAL SUMMARY
${this.currentProfile.description || 'Experienced professional'}

WORK EXPERIENCE
${this.currentProfile.experience.map(exp => 
  `${exp.position} at ${exp.company}
  ${exp.startDate} - ${exp.endDate}
  ${exp.description}`
).join('\n\n')}

EDUCATION
${this.currentProfile.education.map(edu => 
  `${edu.degree} in ${edu.field}
  ${edu.school}, ${edu.graduationDate}`
).join('\n\n')}

SKILLS
${this.currentProfile.skills.join(' • ')}
    `.trim();
  }

  onFileSelected(event: any) {
    this.uploadedFile = event.target.files[0] as File;
  }

  handleSubmit() {
    if (!this.generatedCV && !this.uploadedFile) {
      this.errorMessage = 'Please generate a CV or upload your own CV file.';
      return;
    }

    this.dialogRef.close({ 
      jobId: this.data.job.id, 
      generatedCV: this.generatedCV?.rawContent, 
      uploadedFile: this.uploadedFile,
      coverLetter: this.coverLetter,
      atsScore: this.generatedCV?.atsScore,
      usedMockProfile: this.usedMockProfile
    });
  }

  /**
   * Formate le contenu CV pour l'affichage
   */
  get formattedCV(): string {
    if (!this.generatedCV) return '';
    
    return this.generatedCV.sections
      .sort((a, b) => a.order - b.order)
      .map(section => `${section.title.toUpperCase()}\n${'='.repeat(section.title.length)}\n${section.content}`)
      .join('\n\n');
  }

  /**
   * Obtient la couleur du score ATS
   */
  get atsScoreColor(): string {
    if (!this.generatedCV) return 'gray';
    
    const score = this.generatedCV.atsScore;
    if (score >= 80) return 'green';
    if (score >= 60) return 'orange';
    return 'red';
  }
  /**
 * Imprime le CV
 */
printCV(): void {
  const printContent = document.getElementById('cv-preview');
  if (!printContent) {
    this.errorMessage = 'CV preview not found. Please generate a CV first.';
    return;
  }

  const printWindow = window.open('', '_blank');
  if (!printWindow) {
    this.errorMessage = 'Popup blocked! Please allow popups to print the CV.';
    return;
  }

  printWindow.document.write(`
    <!DOCTYPE html>
    <html>
    <head>
      <title>CV - ${this.currentProfile.fullName}</title>
      <style>
        body { 
          font-family: Arial, sans-serif; 
          line-height: 1.6; 
          margin: 20px; 
          color: #333;
        }
        .cv-content { max-width: 800px; margin: 0 auto; }
        .text-center { text-align: center; }
        .text-2xl { font-size: 24px; }
        .text-lg { font-size: 18px; }
        .text-md { font-size: 16px; }
        .text-sm { font-size: 14px; }
        .text-xs { font-size: 12px; }
        .font-bold { font-weight: bold; }
        .font-semibold { font-weight: 600; }
        .text-blue-600 { color: #2563eb; }
        .text-gray-700 { color: #374151; }
        .text-gray-500 { color: #6b7280; }
        .text-green-800 { color: #166534; }
        .text-green-600 { color: #16a34a; }
        .bg-green-50 { background-color: #f0fdf4; }
        .border-b { border-bottom: 1px solid #e5e7eb; }
        .border-t { border-top: 1px solid #e5e7eb; }
        .border-blue-200 { border-color: #bfdbfe; }
        .border-green-200 { border-color: #bbf7d0; }
        .mb-2 { margin-bottom: 8px; }
        .mb-4 { margin-bottom: 16px; }
        .mb-6 { margin-bottom: 24px; }
        .mt-6 { margin-top: 24px; }
        .p-3 { padding: 12px; }
        .p-6 { padding: 24px; }
        .pb-1 { padding-bottom: 4px; }
        .pb-4 { padding-bottom: 16px; }
        .pt-4 { padding-top: 16px; }
        .rounded { border-radius: 4px; }
        .whitespace-pre-line { white-space: pre-line; }
        .flex { display: flex; }
        .flex-wrap { flex-wrap: wrap; }
        .gap-1 { gap: 4px; }
        @media print {
          body { margin: 0; }
          .no-print { display: none; }
        }
      </style>
    </head>
    <body>
      ${printContent.innerHTML}
      <div class="no-print" style="margin-top: 20px; text-align: center;">
        <button onclick="window.print()" style="padding: 10px 20px; background: #2563eb; color: white; border: none; border-radius: 5px; cursor: pointer;">
          Print CV
        </button>
        <button onclick="window.close()" style="padding: 10px 20px; background: #6b7280; color: white; border: none; border-radius: 5px; cursor: pointer; margin-left: 10px;">
          Close
        </button>
      </div>
    </body>
    </html>
  `);
  
  printWindow.document.close();
}
}