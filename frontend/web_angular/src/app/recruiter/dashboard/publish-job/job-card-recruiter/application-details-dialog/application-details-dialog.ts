import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Application } from '../../../../../types';

@Component({
  selector: 'app-application-details-dialog',
  templateUrl: './application-details-dialog.html',
  styleUrls: ['./application-details-dialog.scss'],
  standalone: false
})
export class ApplicationDetailsDialog {
  constructor(
    public dialogRef: MatDialogRef<ApplicationDetailsDialog>,
    @Inject(MAT_DIALOG_DATA) public data: { application: Application }
  ) {}

  close(): void {
    this.dialogRef.close();
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'accepted': return 'bg-green-100 text-green-800';
      case 'rejected': return 'bg-red-100 text-red-800';
      case 'reviewed': return 'bg-blue-100 text-blue-800';
      default: return 'bg-yellow-100 text-yellow-800';
    }
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('fr-FR', {
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    });
  }

  getExperiencePeriod(exp: any): string {
    return `${this.formatDate(exp.startDate)} - ${this.formatDate(exp.endDate)}`;
  }
}