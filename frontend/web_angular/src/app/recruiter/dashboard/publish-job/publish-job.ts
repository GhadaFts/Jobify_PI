import { Component } from '@angular/core';
import { JobOffer, Application } from '../../../types';
import { JobOfferService } from '../../../services/job-offer.service';
import { AuthService } from '../../../services/auth.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ApplicationService } from '../../../services/application.service';
import { ToastService } from '../../../services/toast.service';
import { 
  faCheck, 
  faTimes, 
  faPlus, 
  faTag, 
  faListCheck, 
  faPaperPlane 
} from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-publish-job',
  standalone: false,
  templateUrl: './publish-job.html',
  styleUrls: ['./publish-job.scss']
})
export class PublishJob {
  isPublishing: boolean = false;
  activeTab: string = 'all';
  searchQuery: string = '';
  isComposerExpanded: boolean = false;
  newSkill: string = '';
  newRequirement: string = '';

  faTag = faTag;
  faListCheck = faListCheck;
  faCheck = faCheck;
  faTimes = faTimes;
  faPlus = faPlus;
  faPaperPlane = faPaperPlane;
  faSpinner = faPaperPlane; // placeholder, will set spinner icon in template via CSS if needed

  newJob: Partial<JobOffer> = {
    title: '',
    company: '',
    location: '',
    type: '',
    experience: '',
    salary: '',
    description: '',
    skills: [],
    requirements: [], 
    status: 'open',
    published: false,
    applicants: 0,
    posted: 'Just now'
  };

  // file upload fields
  logoFile?: File;
  logoPreview?: string;
  logoUrl?: string;

  constructor(
    private jobService: JobOfferService,
    private toastService: ToastService,
    private applicationService: ApplicationService,
    private authService: AuthService,
    private http: HttpClient
  ) {}

  jobs: JobOffer[] = [];
  isUpdating: boolean = false;
  ngOnInit(): void {
    this.loadMyJobs();
  }

