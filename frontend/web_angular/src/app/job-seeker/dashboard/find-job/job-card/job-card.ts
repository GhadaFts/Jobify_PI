import { Component, Input, Output, EventEmitter } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { JobDetailsDialog } from './job-details-dialog/job-details-dialog';
import { ApplicationDialog } from './application-dialog/application-dialog';
import { CompanyProfileDialog } from './company-profile-dialog/company-profile-dialog'; // À créer
import { JobOffer } from '../../../../types';

@Component({
  selector: 'app-job-card',
  standalone: false,
  templateUrl: './job-card.html',
  styleUrls: ['./job-card.scss']
})
export class JobCard {
  @Input() job!: JobOffer;
  @Input() applied: boolean = false;
  @Input() bookmarked: boolean = false;
  @Output() apply = new EventEmitter<{
    jobId: string;
    generatedCV?: string;
    uploadedFile?: File;
    coverLetter?: string;
  }>(); 
  @Output() bookmark = new EventEmitter<string>();

  constructor(public dialog: MatDialog) {}

  // Méthode pour ouvrir le profil de l'entreprise
  openCompanyProfile(companyName: string) {
    const dialogRef = this.dialog.open(CompanyProfileDialog, {
      width: '800px',
      maxWidth: '90vw',
      maxHeight: '90vh',
      data: { companyName: companyName }
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('Company profile dialog closed');
    });
  }

  // Les autres méthodes restent inchangées
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

    dialogRef.afterClosed().subscribe(result => {});
  }

  openApplyModal(job: JobOffer) {
    const dialogRef = this.dialog.open(ApplicationDialog, {
      width: '700px',
      data: { 
        job: job, 
        profile: null
      },
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      if (result) {
        if (result.usedMockProfile) {
          console.log('Application submitted with mock profile');
        }
        
        this.apply.emit({
          jobId: job.id,
          generatedCV: result.generatedCV,
          uploadedFile: result.uploadedFile,
          coverLetter: result.coverLetter
        });
      }
    });
  }

  formatCount(count: number): string {
    return count >= 1000 ? (count / 1000).toFixed(1) + 'k' : count.toString();
  }
}