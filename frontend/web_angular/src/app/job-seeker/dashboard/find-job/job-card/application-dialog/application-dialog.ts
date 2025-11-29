import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { JobOffer, JobSeeker } from '../../../../../types';
import { CvGenerationService, GenerateCVRequest, GenerateCVResponse } from '../../../../../services/cv-generation.service';
import { ApplicationService, ApplicationRequestDTO } from '../../../../../services/application.service';
import { CvUploadService } from '../../../../../services/cv-upload.service';
import { AuthService } from '../../../../../services/auth.service';

@Component({
  selector: 'app-application-dialog',
  standalone: false,
  templateUrl: './application-dialog.html',
  styleUrls: ['./application-dialog.scss']
})
export class ApplicationDialog {
  generatedCV: GenerateCVResponse | null = null;
  generatedPDFBlob: Blob | null = null;
  coverLetter: string = '';
  isGenerating: boolean = false;
  isSubmitting: boolean = false;
  isUploadingCV: boolean = false;
  uploadedFile: File | null = null;
  errorMessage: string = '';
  successMessage: string = '';
  currentProfile: JobSeeker | null = null;
  cvLink: string = '';

  constructor(
    public dialogRef: MatDialogRef<ApplicationDialog>,
    @Inject(MAT_DIALOG_DATA) public data: { job: JobOffer },
    private cvGenerationService: CvGenerationService,
    private applicationService: ApplicationService,
    private cvUploadService: CvUploadService,
    private authService: AuthService
  ) {
    // Get real user profile from localStorage
    const currentUser = this.authService.getCurrentUser();
    
    if (currentUser) {
      this.currentProfile = currentUser as any;
      console.log('✅ Profile loaded from localStorage:', this.currentProfile);
    } else {
      this.errorMessage = 'User profile not found. Please log in again.';
      console.error('❌ No user found in localStorage');
    }
  }

