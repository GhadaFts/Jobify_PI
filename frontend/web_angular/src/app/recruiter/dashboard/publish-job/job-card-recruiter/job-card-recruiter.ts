import { Component, Input, Output, EventEmitter } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { JobOffer, Application } from '../../../../types';
import { RecruiterJobDetailsDialog } from './recruiter-job-details-dialog/recruiter-job-details-dialog';
import { EditJobDialog } from './edit-job-dialog/edit-job-dialog';
import { ApplicationDetailsDialog } from './application-details-dialog/application-details-dialog';
import { InterviewScheduleDialog } from './interview-schedule-dialog/interview-schedule-dialog';
import { TakeActionDialog } from './take-action-dialog/take-action-dialog';

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
  @Output() applicationStatusChange = new EventEmitter<{applicationId: number, newStatus: string, interviewData?: any}>();

  showApplications = false;
  private statusChangeTimeout: any;
  
  // Nouvelles propriétés pour AI Ranking et Favoris
  aiRankingEnabled = false;
  showFavoritesOnly = false;
  isRanking = false; // Pour l'animation de chargement

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

  toggleApplications(): void {
    this.showApplications = !this.showApplications;
  }

  getApplicationStatusClass(status: string): string {
    switch (status) {
      case 'new':
        return 'bg-blue-100 text-blue-800 border-blue-200';
      case 'under_review':
        return 'bg-yellow-100 text-yellow-800 border-yellow-200';
      case 'interview_scheduled':
        return 'bg-purple-100 text-purple-800 border-purple-200';
      case 'interview_annulled':
        return 'bg-gray-100 text-gray-800 border-gray-200';
      case 'offer_pending':
        return 'bg-teal-100 text-teal-800 border-teal-200';
      case 'accepted':
        return 'bg-green-100 text-green-800 border-green-200';
      case 'rejected':
        return 'bg-red-100 text-red-800 border-red-200';
      default:
        return 'bg-gray-100 text-gray-800 border-gray-200';
    }
  }

  getApplicationStatusText(status: string): string {
    const statusTexts: { [key: string]: string } = {
      'new': 'New',
      'under_review': 'Under Review',
      'interview_scheduled': 'Interview Scheduled',
      'interview_annulled': 'Interview Annulled',
      'offer_pending': 'Offer Pending',
      'accepted': 'Accepted',
      'rejected': 'Rejected'
    };
    return statusTexts[status] || status;
  }

  formatApplicationDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    });
  }

  openApplicationDetails(application: Application): void {
    if (application.status === 'new') {
      this.scheduleStatusChange(application.id);
    }

    const dialogRef = this.dialog.open(ApplicationDetailsDialog, {
      width: '800px',
      maxWidth: '95vw',
      maxHeight: '90vh',
      data: { application }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (this.statusChangeTimeout) {
        clearTimeout(this.statusChangeTimeout);
      }
    });
  }

  private scheduleStatusChange(applicationId: number): void {
    this.statusChangeTimeout = setTimeout(() => {
      this.applicationStatusChange.emit({
        applicationId: applicationId,
        newStatus: 'under_review'
      });
    }, 20000);
  }

  openInterviewSchedule(application: Application): void {
    const dialogRef = this.dialog.open(InterviewScheduleDialog, {
      width: '500px',
      data: { application }
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      if (result) {
        this.applicationStatusChange.emit({
          applicationId: application.id,
          newStatus: 'interview_scheduled',
          interviewData: result
        });
      }
    });
  }

  openTakeAction(application: Application): void {
    const dialogRef = this.dialog.open(TakeActionDialog, {
      width: '500px',
      data: { application }
    });

    dialogRef.afterClosed().subscribe((result: 'accepted' | 'rejected') => {
      if (result) {
        this.applicationStatusChange.emit({
          applicationId: application.id,
          newStatus: result
        });
      }
    });
  }

  openDetailsModal(job: JobOffer): void {
    const dialogRef = this.dialog.open(RecruiterJobDetailsDialog, {
      width: '700px',
      maxWidth: '90vw',
      maxHeight: '90vh',
      data: { job }
    });

    dialogRef.afterClosed().subscribe(result => {
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
        this.edit.emit(result);
      }
    });
  }

  // NOUVELLES MÉTHODES POUR AI RANKING ET FAVORIS

  /**
   * Active le classement AI et génère des scores aléatoires
   */
  enableAIRanking(): void {
    if (!this.job.applications || this.job.applications.length === 0) {
      return;
    }

    this.isRanking = true;

    // Simulation d'un délai de traitement AI (1.5 secondes)
    setTimeout(() => {
      // Attribuer des scores aléatoires à chaque application
      this.job.applications!.forEach(application => {
        if (!application.aiScore) {
          application.aiScore = Math.floor(Math.random() * 30) + 70; // Score entre 70 et 100
        }
      });

      // Trier par score décroissant
      this.job.applications!.sort((a, b) => (b.aiScore || 0) - (a.aiScore || 0));

      this.aiRankingEnabled = true;
      this.isRanking = false;
    }, 1500);
  }

  /**
   * Désactive le classement AI et réinitialise l'ordre
   */
  disableAIRanking(): void {
    this.aiRankingEnabled = false;
    // Réinitialiser l'ordre par date de candidature
    if (this.job.applications) {
      this.job.applications.sort((a, b) => 
        new Date(b.applicationDate).getTime() - new Date(a.applicationDate).getTime()
      );
    }
  }

  /**
   * Bascule l'affichage des favoris uniquement
   */
  toggleFavorites(): void {
    this.showFavoritesOnly = !this.showFavoritesOnly;
  }

  /**
   * Ajoute/Retire une application des favoris
   */
  toggleFavorite(application: Application, event: Event): void {
    event.stopPropagation(); // Empêcher l'ouverture des détails
    application.isFavorite = !application.isFavorite;
  }

  /**
   * Obtient la liste filtrée des applications (avec ou sans favoris)
   */
  get filteredApplications(): Application[] {
    if (!this.job.applications) {
      return [];
    }

    if (this.showFavoritesOnly) {
      return this.job.applications.filter(app => app.isFavorite);
    }

    return this.job.applications;
  }

  /**
   * Obtient la classe CSS pour le badge de score
   */
  getScoreBadgeClass(score: number): string {
    if (score >= 90) {
      return 'bg-green-100 text-green-800 border-green-300';
    } else if (score >= 80) {
      return 'bg-blue-100 text-blue-800 border-blue-300';
    } else if (score >= 70) {
      return 'bg-yellow-100 text-yellow-800 border-yellow-300';
    } else {
      return 'bg-gray-100 text-gray-800 border-gray-300';
    }
  }

  /**
   * Compte le nombre de favoris
   */
  get favoritesCount(): number {
    return this.job.applications?.filter(app => app.isFavorite).length || 0;
  }
}