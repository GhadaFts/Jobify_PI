import { Component, Input, Output, EventEmitter } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { JobDetailsDialog } from '../job-card/job-details-dialog/job-details-dialog';
import { JobOffer } from '../../../../types';
import { CvUploadService } from '../../../../services/cv-upload.service';

interface ApplicationData {
  generatedCV?: string;
  uploadedFile?: File;
  coverLetter?: string;
  applicationDate: Date;
  applicationId?: string;
  status?: string;
  aiScore?: number;
  cvLink?: string;
}

@Component({
  selector: 'app-applied-job-card',
  standalone: false,
  templateUrl: './applied-job-card.html',
  styleUrls: ['./applied-job-card.scss']
})
export class AppliedJobCard {
  @Input() job!: JobOffer;
  @Input() applicationData!: ApplicationData;
  @Output() withdraw = new EventEmitter<string>();

  constructor(
    public dialog: MatDialog,
    private cvUploadService: CvUploadService
  ) {}

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

  getApplicationStatusColor(status?: string): string {
    const colors: { [key: string]: string } = {
      'NEW': '#3B82F6',
      'UNDER_REVIEW': '#F59E0B',
      'SHORTLISTED': '#8B5CF6',
      'INTERVIEW_SCHEDULED': '#10B981',
      'REJECTED': '#DC2626',
      'ACCEPTED': '#059669',
      'WITHDRAWN': '#6B7280'
    };
    return colors[status || 'NEW'] || '#6B7280';
  }

  openDetailsModal(job: JobOffer) {
    this.dialog.open(JobDetailsDialog, {
      width: '600px',
      data: { job },
    });
  }

  withdrawApplication() {
    this.withdraw.emit(this.job.id);
  }

  getCVUrl(): string {
    if (this.applicationData.cvLink) {
      return this.cvUploadService.getCVUrl(this.applicationData.cvLink);
    }
    return '/assets/pdf/ghada.pdf';
  }

  hasCVInBackend(): boolean {
    return !!this.applicationData.cvLink;
  }

  /**
   * Download CV using HttpClient (goes through auth interceptor)
   */
  downloadCV() {
    if (!this.applicationData.cvLink) {
      // Fallback to default CV
      const link = document.createElement('a');
      link.href = '/assets/pdf/ghada.pdf';
      link.download = 'CV.pdf';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      return;
    }

    console.log('📥 Downloading CV:', this.applicationData.cvLink);

    // Use HttpClient through the service - auth headers added by interceptor
    this.cvUploadService.downloadCVBlob(this.applicationData.cvLink).subscribe({
      next: (blob) => {
        // Create download link
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = this.getCVFileName();
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        
        // Clean up
        setTimeout(() => window.URL.revokeObjectURL(url), 100);
        
        console.log('✅ CV downloaded successfully');
      },
      error: (error) => {
        console.error('❌ Error downloading CV:', error);
        
        // Better error messages
        if (error.status === 401) {
          alert('Session expired. Please login again.');
        } else if (error.status === 403) {
          alert('You do not have permission to download this CV.');
        } else if (error.status === 404) {
          alert('CV file not found on server.');
        } else if (error.status === 0) {
          alert('Network error. Please check your connection or disable ad blockers.');
        } else {
          alert(`Failed to download CV: ${error.message || 'Unknown error'}`);
        }
      }
    });
  }

  /**
   * View CV using HttpClient (goes through auth interceptor)
   */
  viewCV() {
    if (!this.applicationData.cvLink) {
      // Fallback to default CV
      window.open('/assets/pdf/ghada.pdf', '_blank', 'noopener,noreferrer');
      return;
    }

    console.log('👁️ Viewing CV:', this.applicationData.cvLink);

    // Use HttpClient through the service - auth headers added by interceptor
    this.cvUploadService.downloadCVBlob(this.applicationData.cvLink).subscribe({
      next: (blob) => {
        // Create blob URL and open in new tab
        const url = window.URL.createObjectURL(blob);
        const newWindow = window.open(url, '_blank', 'noopener,noreferrer');
        
        if (!newWindow) {
          alert('Popup blocked. Please allow popups for this site.');
          window.URL.revokeObjectURL(url);
          return;
        }
        
        // Clean up after window loads
        setTimeout(() => window.URL.revokeObjectURL(url), 100);
        
        console.log('✅ CV opened successfully');
      },
      error: (error) => {
        console.error('❌ Error viewing CV:', error);
        
        // Better error messages
        if (error.status === 401) {
          alert('Session expired. Please login again.');
        } else if (error.status === 403) {
          alert('You do not have permission to view this CV.');
        } else if (error.status === 404) {
          alert('CV file not found on server.');
        } else if (error.status === 0) {
          alert('Network error. Please check your connection or disable ad blockers.');
        } else {
          alert(`Failed to view CV: ${error.message || 'Unknown error'}`);
        }
      }
    });
  }

  getCVFileName(): string {
    if (this.applicationData.cvLink) {
      return this.applicationData.cvLink;
    }
    return 'CV.pdf';
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

  formatStatus(status?: string): string {
    if (!status) return 'New';
    return status.replace(/_/g, ' ').toLowerCase()
      .split(' ')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1))
      .join(' ');
  }
}