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
  currentPage: number = 1;
  totalPages: number = 0;
  pdfUrl: string;

  constructor(
    public dialogRef: MatDialogRef<ApplicationDetailsDialog>,
    @Inject(MAT_DIALOG_DATA) public data: { application: Application }
  ) {
    // Utiliser le CV du candidat ou un CV par défaut
    this.pdfUrl =  '/assets/pdf/test.pdf';
  }

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
    if (!dateString) return 'Non spécifié';
    return new Date(dateString).toLocaleDateString('fr-FR', {
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    });
  }

  getExperiencePeriod(exp: any): string {
    if (!exp.startDate) return 'Période non spécifiée';
    const endDate = exp.endDate ? this.formatDate(exp.endDate) : 'Présent';
    return `${this.formatDate(exp.startDate)} - ${endDate}`;
  }

  // Méthodes pour la navigation PDF
  onPdfLoad(pdf: any): void {
    this.totalPages = pdf.numPages;
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }
  onPdfError(error: any): void {
  console.error('Erreur lors du chargement du PDF:', error);
  // Vous pouvez ajouter un message d'erreur à l'utilisateur ici
 }
}