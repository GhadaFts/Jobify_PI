import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Application } from '../../../../../types';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from '../../../../../services/auth.service';

@Component({
  selector: 'app-application-details-dialog',
  templateUrl: './application-details-dialog.html',
  styleUrls: ['./application-details-dialog.scss'],
  standalone: false
})
export class ApplicationDetailsDialog implements OnDestroy {
  currentPage: number = 1;
  totalPages: number = 0;
  pdfUrl: string = '/assets/pdf/test.pdf';
  private objectUrl?: string;

  constructor(
    public dialogRef: MatDialogRef<ApplicationDetailsDialog>,
    @Inject(MAT_DIALOG_DATA) public data: { application: Application },
    private http: HttpClient,
    private authService: AuthService
  ) {
    // Use candidate CV if present, otherwise a default PDF
    const raw = (data?.application as any)?.cv_link || (data?.application as any)?.cvLink || (data?.application as any)?.cv || '';
    if (!raw) {
      this.pdfUrl = '/assets/pdf/test.pdf';
    } else if (/^https?:\/\//i.test(raw)) {
      // absolute url - use directly
      this.pdfUrl = raw;
    } else {
      // filename returned by application-service: use authenticated fetch to get a blob
      this.fetchCvAsBlob(raw).catch((err) => {
        console.error('Failed to load CV blob, falling back to default PDF', err);
        this.pdfUrl = '/assets/pdf/test.pdf';
      });
    }
  }

  async fetchCvAsBlob(filename: string): Promise<void> {
    try {
      const token = this.authService.getAccessToken();
      const headers = token ? new HttpHeaders({ Authorization: `Bearer ${token}` }) : new HttpHeaders();
      const url = `http://localhost:8888/application-service/api/cv/view/${encodeURIComponent(filename)}`;
      const blob = await this.http.get(url, { headers, responseType: 'blob' as 'blob' }).toPromise();
      // ensure we actually received a Blob
      if (!blob || !(blob instanceof Blob)) {
        throw new Error('CV fetch returned no blob');
      }
      // create an object URL so the pdf viewer can load it
      if (this.objectUrl) {
        URL.revokeObjectURL(this.objectUrl);
      }
      this.objectUrl = URL.createObjectURL(blob as Blob);
      this.pdfUrl = this.objectUrl;
    } catch (e) {
      throw e;
    }
  }

  ngOnDestroy(): void {
    if (this.objectUrl) {
      URL.revokeObjectURL(this.objectUrl);
      this.objectUrl = undefined;
    }
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

  // Normalize motivation letter field coming either as snake_case or camelCase
  getMotivation(application: any): string {
    if (!application) return 'Aucune lettre de motivation fournie.';
    const motiv = application.motivation_lettre || (application as any).motivationLettre || application.motivation || '';
    return motiv || 'Aucune lettre de motivation fournie.';
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