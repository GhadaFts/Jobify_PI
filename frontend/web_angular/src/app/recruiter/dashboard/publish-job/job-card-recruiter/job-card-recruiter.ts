import { Component, Input, Output, EventEmitter } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { JobOffer } from '../../../../types';
import { RecruiterJobDetailsDialog } from './recruiter-job-details-dialog/recruiter-job-details-dialog';
import { EditJobDialog } from './edit-job-dialog/edit-job-dialog';

@Component({
  selector: 'app-recruiter-job-card',
  standalone: false,
  templateUrl: './job-card-recruiter.html',
  styleUrls: ['./job-card-recruiter.scss']
})
export class JobCardRecruiter {
  @Input() job!: JobOffer;
  @Input() published: boolean = false;
  @Output() publish = new EventEmitter<string>();
  @Output() edit = new EventEmitter<JobOffer>(); 


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

  formatCount(count: number): string {
    return count >= 1000 ? (count / 1000).toFixed(1) + 'k' : count.toString();
  }

  openDetailsModal(job: JobOffer): void {
    const dialogRef = this.dialog.open(RecruiterJobDetailsDialog, {
      width: '700px',
      maxWidth: '90vw',
      maxHeight: '90vh',
      data: { job }
    });

    dialogRef.afterClosed().subscribe(result => {
      // Logique après fermeture de la modal si nécessaire
      console.log('Modal details closed', result);
    });
  }
   openEditModal(job: JobOffer): void {
    const dialogRef = this.dialog.open(EditJobDialog, {
      width: '800px',
      maxWidth: '95vw',
      maxHeight: '90vh',
      data: { job }
    });

    dialogRef.afterClosed().subscribe((result: JobOffer) => {
      if (result) {
        this.edit.emit(result); // Émet le job modifié
      }
    });
  }
}