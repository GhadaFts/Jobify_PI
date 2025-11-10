import { Component, Input, Output, EventEmitter } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { JobDetailsDialog } from '../job-card/job-details-dialog/job-details-dialog';
import { JobOffer } from '../../../../types';
interface ApplicationData {
  generatedCV?: string;
  uploadedFile?: File;
  coverLetter?: string;
  applicationDate: Date;
}
@Component({
  selector: 'app-applied-job-card',
  standalone: false,
  templateUrl: './applied-job-card.html',
  styleUrls: ['./applied-job-card.scss']
})
export class AppliedJobCard {
  @Input() job!: JobOffer;
  @Input() applicationData!: {
    generatedCV?: string;
    uploadedFile?: File;
    coverLetter?: string;
    applicationDate: Date;
  };
  @Output() withdraw = new EventEmitter<string>();

  constructor(public dialog: MatDialog) {}

  getStatusColor(status: string): string {
    const colors: { [key: string]: string } = {
      'open': '#10B981',
      'new': '#3B82F6',
      'hot job': '#DC2626',
      'limited openings': '#F59E0B',
      'actively hiring': '#8B5CF6',
      'urgent hiring': '#EF4444'
    };
    return colors[status] || '#6B7280';
  }

  openDetailsModal(job: JobOffer) {
    const dialogRef = this.dialog.open(JobDetailsDialog, {
      width: '600px',
      data: { job },
    });
  }

  withdrawApplication() {
    this.withdraw.emit(this.job.id);
  }

  getCVUrl(): string {
    // Si un fichier a été uploadé, créer une URL temporaire
    if (this.applicationData.uploadedFile) {
      return URL.createObjectURL(this.applicationData.uploadedFile);
    }
    // Sinon, utiliser le CV par défaut
    return '/assets/pdf/ghada.pdf';
  }

  downloadGeneratedCV() {
    if (this.applicationData.generatedCV) {
      const blob = new Blob([this.applicationData.generatedCV], { type: 'text/plain' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `CV_${this.job.company}_${this.job.title}.txt`;
      a.click();
      URL.revokeObjectURL(url);
    }
  }

  formatCount(count: number): string {
    return count >= 1000 ? (count / 1000).toFixed(1) + 'k' : count.toString();
  }
  getSafeApplicationData(): ApplicationData {
    return this.applicationData || {
      applicationDate: new Date(),
      generatedCV: '',
      coverLetter: '',
      uploadedFile: undefined
    };
  }
}