import { Component, Input, Output, EventEmitter, HostListener, OnChanges, SimpleChanges, OnInit, OnDestroy } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { JobOffer, Application } from '../../../../types';
import { RecruiterJobDetailsDialog } from './recruiter-job-details-dialog/recruiter-job-details-dialog';
import { EditJobDialog } from './edit-job-dialog/edit-job-dialog';
import { ApplicationDetailsDialog } from './application-details-dialog/application-details-dialog';
import { InterviewScheduleDialog } from './interview-schedule-dialog/interview-schedule-dialog';
import { TakeActionDialog } from './take-action-dialog/take-action-dialog';
import { ToastService } from '../../../../services/toast.service';
import { InterviewsService } from '../../../../services/interviews.service';
import { BookmarkService } from '../../../../services/bookmark.service';
import { Subscription } from 'rxjs';
import {
  AiService,
  JobOfferAIRequest,
  AIRankingResponse,
} from '../../../../ai-service/ai-service-backend';

@Component({
  selector: 'app-recruiter-job-card',
  standalone: false,
  templateUrl: './job-card-recruiter.html',
  styleUrls: ['./job-card-recruiter.scss'],
})
export class JobCardRecruiter implements OnChanges {
  @Input() job!: JobOffer;
  @Input() published: boolean = false;
  @Output() publish = new EventEmitter<string>();
  @Output() edit = new EventEmitter<JobOffer>();
  @Output() applicationStatusChange = new EventEmitter<{
    applicationId: string;
    newStatus: string;
    interviewData?: any;
  }>();

  showApplications = false;
  private statusChangeTimeout: any;
  // track which application's overflow menu is open (use string ids)
  openMenuFor: string | null = null;

  // Nouvelles propriétés pour AI Ranking et Favoris
  aiRankingEnabled = false;
  showFavoritesOnly = false;
  isRanking = false; // Pour l'animation de chargement
  private bookmarkSub?: Subscription;

  constructor(
    public dialog: MatDialog,
    private interviewsService: InterviewsService,
    private toastService: ToastService,
    private aiService: AiService,
    private bookmarkService: BookmarkService
  ) {}

  ngOnInit(): void {
    // Keep application.isFavorite in sync with bookmark cache
    this.bookmarkSub = this.bookmarkService.bookmarks$.subscribe((set) => {
      if (!this.job || !this.job.applications) return;
      const bookmarked = set.has(Number(this.job.id));
      this.job.applications.forEach((app) => (app.isFavorite = bookmarked));
    });
  }

