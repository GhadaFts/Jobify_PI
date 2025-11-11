import { Component, Input, Output, EventEmitter } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { JobDetailsDialog } from './job-details-dialog/job-details-dialog';
import { ApplicationDialog } from './application-dialog/application-dialog';
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

  // Méthode pour obtenir la couleur selon le statut
  getStatusColor(status: string): string {
    const colors: { [key: string]: string } = {
      'open': '#10B981', // Vert
      'new': '#3B82F6', // Bleu
      'hot job': '#DC2626', // Rouge
      'limited openings': '#F59E0B', // Orange
      'actively hiring': '#8B5CF6', // Violet
      'urgent hiring': '#EF4444' // Rouge vif
    };
    return colors[status] || '#6B7280'; // Gris par défaut
  }

  openDetailsModal(job: JobOffer) {
    const dialogRef = this.dialog.open(JobDetailsDialog, {
      width: '600px',
      data: { job },
    });

    dialogRef.afterClosed().subscribe(result => {
      // No apply logic here, only close
    });
  }

  openApplyModal(job: JobOffer) {
  const dialogRef = this.dialog.open(ApplicationDialog, {
    width: '700px',
    data: { job, profile: { /* Mock user profile data */ } },
  });

  dialogRef.afterClosed().subscribe((result: any) => {
    if (result) {
      // Émettre les données de candidature complètes
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