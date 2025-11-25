// find-job.ts - VERSION COMPLÈTE AVEC BOUTONS
import { Component, OnInit } from '@angular/core';
import { JobOffer } from '../../../types';
import { GeocodingService } from '../../../services/geocoding.service';

interface ApplicationData {
  generatedCV?: string;
  uploadedFile?: File;
  coverLetter?: string;
  applicationDate: Date;
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
export class FindJob implements OnInit {
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
  jobs: JobOffer[] = [
    // Your existing jobs...
    {
      id: '1',
      title: 'Full Stack Developer',
      company: 'TekUp',
      companyLogo: 'https://images.unsplash.com/photo-1559136555-9303baea8ebd?w=800&h=200&fit=crop',
      location: 'Tunis Center',
      type: 'Full-time',
      experience: '3+ years',
      salary: '3,500 TND',
      description: 'Develop modern web applications with Angular and Node.js',
      skills: ['JavaScript', 'Angular', 'Node.js', 'MongoDB'],
      requirements: ['Computer Science degree', 'Full stack development experience'],
      posted: '2 days ago',
      applicants: 15,
      status: 'open',
      published: true,
      applications: [],
      coordinates: { lat: 36.8065, lng: 10.1815 }
    },
    // ... (include all your existing jobs)
    
    // NEW JOBS IN TEBOURBA/MANNOUBA AREA
    {
      id: '9',
      title: 'Agricultural Engineer',
      company: 'Tebourba Farming Coop',
      companyLogo: 'https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=40&h=40&fit=crop',
      location: 'Tebourba',
      type: 'Full-time',
      experience: '2+ years',
      salary: '2,800 TND',
      description: 'Manage agricultural operations and crop production',
      skills: ['Agriculture', 'Crop Management', 'Irrigation', 'French'],
      requirements: ['Agriculture engineering degree', 'Field experience'],
      posted: '2 days ago',
      applicants: 4,
      status: 'open',
      published: true,
      applications: [],
      coordinates: { lat: 36.8333, lng: 9.8500 }
    },
    {
      id: '10',
      title: 'School Teacher',
      company: 'Tebourba Secondary School',
      companyLogo: 'https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=40&h=40&fit=crop',
      location: 'Tebourba Center',
      type: 'Full-time',
      experience: '3+ years',
      salary: '2,500 TND',
      description: 'Teaching mathematics and sciences to secondary students',
      skills: ['Teaching', 'Mathematics', 'Sciences', 'Pedagogy'],
      requirements: ['Teaching degree', '3+ years experience'],
      posted: '1 week ago',
      applicants: 8,
      status: 'urgent hiring',
      published: true,
      applications: [],
      coordinates: { lat: 36.8380, lng: 9.8450 }
    },
    // ... (include all your Tebourba/Mannouba jobs)
  ];

  appliedJobs: Map<string, ApplicationData> = new Map();
  bookmarkedJobs: Set<string> = new Set();

  constructor(private geocodingService: GeocodingService) {}

  ngOnInit() {}

  // TOGGLE SECTIONS
  toggleLocationSection() {
    this.showLocationSection = !this.showLocationSection;
    // Close filter section if opening location section
    if (this.showLocationSection) {
      this.showFilterSection = false;
    }
  }

  toggleFilterSection() {
    this.showFilterSection = !this.showFilterSection;
    // Close location section if opening filter section
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
      Math.sin(dLat/2) * Math.sin(dLat/2) +
      Math.cos(this.deg2rad(lat1)) * Math.cos(this.deg2rad(lat2)) * 
      Math.sin(dLng/2) * Math.sin(dLng/2); 
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
    return R * c;
  }

  private deg2rad(deg: number): number {
    return deg * (Math.PI/180);
  }

  // MAIN GETTER WITH SEPARATE FILTERS
  get allJobs(): JobOffer[] {
    let filteredJobs = this.jobs;

    // MAIN SEARCH
    if (this.searchQuery.trim() !== '') {
      const query = this.searchQuery.toLowerCase();
      filteredJobs = filteredJobs.filter(job =>
        job.title.toLowerCase().includes(query) ||
        job.company.toLowerCase().includes(query) ||
        job.skills.some(skill => skill.toLowerCase().includes(query)) ||
        job.location.toLowerCase().includes(query)
      );
    }

    // JOB TYPE FILTER
    if (this.jobTypeFilter) {
      filteredJobs = filteredJobs.filter(job => job.type === this.jobTypeFilter);
    }

    // JOB STATUS FILTER
    if (this.jobStatusFilter) {
      filteredJobs = filteredJobs.filter(job => job.status === this.jobStatusFilter);
    }

    // REAL-TIME GEOLOCATION FILTER
    if (this.useCurrentLocation && this.currentLocation) {
      filteredJobs = this.filterJobsByGeolocation(filteredJobs);
    }

    return filteredJobs;
  }

  // REAL-TIME GEOLOCATION FILTER
  private filterJobsByGeolocation(jobs: JobOffer[]): JobOffer[] {
    console.log(' Filtering by real-time geolocation:', this.currentLocation);
    
    return jobs.filter(job => {
      if (!job.coordinates) return false;
      
      const distance = this.calculateDistance(
        this.currentLocation!.lat, this.currentLocation!.lng,
        job.coordinates.lat, job.coordinates.lng
      );
      
      console.log(`📏 ${job.location}: ${distance.toFixed(1)}km (radius: ${this.radius}km)`);
      return distance <= this.radius;
    });
  }

