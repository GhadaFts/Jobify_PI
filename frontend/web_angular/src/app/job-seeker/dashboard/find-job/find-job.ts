import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil, debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { JobOffer } from '../../../types';
import { GeocodingService } from '../../../services/geocoding.service';
import { JobService, JobOfferDTO } from '../../../services/job.service';
import { LocalBookmarkService } from '../../../services/bookmark.service';
import { ApplicationService, ApplicationResponseDTO } from '../../../services/application.service';
import { RecruiterService } from '../../../services/recruiter.service';
import { UserService } from '../../../services/user.service';

// Local interface for application data used in UI
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

interface Coordinates {
  lat: number;
  lng: number;
  accuracy?: number;
}

@Component({
  selector: 'app-find-job',
  standalone: false,
  templateUrl: './find-job.html',
  styleUrls: ['./find-job.scss']
})
export class FindJob implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  private searchSubject = new Subject<string>();

  activeTab: string = 'all';
  searchQuery: string = '';

  // SECTION TOGGLES
  showLocationSection: boolean = false;
  showFilterSection: boolean = false;

  // SEPARATE FILTERS
  useCurrentLocation: boolean = false;
  isLoadingLocation: boolean = false;
  radius: number = 50;

  // Additional filters
  jobTypeFilter: string = '';
  jobStatusFilter: string = '';

  // IMPROVED location detection
  detectedCity: string = '';
  detectedArea: string = '';
  detectedCoordinates: string = '';
  locationAccuracy: string = '';
  locationInfo: string = 'Enter a city name or use your current location';

  private coordinatesCache: Map<string, Coordinates> = new Map();
  private currentLocation: Coordinates | null = null;

  // JOBS DATA
  jobs: JobOffer[] = [];
  isLoadingJobs: boolean = false;
  jobsLoadError: string | null = null;

  // APPLICATIONS DATA - Using local ApplicationData interface
  appliedJobs: Map<string, ApplicationData> = new Map();
  isLoadingApplications: boolean = false;

  bookmarkedJobs: Set<string> = new Set();

  constructor(
    private geocodingService: GeocodingService,
    private jobService: JobService,
    private bookmarkService: LocalBookmarkService,
    private applicationService: ApplicationService,
    private recruiterService: RecruiterService,
    private userService: UserService
  ) { }

  ngOnInit() {
    // Load jobs from backend
    this.loadJobs();

    // Load user's applications
    this.loadMyApplications();

    // Load bookmarks
    this.bookmarkService.bookmarks$
      .pipe(takeUntil(this.destroy$))
      .subscribe(bookmarks => {
        this.bookmarkedJobs = new Set(
          Array.from(bookmarks).map(id => id.toString())
        );
      });

    // Setup search debouncing
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(() => {
      this.loadJobs();
    });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Load jobs from backend
   */
  /**
 * Load jobs from backend
 */
  loadJobs(): void {
    this.isLoadingJobs = true;
    this.jobsLoadError = null;

    const filters: any = {};

    if (this.searchQuery.trim()) {
      filters.title = this.searchQuery.trim();
    }

    if (this.jobTypeFilter) {
      filters.type = this.jobTypeFilter;
    }

    this.jobService.searchJobs(filters)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (jobs: JobOffer[]) => {
          this.jobs = jobs;

          this.isLoadingJobs = false;
          console.log(`✅ Loaded ${this.jobs.length} jobs from backend`);
          // You can also log applicant counts to verify they're working:
          jobs.forEach(job => {
            console.log(`Job ${job.title}: ${job.applicants} applicants`);
          });
        },
        error: (error) => {
          console.error('❌ Failed to load jobs:', error);
          this.jobsLoadError = 'Failed to load jobs. Please try again.';
          this.isLoadingJobs = false;
          this.jobs = [];
        }
      });
  }
  /**
   * Load user's applications and convert to ApplicationData format
   */
  loadMyApplications(): void {
    this.isLoadingApplications = true;

    this.applicationService.getMyApplications()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (applications: ApplicationResponseDTO[]) => {
          // Map applications by job ID and convert to ApplicationData format
          this.appliedJobs.clear();
          applications.forEach(app => {
            const applicationData: ApplicationData = {
              applicationDate: new Date(app.applicationDate), // Convert string to Date
              coverLetter: app.motivationLettre || undefined,
              cvLink: app.cvLink,
              applicationId: app.id,
              status: app.status,
              aiScore: app.aiScore || undefined
            };
            this.appliedJobs.set(app.jobOfferId.toString(), applicationData);
          });

          this.isLoadingApplications = false;
          console.log(`✅ Loaded ${applications.length} applications from backend`);
        },
        error: (error) => {
          console.error('❌ Failed to load applications:', error);
          this.isLoadingApplications = false;
        }
      });
  }

  /**
   * Called when search query changes
   */
  onSearchQueryChange(): void {
    this.searchSubject.next(this.searchQuery);
  }

  // TOGGLE SECTIONS
  toggleLocationSection() {
    this.showLocationSection = !this.showLocationSection;
    if (this.showLocationSection) {
      this.showFilterSection = false;
    }
  }

  toggleFilterSection() {
    this.showFilterSection = !this.showFilterSection;
    if (this.showFilterSection) {
      this.showLocationSection = false;
    }
  }

  // DISTANCE CALCULATION
  private calculateDistance(lat1: number, lng1: number, lat2: number, lng2: number): number {
    const R = 6371;
    const dLat = this.deg2rad(lat2 - lat1);
    const dLng = this.deg2rad(lng2 - lng1);
    const a =
      Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos(this.deg2rad(lat1)) * Math.cos(this.deg2rad(lat2)) *
      Math.sin(dLng / 2) * Math.sin(dLng / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
  }

  private deg2rad(deg: number): number {
    return deg * (Math.PI / 180);
  }

  // MAIN GETTER WITH FILTERS
  get allJobs(): JobOffer[] {
    let filteredJobs = this.jobs;

    if (this.jobStatusFilter) {
      filteredJobs = filteredJobs.filter(job => job.status === this.jobStatusFilter);
    }

    if (this.useCurrentLocation && this.currentLocation) {
      filteredJobs = this.filterJobsByGeolocation(filteredJobs);
    }

    return filteredJobs;
  }

  private filterJobsByGeolocation(jobs: JobOffer[]): JobOffer[] {
    console.log('🔍 Filtering by real-time geolocation:', this.currentLocation);

    return jobs.filter(job => {
      if (!job.coordinates) return false;
      console.log("📍 Job coordinates:", job.coordinates.lat, job.coordinates.lng);

      const distance = this.calculateDistance(
        this.currentLocation!.lat, this.currentLocation!.lng,
        job.coordinates.lat, job.coordinates.lng
      );

      console.log(`📍 ${job.location}: ${distance.toFixed(1)}km (radius: ${this.radius}km)`);
      return distance <= this.radius;
    });
  }

  async toggleCurrentLocation() {
    if (this.useCurrentLocation) {
      this.useCurrentLocation = false;
      this.currentLocation = null;
      this.detectedCity = '';
      this.detectedArea = '';
      this.locationInfo = 'Real-time location deactivated';
    } else {
      this.isLoadingLocation = true;
      this.locationInfo = 'Detecting your precise location...';

      try {
        const position = await this.geocodingService.getCurrentPosition();
        this.currentLocation = position;
        this.useCurrentLocation = true;

        this.detectedCoordinates = `${position.lat.toFixed(6)}, ${position.lng.toFixed(6)}`;
        this.locationAccuracy = `Accuracy: ${position.accuracy ? position.accuracy.toFixed(0) + 'm' : 'Unknown'}`;

        this.geocodingService.getCityFromCoords(position.lat, position.lng).subscribe({
          next: (data: any) => {
            if (data && data.results && data.results.length > 0) {
              const components = data.results[0].components;
              let detectedLocation = this.extractBestLocationName(components);

              if (detectedLocation) {
                this.detectedCity = detectedLocation;
                this.detectedArea = this.extractAreaDetails(components);
                this.locationInfo = `📍 Your location: ${detectedLocation}`;
              } else {
                this.detectedCity = 'Your current area';
                this.locationInfo = '📍 Real-time location active';
              }
            }
            this.isLoadingLocation = false;
          },
          error: (error) => {
            console.error('❌ Reverse geocoding error:', error);
            this.detectedCity = 'Your current position';
            this.locationInfo = '📍 Real-time location active (API error)';
            this.isLoadingLocation = false;
          }
        });
      } catch (error) {
        console.error('Geolocation error:', error);
        this.locationInfo = `❌ Error: ${error}`;
        this.useCurrentLocation = false;
        this.isLoadingLocation = false;
      }
    }
  }

  private extractBestLocationName(components: any): string {
    const locationPriority = [
      components.city,
      components.town,
      components.village,
      components.municipality,
      components.county,
      components.suburb
    ];
    return locationPriority.find(name => name && name.trim() !== '') || '';
  }

  private extractAreaDetails(components: any): string {
    const details = [];
    if (components.road) details.push(components.road);
    if (components.suburb && !components.city) details.push(components.suburb);
    if (components.postcode) details.push(components.postcode);
    return details.length > 0 ? details.join(', ') : 'Area details not available';
  }

  resetFilters() {
    this.searchQuery = '';
    this.useCurrentLocation = false;
    this.currentLocation = null;
    this.radius = 50;
    this.jobTypeFilter = '';
    this.jobStatusFilter = '';
    this.detectedCity = '';
    this.detectedArea = '';
    this.showLocationSection = false;
    this.showFilterSection = false;
    this.loadJobs();
  }

  onTypeFilterChange() {
    this.loadJobs();
  }

  onStatusFilterChange() {
    // Status filter is client-side only
  }

  get appliedJobsList(): JobOffer[] {
    return this.jobs.filter(job => this.appliedJobs.has(job.id));
  }

  get bookmarkedJobsList(): JobOffer[] {
    return this.jobs.filter(job => this.bookmarkedJobs.has(job.id));
  }

  /**
   * Handle job application submission
   */
  handleApplyJob(result: any) {
    if (result && result.success) {
      console.log('✅ Application submitted successfully:', result);

      // Immediately add to appliedJobs map for instant UI update
      const applicationData: ApplicationData = {
        applicationDate: new Date(),
        coverLetter: result.coverLetter || undefined,
        cvLink: result.cvLink,
        applicationId: result.applicationId,
        aiScore: result.atsScore || undefined
      };
      this.appliedJobs.set(result.jobId, applicationData);

      // Also reload from backend to sync
      this.loadMyApplications();
    }
  }

  /**
   * Handle application withdrawal
   */
  handleWithdrawJob(jobId: string) {
    const application = this.appliedJobs.get(jobId);
    if (!application || !application.applicationId) {
      console.error('No application found for job:', jobId);
      return;
    }

    if (confirm('Are you sure you want to withdraw this application?')) {
      this.applicationService.deleteApplication(application.applicationId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            console.log('✅ Application withdrawn successfully');
            this.appliedJobs.delete(jobId);

            // Reload applications
            this.loadMyApplications();
          },
          error: (error) => {
            console.error('❌ Failed to withdraw application:', error);
            alert('Failed to withdraw application. Please try again.');
          }
        });
    }
  }

  handleBookmarkJob(jobId: string) {
    const jobIdNum = parseInt(jobId);
    this.bookmarkService.toggleBookmark(jobIdNum);
    console.log('🔖 Bookmark toggled for job:', jobId);
  }

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
}