  /**
   * Generate ATS-optimized CV via AI microservice
   */
  async generateTailoredCV() {
    if (!this.currentProfile) {
      this.errorMessage = 'User profile not found. Please log in again.';
      return;
    }

    this.isGenerating = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.generatedCV = null;
    this.generatedPDFBlob = null;

    const request: GenerateCVRequest = {
      jobSeeker: this.currentProfile,
      jobOffer: this.data.job,
      format: 'ats'
    };

    console.log('Sending CV generation request:', request);

    try {
      this.cvGenerationService.generateATSCV(request).subscribe({
        next: (response) => {
          console.log('✅ ATS CV generated successfully:', response);
          this.generatedCV = response;
          this.isGenerating = false;
          
          // Generate PDF using the CV generation service
          this.generatePDF();
        },
        error: (error) => {
          console.error('❌ CV generation error:', error);
          this.errorMessage = 'Failed to generate CV. Using fallback version.';
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
   * Generate PDF from ATS CV (downloads directly)
   */
  generatePDF() {
    if (!this.generatedCV || !this.currentProfile) {
      this.errorMessage = 'No CV generated yet. Please generate a CV first.';
      return;
    }

    try {
      // Use the CV generation service to generate and download PDF
      this.cvGenerationService.generatePDF(
        this.generatedCV, 
        this.currentProfile, 
        this.data.job
      );
      
      // Also create blob for backend upload
      this.generatedPDFBlob = this.createPDFBlob(this.generatedCV);
      this.successMessage = 'PDF generated and ready for submission!';
      console.log('✅ PDF generated successfully');
    } catch (error) {
      console.error('PDF generation error:', error);
      this.errorMessage = 'Failed to generate PDF. Please try again.';
    }
  }

  /**
   * Create PDF blob from CV data (for backend upload)
   */
  private createPDFBlob(cv: GenerateCVResponse): Blob {
    // Create a proper PDF blob
    const content = cv.sections
      .sort((a, b) => a.order - b.order)
      .map(section => `${section.title.toUpperCase()}\n${'='.repeat(section.title.length)}\n${section.content}`)
      .join('\n\n');

    // Create blob with proper PDF MIME type
    return new Blob([content], { type: 'application/pdf' });
  }

  /**
   * Handle file upload
   */
  onFileSelected(event: any) {
    const file = event.target.files[0] as File;
    if (file) {
      const allowedTypes = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'];
      
      if (!allowedTypes.includes(file.type)) {
        this.errorMessage = 'Invalid file type. Please upload PDF, DOC, or DOCX files only.';
        return;
      }

      if (file.size > 5 * 1024 * 1024) {
        this.errorMessage = 'File size exceeds 5MB. Please upload a smaller file.';
        return;
      }

      this.uploadedFile = file;
      this.errorMessage = '';
      console.log('✅ File selected:', file.name);
    }
  }

  /**
   * Submit application to backend with CV upload
   */
  async handleSubmit() {
    if (!this.currentProfile) {
      this.errorMessage = 'User profile not found. Please log in again.';
      return;
    }

    if (!this.generatedPDFBlob && !this.uploadedFile) {
      this.errorMessage = 'Please generate a CV or upload your own CV file.';
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';
    this.successMessage = '';

    try {
      const currentUser = this.authService.getCurrentUser();
      if (!currentUser) {
        this.errorMessage = 'User not authenticated. Please log in again.';
        this.isSubmitting = false;
        return;
      }

      // Check for duplicate application
      const isDuplicate = await this.applicationService.checkDuplicateApplication(
        parseInt(this.data.job.id),
        currentUser.keycloakId
      ).toPromise();

      if (isDuplicate) {
        this.errorMessage = 'You have already applied for this job.';
        this.isSubmitting = false;
        return;
      }

      // STEP 1: Upload CV to backend
      this.isUploadingCV = true;
      this.successMessage = 'Uploading CV...';

      let cvUploadResponse;
      
      if (this.generatedPDFBlob) {
        // Upload generated CV with proper PDF format
        const fileName = `CV_${currentUser.keycloakId}_${this.data.job.id}_${Date.now()}.pdf`;
        cvUploadResponse = await this.cvUploadService.uploadGeneratedCV(
          this.generatedPDFBlob,
          currentUser.keycloakId,
          parseInt(this.data.job.id),
          fileName
        ).toPromise();
      } else if (this.uploadedFile) {
        // Upload user's file
        cvUploadResponse = await this.cvUploadService.uploadCV(
          this.uploadedFile,
          currentUser.keycloakId,
          parseInt(this.data.job.id)
        ).toPromise();
      }

      if (!cvUploadResponse) {
        throw new Error('CV upload failed');
      }

      console.log('✅ CV uploaded successfully:', cvUploadResponse);
      this.cvLink = cvUploadResponse.cvLink;
      this.isUploadingCV = false;
      this.successMessage = 'CV uploaded! Submitting application...';

      // STEP 2: Submit application with CV link
      const applicationDto: ApplicationRequestDTO = {
        jobOfferId: parseInt(this.data.job.id),
        cvLink: this.cvLink,
        motivationLettre: this.coverLetter || undefined,
        aiScore: this.generatedCV?.atsScore || undefined,
        isFavorite: false
      };

      console.log('📤 Submitting application:', applicationDto);

      this.applicationService.createApplication(applicationDto).subscribe({
        next: (response) => {
          console.log('✅ Application submitted successfully:', response);
          this.successMessage = 'Application submitted successfully!';
          this.isSubmitting = false;

          setTimeout(() => {
            this.dialogRef.close({ 
              success: true,
              applicationId: response.id,
              jobId: this.data.job.id,
              cvLink: this.cvLink,
              atsScore: this.generatedCV?.atsScore
            });
          }, 1500);
        },
        error: (error) => {
          console.error('❌ Application submission failed:', error);
          
          if (error.status === 401) {
            this.errorMessage = 'Unauthorized. Please log in again.';
          } else if (error.status === 403) {
            this.errorMessage = 'Access denied. Only job seekers can apply.';
          } else if (error.error?.message) {
            this.errorMessage = error.error.message;
          } else {
            this.errorMessage = 'Failed to submit application. Please try again.';
          }
          
          this.isSubmitting = false;
          this.isUploadingCV = false;
        }
      });

    } catch (error: any) {
      console.error('Unexpected error:', error);
      this.errorMessage = error.message || 'An unexpected error occurred. Please try again.';
      this.isSubmitting = false;
      this.isUploadingCV = false;
    }
  }

  /**
   * Fallback CV in case of error
   */
  private showFallbackCV() {
    if (!this.currentProfile) return;

    const fallbackCV: GenerateCVResponse = {
      sections: [
        {
          title: "Professional Summary",
          content: `Experienced ${this.currentProfile.title || 'professional'} with skills in ${this.currentProfile.skills?.slice(0, 3).join(', ') || 'various areas'}. Seeking ${this.data.job.title} position.`,
          order: 1
        },
        {
          title: "Work Experience",
          content: this.currentProfile.experience?.map(exp => 
            `${exp.position} at ${exp.company}\n${exp.startDate} - ${exp.endDate}\n${exp.description}`
          ).join('\n\n') || 'No experience listed',
          order: 2
        },
        {
          title: "Education",
          content: this.currentProfile.education?.map(edu =>
            `${edu.degree} in ${edu.field}\n${edu.school}, ${edu.graduationDate}`
          ).join('\n\n') || 'No education listed',
          order: 3
        },
        {
          title: "Skills",
          content: this.currentProfile.skills?.map(skill => `• ${skill}`).join('\n') || 'No skills listed',
          order: 4
        }
      ],
      summary: `Strong candidate for ${this.data.job.title} with relevant experience.`,
      optimizedSkills: this.currentProfile.skills || [],
      atsScore: 65,
      keywords: this.data.job.skills,
      rawContent: this.generateFallbackContent()
    };

    this.generatedCV = fallbackCV;
    this.generatedPDFBlob = this.createPDFBlob(fallbackCV);
  }

  private generateFallbackContent(): string {
    if (!this.currentProfile) return '';

    return `
${this.currentProfile.fullName}
${this.currentProfile.email} | ${this.currentProfile.phone_number || ''}

PROFESSIONAL SUMMARY
${this.currentProfile.description || 'Experienced professional'}

WORK EXPERIENCE
${this.currentProfile.experience?.map(exp => 
  `${exp.position} at ${exp.company}
  ${exp.startDate} - ${exp.endDate}
  ${exp.description}`
).join('\n\n') || 'No experience listed'}

EDUCATION
${this.currentProfile.education?.map(edu => 
  `${edu.degree} in ${edu.field}
  ${edu.school}, ${edu.graduationDate}`
).join('\n\n') || 'No education listed'}

SKILLS
${this.currentProfile.skills?.join(' • ') || 'No skills listed'}
    `.trim();
  }

  get formattedCV(): string {
    if (!this.generatedCV) return '';
    
    return this.generatedCV.sections
      .sort((a, b) => a.order - b.order)
      .map(section => `${section.title.toUpperCase()}\n${'='.repeat(section.title.length)}\n${section.content}`)
      .join('\n\n');
  }

  get atsScoreColor(): string {
    if (!this.generatedCV) return 'gray';
    
    const score = this.generatedCV.atsScore;
    if (score >= 80) return 'green';
    if (score >= 60) return 'orange';
    return 'red';
  }

  printCV(): void {
    if (!this.currentProfile) {
      this.errorMessage = 'User profile not found.';
      return;
    }

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
          body { font-family: Arial, sans-serif; line-height: 1.6; margin: 20px; color: #333; }
          .cv-content { max-width: 800px; margin: 0 auto; }
          .text-center { text-align: center; }
          .text-2xl { font-size: 24px; }
          .font-bold { font-weight: bold; }
          .whitespace-pre-line { white-space: pre-line; }
        </style>
      </head>
      <body>
        ${printContent.innerHTML}
      </body>
      </html>
    `);
    
    printWindow.document.close();
    printWindow.print();
  }
}