  private loadMyJobs(): void {
    this.jobService.getMyJobs().subscribe({
      next: (res: any) => {
        // expect res to be an array of job DTOs
        if (Array.isArray(res)) {
          this.jobs = res.map((created: any) => ({
            id: created.id ? String(created.id) : Date.now().toString(),
            title: created.title || '',
            company: created.company || '',
            // Normalize companyLogo: if backend returns a filename or relative path, prefix with gateway+service path
            companyLogo: ((): string => {
              const raw = created.companyLogo || '';
              if (!raw) return '';
              // if already absolute URL, return as-is
              if (/^https?:\/\//i.test(raw)) return raw;
              // if starts with /, assume it's already a root path like /uploads/...
              if (raw.startsWith('/')) return `http://localhost:8888/joboffer-service${raw}`;
              // otherwise assume it's a filename or relative path under uploads
              if (raw.startsWith('uploads/') || raw.includes('company-logos')) {
                return `http://localhost:8888/joboffer-service/${raw}`;
              }
              // otherwise treat as filename
              return `http://localhost:8888/joboffer-service/uploads/company-logos/${raw}`;
            })(),
            location: created.location || '',
            type: created.type || '',
            experience: created.experience || '',
            salary: created.salary || '',
            description: created.description || '',
            skills: created.skills || [],
            requirements: created.requirements || [],
            posted: created.posted || '',
            applicants: created.applicants || 0,
            status: created.status || 'open',
            published: !!created.published,
            applications: created.applications || []
          }));

          // Fetch applications for each job and attach them so recruiter UI can display them
          this.jobs.forEach((job) => {
            if (!job.id) return;
            this.applicationService.getByJobOffer(job.id).subscribe({
              next: (apps: any[]) => {
                job.applications = apps || [];
                job.applicants = (apps && apps.length) || 0;

                // For each application, try to enrich with job seeker profile from auth-service
                job.applications.forEach((app: any) => {
                  const seekerId = app.jobSeekerId || app.jobSeeker?.id;
                  if (!seekerId) return;

                  // Use stored access token if available
                  const token = this.authService.getAccessToken();
                  const headers = token
                    ? new HttpHeaders({ Authorization: `Bearer ${token}` })
                    : new HttpHeaders();

                  this.http.get<any>(`http://localhost:8888/auth-service/user/${seekerId}`, { headers }).subscribe({
                    next: (user) => {
                      app.jobSeeker = user;
                    },
                    error: (err) => {
                      // If we cannot fetch profile (unauthorized or not found), leave minimal info
                      app.jobSeeker = app.jobSeeker || { id: seekerId, fullName: 'Unknown', title: '' };
                    }
                  });
                });
              },
              error: (err) => {
                console.warn('Failed to load applications for job', job.id, err);
              }
            });
          });
        } else {
          console.warn('Unexpected response from getMyJobs', res);
        }
      },
      error: (err) => {
        console.error('Failed to load jobs', err);
        this.toastService.error('Failed to load your jobs.');
      }
    });
  }

  get filteredJobs(): JobOffer[] {
    return this.jobs.filter(job =>
      job.title.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
      job.company.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
      job.skills.some(skill => skill.toLowerCase().includes(this.searchQuery.toLowerCase()))
    );
  }

  get notPublishedJobs(): JobOffer[] {
    return this.jobs.filter(job => !job.published);
  }

  toggleComposer(): void {
    this.isComposerExpanded = true;
  }

  cancelComposer(): void {
    this.isComposerExpanded = false;
    this.resetForm();
  }

  addSkill(): void {
    if (this.newSkill.trim() && !this.newJob.skills!.includes(this.newSkill.trim())) {
      this.newJob.skills!.push(this.newSkill.trim());
      this.newSkill = '';
    }
  }

  removeSkill(skillToRemove: string): void {
    this.newJob.skills = this.newJob.skills!.filter(skill => skill !== skillToRemove);
  }

  isFormValid(): boolean {
    return !!(
      this.newJob.title?.trim() &&
      this.newJob.company?.trim() &&
      this.newJob.location?.trim() &&
      this.newJob.type?.trim() &&
      this.newJob.experience?.trim() &&
      this.newJob.salary?.trim() &&
      this.newJob.description?.trim()
    );
  }

  addRequirement(): void {
    if (this.newRequirement.trim() && !this.newJob.requirements!.includes(this.newRequirement.trim())) {
      this.newJob.requirements!.push(this.newRequirement.trim());
      this.newRequirement = '';
    }
  }

  removeRequirement(requirementToRemove: string): void {
    this.newJob.requirements = this.newJob.requirements!.filter(req => req !== requirementToRemove);
  }

  publishJob(): void {
    if (this.isFormValid()) {
      // start publishing flow
      this.isPublishing = true;

      const doCreate = (companyLogoUrl?: string) => {
        const payload = {
          title: this.newJob.title,
          jobPosition: this.newJob.title,
          company: this.newJob.company,
          companyLogo: companyLogoUrl || this.newJob.companyLogo || this.logoUrl,
          location: this.newJob.location,
          type: this.newJob.type,
          experience: this.newJob.experience,
          salary: this.newJob.salary,
          description: this.newJob.description,
          skills: this.newJob.skills,
          requirements: this.newJob.requirements || [],
          status: this.newJob.status || 'open',
          published: true
        };

        this.jobService.createJob(payload).subscribe({
          next: (created: any) => {
            // After create, refresh the authoritative list from the server
            this.loadMyJobs();
            this.isComposerExpanded = false;
            this.resetForm();
            this.logoFile = undefined;
            this.logoPreview = undefined;
            this.logoUrl = undefined;
            this.toastService.success('Job published successfully.');
          },
          error: (err) => {
            console.error('Create job failed', err);
            this.toastService.error('Failed to publish job.');
            this.isPublishing = false;
          },
          complete: () => {
            this.isPublishing = false;
          }
        });
      };

      // If there's a selected file, upload it first
      if (this.logoFile) {
        const form = new FormData();
        form.append('file', this.logoFile, this.logoFile.name);
        this.jobService.uploadLogo(form).subscribe({
          next: (res: any) => {
            const url = res && res.url ? res.url : undefined;
            this.logoUrl = url;
            doCreate(url);
          },
          error: (err) => {
            console.error('Logo upload failed', err);
            this.toastService.error('Failed to upload company logo.');
            this.isPublishing = false;
          }
        });
      } else {
        doCreate();
      }
    }
  }

  onLogoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.logoFile = input.files[0];
      const reader = new FileReader();
      reader.onload = (e: any) => this.logoPreview = e.target.result;
      reader.readAsDataURL(this.logoFile);
    }
  }

  handlePublishJob(jobId: string): void {
    const job = this.jobs.find(j => j.id === jobId);
    if (job) {
      // If job seems to be persisted (has id), call backend to set published=true
      this.jobService.updateJob(job.id, { published: true }).subscribe({
        next: (updated: any) => {
          job.published = true;
          this.toastService.success('Job published successfully.');
        },
        error: (err) => {
          console.error('Publish job failed', err);
          this.toastService.error('Failed to publish job.');
        }
      });
    }
  }

  handleEditJob(updatedJob: JobOffer): void {
    const index = this.jobs.findIndex(j => j.id === updatedJob.id);
    if (index === -1) {
      // If job isn't in local list, just refresh
      this.loadMyJobs();
      return;
    }

    // Prepare payload for backend - ensure required fields like jobPosition are included
    const payload: any = {
      title: updatedJob.title,
      jobPosition: updatedJob.title || (updatedJob as any).jobPosition,
      company: updatedJob.company,
      location: updatedJob.location,
      type: updatedJob.type,
      experience: updatedJob.experience,
      salary: updatedJob.salary,
      description: updatedJob.description,
      skills: updatedJob.skills || [],
      requirements: updatedJob.requirements || [],
      status: (updatedJob as any).status || 'open',
      published: !!updatedJob.published
    };

    this.isUpdating = true;
    this.jobService.updateJob(updatedJob.id!, payload).subscribe({
      next: (res: any) => {
        // update local copy with server response if present, otherwise use updatedJob
        const serverJob = res && res.id ? res : updatedJob;
        this.jobs[index] = {
          ...this.jobs[index],
          title: serverJob.title || updatedJob.title,
          company: serverJob.company || updatedJob.company,
          location: serverJob.location || updatedJob.location,
          type: serverJob.type || updatedJob.type,
          experience: serverJob.experience || updatedJob.experience,
          salary: serverJob.salary || updatedJob.salary,
          description: serverJob.description || updatedJob.description,
          skills: serverJob.skills || updatedJob.skills || [],
          requirements: serverJob.requirements || updatedJob.requirements || [],
          published: serverJob.published !== undefined ? serverJob.published : updatedJob.published,
          applicants: serverJob.applicants || this.jobs[index].applicants,
          applications: serverJob.applications || this.jobs[index].applications
        } as JobOffer;

        this.toastService.success('Job updated successfully.');
      },
      error: (err) => {
        console.error('Update job failed', err);
        this.toastService.error('Failed to update job.');
      },
      complete: () => {
        this.isUpdating = false;
      }
    });
  }

  // NOUVELLE MÉTHODE : Gestion des changements de statut des applications
  handleApplicationStatusChange(event: {applicationId: number, newStatus: string, interviewData?: any}) {
    console.log('Changement de statut d\'application:', event);
    
    // Trouver le job et l'application concernés
    for (const job of this.jobs) {
      if (job.applications) {
        const application = job.applications.find(a => a.id === event.applicationId);
        if (application) {
          // Sauvegarder l'ancien statut pour le log
          const oldStatus = application.status;
          
          // Mettre à jour le statut
          application.status = event.newStatus as any;
          
          // Mettre à jour les données d'entretien si fournies
          if (event.interviewData) {
            application.interviewDate = event.interviewData.interviewDate;
            application.interviewLocation = event.interviewData.location;
            application.interviewNotes = event.interviewData.additionalNotes;
          }
          
          // Mettre à jour la date de dernier changement
          application.lastStatusChange = new Date().toISOString();
          
          console.log(`Statut de l'application ${event.applicationId} mis à jour: ${oldStatus} -> ${event.newStatus}`);
          break;
        }
      }
    }
  }

  private resetForm(): void {
    this.newJob = {
      title: '',
      company: '',
      location: '',
      type: '',
      experience: '',
      salary: '',
      description: '',
      skills: [],
      status: 'open',
      requirements: [],
      published: false,
      applicants: 0,
      posted: 'Just now'
    };
    this.newSkill = '';
    this.newRequirement = '';
  }
}