  ngOnDestroy(): void {
    this.bookmarkSub?.unsubscribe();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['job'] && this.job && this.job.applications) {
      // mark applications as favorite if this job is bookmarked in the service cache
      const bookmarked = this.bookmarkService.isBookmarked(Number(this.job.id));
      this.job.applications.forEach((app) => {
        app.isFavorite = bookmarked;
      });
    }
  }

  getStatusColor(status: string): string {
    const colors: { [key: string]: string } = {
      open: '#10B981',
      new: '#3B82F6',
      'hot job': '#DC2626',
      'limited openings': '#F59E0B',
      'actively hiring': '#8B5CF6',
      'urgent hiring': '#EF4444',
    };
    return colors[status] || '#6B7280';
  }

  // Normalize status for comparisons (handle server enums like UPPER_CASE or mixed case)
  statusEquals(application: Application, status: string): boolean {
    if (!application || application.status === undefined || application.status === null) return false;
    return String(application.status).toLowerCase() === status;
  }

  statusNotIn(application: Application, statuses: string[]): boolean {
    if (!application || application.status === undefined || application.status === null) return true;
    const s = String(application.status).toLowerCase();
    return statuses.indexOf(s) === -1;
  }

  formatCount(count: number): string {
    return count >= 1000 ? (count / 1000).toFixed(1) + 'k' : count.toString();
  }

  toggleApplications(): void {
    this.showApplications = !this.showApplications;
  }

  getApplicationStatusClass(status: string): string {
    const s = (status || '').toLowerCase();
    switch (s) {
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
    const s = (status || '').toLowerCase();
    const statusTexts: { [key: string]: string } = {
      new: 'New',
      under_review: 'Under Review',
      interview_scheduled: 'Interview Scheduled',
      interview_annulled: 'Interview Annulled',
      offer_pending: 'Offer Pending',
      accepted: 'Accepted',
      rejected: 'Rejected',
    };
    return statusTexts[s] || status;
  }

  formatApplicationDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
      day: 'numeric',
      month: 'long',
      year: 'numeric',
    });
  }

  openApplicationDetails(application: Application): void {
    // Accept any casing coming from backend (NEW, new, New)
    if (String(application.status).toLowerCase() === 'new') {
      this.scheduleStatusChange(String(application.id));
    }

    const dialogRef = this.dialog.open(ApplicationDetailsDialog, {
      width: '80vw',
      maxWidth: '1800px',
      maxHeight: '90vh',
      data: { application },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (this.statusChangeTimeout) {
        clearTimeout(this.statusChangeTimeout);
      }
    });
  }

  private scheduleStatusChange(applicationId: string): void {
    if (this.statusChangeTimeout) {
      clearTimeout(this.statusChangeTimeout);
    }
    this.statusChangeTimeout = setTimeout(() => {
      this.applicationStatusChange.emit({
        applicationId: applicationId,
        newStatus: 'under_review',
      });
    }, 20000);
  }

  openInterviewSchedule(application: Application): void {
    const dialogRef = this.dialog.open(InterviewScheduleDialog, {
      width: '500px',
      maxWidth: '95vw',
      maxHeight: '90vh',
      data: { application },
    });
    console.log("application",application.jobSeeker);

    dialogRef.afterClosed().subscribe((result: any) => {
      if (result) {
        // Build interview payload expected by backend
        const scheduledDateTime = new Date(result.interviewDate + 'T' + result.interviewTime);

        // Map frontend interviewType to backend enum values
        const typeMap: { [key: string]: string } = {
          online: 'REMOTE',
          local: 'ON_SITE'
        };

        const pad = (n: number) => (n < 10 ? '0' + n : n);
        const y = scheduledDateTime.getFullYear();
        const mo = pad(scheduledDateTime.getMonth() + 1);
        const d = pad(scheduledDateTime.getDate());
        const hh = pad(scheduledDateTime.getHours());
        const mm = pad(scheduledDateTime.getMinutes());
        const localDateTime = `${y}-${mo}-${d}T${hh}:${mm}:00`;
        

        const payload = {
          applicationId: String(application.id),
          jobSeekerId: application.jobSeeker?.keycloakId,
          // recruiterId is set server-side from JWT by the interview-service
          scheduledDate: localDateTime,
          duration: result.duration,
          location: result.interviewLocation,
          interviewType: typeMap[result.interviewType] || 'REMOTE',
          notes: result.additionalNotes,
          meetingLink: result.meetingLink
        };

        // Call backend to persist the interview
        this.interviewsService.scheduleInterview(payload).subscribe({
          next: (response) => {
            // Update application status UI and notify parent
            this.applicationStatusChange.emit({
              applicationId: String(application.id),
              newStatus: 'interview_scheduled',
              interviewData: response,
            });

            this.toastService.success('Interview scheduled and saved.');
            console.log('Interview scheduled persisted:', response);
          },
          error: (err) => {
            console.error('Failed to persist interview:', err);
            let msg = 'Failed to schedule interview. Please try again.';
            if (err && err.error && err.error.message) msg = err.error.message;
            this.toastService.error(msg);
          }
        });
      }
    });
  }

  toggleMenu(application: Application): void {
    const id = String(application.id);
    this.openMenuFor = this.openMenuFor === id ? null : id;
  }

  closeMenu(): void {
    this.openMenuFor = null;
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    if (!this.openMenuFor) return;
    const target = event.target as HTMLElement;
    const menuSelector = `[data-app-menu-id="${this.openMenuFor}"]`;
    const toggleSelector = `[data-app-menu-toggle="${this.openMenuFor}"]`;
    if (target.closest(menuSelector) || target.closest(toggleSelector)) {
      return;
    }
    this.openMenuFor = null;
  }

  openTakeAction(application: Application): void {
    const dialogRef = this.dialog.open(TakeActionDialog, {
      width: '500px',
      data: { application },
    });

    dialogRef.afterClosed().subscribe((result: 'accepted' | 'rejected') => {
      if (result) {
        this.applicationStatusChange.emit({
          applicationId: String(application.id),
          newStatus: result,
        });
      }
    });
  }

  openDetailsModal(job: JobOffer): void {
    const dialogRef = this.dialog.open(RecruiterJobDetailsDialog, {
      width: '700px',
      maxWidth: '90vw',
      maxHeight: '90vh',
      data: { job },
    });

    dialogRef.afterClosed().subscribe((result) => {
      console.log('Modal details closed', result);
    });
  }

  openEditModal(job: JobOffer): void {
    const dialogRef = this.dialog.open(EditJobDialog, {
      width: '800px',
      maxWidth: '95vw',
      maxHeight: '90vh',
      data: { job },
    });

    dialogRef.afterClosed().subscribe((result: JobOffer) => {
      if (result) {
        this.edit.emit(result);
      }
    });
  }

  // NOUVELLES MÉTHODES POUR AI RANKING ET FAVORIS

  /**
   * Active le classement AI en utilisant le microservice backend
   */
  enableAIRanking(): void {
    if (!this.job.applications || this.job.applications.length === 0) {
      this.toastService.error('Aucune candidature à classer pour cette offre.');
      return;
    }

    this.isRanking = true;

    // Préparer les données pour l'API
    const inputData: JobOfferAIRequest = {
      id: this.job.id,
      title: this.job.title,
      company: this.job.company,
      location: this.job.location,
      type: this.job.type,
      experience: this.job.experience,
      salary: this.job.salary,
      description: this.job.description,
      skills: this.job.skills,
      requirements: this.job.requirements,
      applications: this.job.applications!.map((app) => ({
        id: app.id,
        applicationDate: app.applicationDate,
        status: app.status,
        motivation_lettre: app.motivation_lettre || '',
        jobSeeker: {
          id: app.jobSeeker.id,
          email: app.jobSeeker.email,
          fullName: app.jobSeeker.fullName,
          description: app.jobSeeker.description || '',
          nationality: app.jobSeeker.nationality || '',
          skills: app.jobSeeker.skills || [],
          experience: app.jobSeeker.experience || '',
          education: app.jobSeeker.education || '',
          title: app.jobSeeker.title || '',
          date_of_birth: app.jobSeeker.date_of_birth || '',
          gender: app.jobSeeker.gender || '',
        },
        jobOfferId: this.job.id,
      })),
    };

    // Optionnel: Valider les données avant envoi
    this.aiService.validateRankingRequest(inputData).subscribe({
      next: (validationResult) => {
        if (!validationResult.valid) {
          console.warn('Validation warnings:', validationResult.warnings);
          if (validationResult.errors.length > 0) {
            this.toastService.error(`Erreurs de validation: ${validationResult.errors.join(', ')}`);
            this.isRanking = false;
            return;
          }
        }

        // Appeler le microservice backend pour le ranking
        this.performAIRanking(inputData);
      },
      error: (validationError) => {
        console.error('Validation error:', validationError);
        // Continuer avec le ranking même si la validation échoue
        this.performAIRanking(inputData);
      },
    });
  }

  /**
   * Effectue le classement AI via le microservice
   */
  private performAIRanking(inputData: JobOfferAIRequest): void {
    this.aiService.rankApplications(inputData).subscribe({
      next: (response: AIRankingResponse) => {
        console.log('AI Ranking response:', response);

        // Attribuer les scores aux applications
        this.job.applications!.forEach((application) => {
          const scoredApp = response.applications.find((app) => app.id === application.id);
          application.aiScore = scoredApp?.score || 0;
        });

        // Trier par score décroissant
        this.job.applications!.sort((a, b) => (b.aiScore || 0) - (a.aiScore || 0));

        this.aiRankingEnabled = true;
        this.isRanking = false;

        // Calculer les statistiques pour le feedback
        const scores = response.applications.map((app) => app.score);
        const averageScore = scores.reduce((a, b) => a + b, 0) / scores.length;
        const maxScore = Math.max(...scores);

        this.toastService.success(
          `Classement AI terminé ! ${
            response.applications.length
          } candidatures évaluées. Score moyen: ${averageScore.toFixed(1)}/100`
        );
      },
      error: (error) => {
        console.error('AI Ranking error:', error);
        this.isRanking = false;

        let errorMessage = 'Erreur lors du classement AI. Veuillez réessayer.';

        if (error.status === 400) {
          errorMessage = 'Données invalides pour le classement AI.';
        } else if (error.status === 500) {
          errorMessage = 'Service AI temporairement indisponible.';
        } else if (error.status === 0) {
          errorMessage = 'Impossible de contacter le service AI. Vérifiez la connexion.';
        }

        this.toastService.error(errorMessage);

        // Fallback: utiliser le classement par date
        this.disableAIRanking();
      },
    });
  }

  /**
   * Désactive le classement AI et réinitialise l'ordre
   */
  disableAIRanking(): void {
    this.aiRankingEnabled = false;
    // Réinitialiser l'ordre par date de candidature
    if (this.job.applications) {
      this.job.applications.sort(
        (a, b) => new Date(b.applicationDate).getTime() - new Date(a.applicationDate).getTime()
      );
    }
  }

  /**
   * Vérifie la santé du service AI avant de lancer le ranking
   */
  checkAIServiceHealth(): void {
    this.aiService.checkRankingHealth().subscribe({
      next: (health) => {
        console.log('AI Service Health:', health);
        if (health.status === 'OK') {
          this.toastService.success('Service AI disponible et opérationnel');
        }
      },
      error: (error) => {
        console.error('AI Service Health check failed:', error);
        this.toastService.error(
          'Service AI temporairement indisponible - Le classement peut échouer'
        );
      },
    });
  }

  /**
   * Bascule l'affichage des favoris uniquement
   */
  toggleFavorites(): void {
    this.showFavoritesOnly = !this.showFavoritesOnly;
    if (this.showFavoritesOnly) {
      this.toastService.success(`Affichage des favoris uniquement (${this.favoritesCount})`);
    } else {
      this.toastService.success('Affichage de toutes les candidatures');
    }
  }

  /**
   * Ajoute/Retire une application des favoris
   */
  toggleFavorite(application: Application, event: Event): void {
    event.stopPropagation(); // Empêcher l'ouverture des détails

    // Optimistic UI update
    const previous = !!application.isFavorite;
    application.isFavorite = !previous;

    // Use BookmarkService to persist bookmark for this job offer.
    // Note: backend bookmarks are per jobOfferId. We toggle bookmark for the current job.
    this.bookmarkService.toggleBookmark(Number(this.job.id)).subscribe({
      next: () => {
        const action = application.isFavorite ? 'ajoutée aux' : 'retirée des';
        this.toastService.success(`Candidature ${action} favoris`);
      },
      error: (err) => {
        // Revert optimistic update on failure
        application.isFavorite = previous;
        console.error('Failed to toggle bookmark:', err);

        // Prefer server message if present
        let serverMsg = err && err.error && err.error.message ? err.error.message : null;
        let msg = serverMsg || 'Erreur lors de la mise à jour des favoris.';
        if (err && err.status === 403) {
          msg = "Action réservée aux candidats. Connectez-vous en tant que job seeker.";
        }
        this.toastService.error(msg);
      }
    });
  }

  /**
   * Obtient la liste filtrée des applications (avec ou sans favoris)
   */
  get filteredApplications(): Application[] {
    if (!this.job.applications) {
      return [];
    }

    if (this.showFavoritesOnly) {
      return this.job.applications.filter((app) => app.isFavorite);
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
    } else if (score >= 60) {
      return 'bg-orange-100 text-orange-800 border-orange-300';
    } else {
      return 'bg-gray-100 text-gray-800 border-gray-300';
    }
  }

  /**
   * Obtient le texte descriptif pour le score
   */
  getScoreText(score: number): string {
    if (score >= 90) return 'Excellent';
    if (score >= 80) return 'Très bon';
    if (score >= 70) return 'Bon';
    if (score >= 60) return 'Moyen';
    return 'À améliorer';
  }

  /**
   * Compte le nombre de favoris
   */
  get favoritesCount(): number {
    return this.job.applications?.filter((app) => app.isFavorite).length || 0;
  }

  /**
   * Obtient le score moyen des candidatures
   */
  get averageScore(): number {
    if (!this.job.applications || this.job.applications.length === 0) return 0;
    const total = this.job.applications.reduce((sum, app) => sum + (app.aiScore || 0), 0);
    return total / this.job.applications.length;
  }

  /**
   * Obtient le nombre de candidatures avec score élevé (>80)
   */
  get highQualityApplications(): number {
    return this.job.applications?.filter((app) => (app.aiScore || 0) >= 80).length || 0;
  }
}