  // REAL-TIME GEOLOCATION TOGGLE
  async toggleCurrentLocation() {
    if (this.useCurrentLocation) {
      // Deactivate
      this.useCurrentLocation = false;
      this.currentLocation = null;
      this.detectedCity = '';
      this.detectedArea = '';
      this.locationInfo = 'Real-time location deactivated';
    } else {
      // Activate
      this.isLoadingLocation = true;
      this.locationInfo = 'Detecting your precise location...';
      
      try {
        const position = await this.geocodingService.getCurrentPosition();
        this.currentLocation = position;
        this.useCurrentLocation = true;
        
        this.detectedCoordinates = `${position.lat.toFixed(6)}, ${position.lng.toFixed(6)}`;
        this.locationAccuracy = `Accuracy: ${position.accuracy ? position.accuracy.toFixed(0) + 'm' : 'Unknown'}`;
        
        console.log(' Raw coordinates:', this.detectedCoordinates);
        
        // IMPROVED REVERSE GEOCODING WITH BETTER LOGIC
        this.geocodingService.getCityFromCoords(position.lat, position.lng).subscribe({
          next: (data: any) => {
            console.log('🔄 Full geocoding response:', data);
            
            if (data && data.results && data.results.length > 0) {
              const bestResult = data.results[0];
              const components = bestResult.components;
              
              console.log(' All location components:', components);
              
              // IMPROVED LOCATION DETECTION STRATEGY
              let detectedLocation = this.extractBestLocationName(components);
              
              if (detectedLocation) {
                this.detectedCity = detectedLocation;
                this.detectedArea = this.extractAreaDetails(components);
                this.locationInfo = ` Your location: ${detectedLocation}`;
                
                console.log('✅ Detected location:', detectedLocation);
                console.log(' Area details:', this.detectedArea);
              } else {
                this.detectedCity = 'Your current area';
                this.locationInfo = ' Real-time location active';
                console.warn('⚠️ No specific location name found');
              }
              
            } else {
              this.detectedCity = 'Your current position';
              this.locationInfo = ' Real-time location active';
              console.error('No results from geocoding API');
            }
            this.isLoadingLocation = false;
          },
          error: (error) => {
            console.error(' Reverse geocoding error:', error);
            this.detectedCity = 'Your current position';
            this.locationInfo = ' Real-time location active (API error)';
            this.isLoadingLocation = false;
          }
        });
      } catch (error) {
        console.error('Geolocation error:', error);
        
        let errorMessage = 'Unknown error';
        if (error instanceof Error) {
          errorMessage = error.message;
        } else if (typeof error === 'string') {
          errorMessage = error;
        } else {
          errorMessage = String(error);
        }
        
        this.locationInfo = ` Error: ${errorMessage}`;
        this.useCurrentLocation = false;
        this.isLoadingLocation = false;
        
        if (errorMessage.includes('permission')) {
          alert(' Please allow location access in your browser settings to use this feature.');
        } else if (errorMessage.includes('unavailable')) {
          alert(' Location unavailable. Please check your GPS signal and try again.');
        } else if (errorMessage.includes('timeout')) {
          alert(' Location detection timeout. Please try again.');
        }
      }
    }
  }

  // IMPROVED LOCATION EXTRACTION
  private extractBestLocationName(components: any): string {
    const locationPriority = [
      components.city,
      components.town,
      components.village,
      components.municipality,
      components.county,
      components.suburb,
      components.neighbourhood,
      components.state_district,
      components.state
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

  // RESET ALL FILTERS
  resetFilters() {
    this.searchQuery = '';
    this.useCurrentLocation = false;
    this.currentLocation = null;
    this.radius = 50;
    this.jobTypeFilter = '';
    this.jobStatusFilter = '';
    this.detectedCity = '';
    this.detectedArea = '';
    this.detectedCoordinates = '';
    this.locationAccuracy = '';
    this.locationInfo = 'Enter a city name or use your current location';
    this.showLocationSection = false;
    this.showFilterSection = false;
  }

  // EXISTING METHODS
  get appliedJobsList(): JobOffer[] {
    return this.jobs.filter(job => this.appliedJobs.has(job.id));
  }

  get bookmarkedJobsList(): JobOffer[] {
    return this.jobs.filter(job => this.bookmarkedJobs.has(job.id));
  }

  handleApplyJob(applicationData: {
    jobId: string;
    generatedCV?: string;
    uploadedFile?: File;
    coverLetter?: string;
  }) {
    this.appliedJobs.set(applicationData.jobId, {
      ...applicationData,
      applicationDate: new Date()
    });
  }

  handleWithdrawJob(jobId: string) {
    this.appliedJobs.delete(jobId);
  }

  handleBookmarkJob(jobId: string) {
    if (this.bookmarkedJobs.has(jobId)) {
      this.bookmarkedJobs.delete(jobId);
    } else {
      this.bookmarkedJobs.add(jobId);
    }
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