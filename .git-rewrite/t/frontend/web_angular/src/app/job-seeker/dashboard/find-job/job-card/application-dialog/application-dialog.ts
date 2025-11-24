import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { JobOffer, JobSeeker } from '../../../../../types';

@Component({
  selector: 'app-application-dialog',
  standalone: false,
  templateUrl: './application-dialog.html',
  styleUrls: ['./application-dialog.scss']
})
export class ApplicationDialog {
  generatedCV: string = '';
  coverLetter: string = '';
  isGenerating: boolean = false;
  uploadedFile: File | null = null;

  constructor(
    public dialogRef: MatDialogRef<ApplicationDialog>,
    @Inject(MAT_DIALOG_DATA) public data: { job: JobOffer, profile: JobSeeker }
  ) {}

  generateTailoredCV() {
    this.isGenerating = true;
    setTimeout(() => {
      const cv = `${this.data.profile.fullName}
${this.data.profile.title}
${this.data.profile.email} | ${this.data.profile.phone_number} 
${this.data.profile.nationality}

PROFESSIONAL SUMMARY
${this.data.profile.description}

KEY SKILLS RELEVANT TO ${this.data.job.title.toUpperCase()}
${this.data.profile.skills.filter((_: string, i: number) => i < 6).join(' • ')}

EXPERIENCE
${this.data.profile.experience.map((exp: { position: string; company: string; startDate: string; endDate: string; description: string }) => `
${exp.position} at ${exp.company}
${exp.startDate} - ${exp.endDate}
${exp.description}
`).join('\n')}

EDUCATION
${this.data.profile.education.map((edu: { degree: string; field: string; school: string; graduationDate: string }) => `
${edu.degree} in ${edu.field}
${edu.school}, ${edu.graduationDate}
`).join('\n')}

SOCIAL LINKS
${this.data.profile.github_link ? `GitHub: ${this.data.profile.github_link}` : ''}
${this.data.profile.web_link ? `Website: ${this.data.profile.web_link}` : ''}
${this.data.profile.twitter_link ? `Twitter: ${this.data.profile.twitter_link}` : ''}
${this.data.profile.facebook_link ? `Facebook: ${this.data.profile.facebook_link}` : ''}

---
This CV has been automatically optimized for the ${this.data.job.title} position at ${this.data.job.company}.
Key skills highlighted based on job requirements.`;

      this.generatedCV = cv;
      this.isGenerating = false;
      // Simulate PDF generation by offering a downloadable template
      const link = document.createElement('a');
      link.href = '/assets/cv-template.pdf'; // Place cv-template.pdf in assets
      link.download = `CV_${this.data.job.title}_${this.data.job.company}.pdf`;
      link.click();
    }, 1500);
  }

  onFileSelected(event: any) {
    this.uploadedFile = event.target.files[0] as File;
  }

  handleSubmit() {
    if (!this.generatedCV && !this.uploadedFile) {
      alert('Please generate your CV or upload a file first');
      return;
    }
    this.dialogRef.close({ 
      jobId: this.data.job.id, 
      generatedCV: this.generatedCV, 
      uploadedFile: this.uploadedFile,
      coverLetter: this.coverLetter 
    });
  }
}