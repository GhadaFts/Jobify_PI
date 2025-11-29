import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JobOffer } from '../types';

export interface JobOfferDTO {
  id: number;
  title: string;
  jobPosition: string;
  experience: string;
  salary: string;
  description: string;
  type: string;
  createdAt: string;
  updatedAt: string;
  status: string;
  requirements: string[];
  skills: string[];
  published: boolean;
  location: string;
  company: string;
  currency: string;
  recruiterId: string;
  applicationDeadline: string;
}

export interface JobSearchFilters {
  title?: string;
  type?: string;
  experience?: string;
  location?: string;
}

@Injectable({
  providedIn: 'root'
})
export class JobService {
  private apiUrl = 'http://localhost:8888/joboffer-service/api/jobs'; // Gateway URL

  constructor(private http: HttpClient) {}

  /**
   * Search jobs with filters
   */
  searchJobs(filters: JobSearchFilters = {}): Observable<JobOfferDTO[]> {
    let params = new HttpParams();
    
    if (filters.title) {
      params = params.set('title', filters.title);
    }
    if (filters.type) {
      params = params.set('type', filters.type);
    }
    if (filters.experience) {
      params = params.set('experience', filters.experience);
    }
    if (filters.location) {
      params = params.set('location', filters.location);
    }

    return this.http.get<JobOfferDTO[]>(this.apiUrl, { params });
  }

  /**
   * Get all jobs (no filters)
   */
  getAllJobs(): Observable<JobOfferDTO[]> {
    return this.http.get<JobOfferDTO[]>(this.apiUrl);
  }

  /**
   * Get job by ID
   */
  getJobById(id: number): Observable<JobOfferDTO> {
    return this.http.get<JobOfferDTO>(`${this.apiUrl}/${id}`);
  }

  /**
   * Get jobs by recruiter (for recruiter dashboard)
   */
  getMyJobs(): Observable<JobOfferDTO[]> {
    return this.http.get<JobOfferDTO[]>(`${this.apiUrl}/my-jobs`);
  }

  /**
   * Create new job (recruiter only)
   */
  createJob(jobData: Partial<JobOfferDTO>): Observable<JobOfferDTO> {
    return this.http.post<JobOfferDTO>(this.apiUrl, jobData);
  }

  /**
   * Update job (recruiter only)
   */
  updateJob(id: number, jobData: Partial<JobOfferDTO>): Observable<JobOfferDTO> {
    return this.http.put<JobOfferDTO>(`${this.apiUrl}/${id}`, jobData);
  }

  /**
   * Delete job (recruiter only)
   */
  deleteJob(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Convert backend DTO to frontend JobOffer type
   */
  convertToJobOffer(dto: JobOfferDTO): JobOffer {
    return {
      id: dto.id.toString(),
      title: dto.title,
      company: dto.company,
      companyLogo: this.getDefaultCompanyLogo(dto.company),
      location: dto.location,
      type: dto.type,
      experience: dto.experience,
      salary: `${dto.salary} ${dto.currency || 'TND'}`,
      description: dto.description,
      skills: dto.skills || [],
      requirements: dto.requirements || [],
      posted: this.calculatePostedTime(dto.createdAt),
      applicants: 0, // This should come from Application Service
      status: dto.status || 'open',
      published: dto.published,
      applications: [],
      coordinates: this.getCoordinatesForLocation(dto.location)
    };
  }

  /**
   * Get default company logo based on company name
   */
  private getDefaultCompanyLogo(company: string): string {
    // Return a placeholder or generate from company name
    return `https://ui-avatars.com/api/?name=${encodeURIComponent(company)}&size=200&background=random`;
  }

  /**
   * Calculate "posted X days ago" from date
   */
  private calculatePostedTime(createdAt: string): string {
    const now = new Date();
    const posted = new Date(createdAt);
    const diffTime = Math.abs(now.getTime() - posted.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays === 0) {
      return 'today';
    } else if (diffDays === 1) {
      return '1 day ago';
    } else if (diffDays < 7) {
      return `${diffDays} days ago`;
    } else if (diffDays < 30) {
      const weeks = Math.floor(diffDays / 7);
      return weeks === 1 ? '1 week ago' : `${weeks} weeks ago`;
    } else {
      const months = Math.floor(diffDays / 30);
      return months === 1 ? '1 month ago' : `${months} months ago`;
    }
  }

  /**
   * Get approximate coordinates for Tunisia locations
   * In production, you'd use a geocoding service
   */
  private getCoordinatesForLocation(location: string): { lat: number; lng: number } {
    const locationMap: { [key: string]: { lat: number; lng: number } } = {
      'Tunis': { lat: 36.8065, lng: 10.1815 },
      'Tunis Center': { lat: 36.8065, lng: 10.1815 },
      'Ariana': { lat: 36.8625, lng: 10.1956 },
      'Ben Arous': { lat: 36.7470, lng: 10.2175 },
      'Manouba': { lat: 36.8103, lng: 10.0964 },
      'Sfax': { lat: 34.7406, lng: 10.7603 },
      'Sousse': { lat: 35.8256, lng: 10.6369 },
      'Bizerte': { lat: 37.2746, lng: 9.8739 },
      'Nabeul': { lat: 36.4511, lng: 10.7356 },
      'Monastir': { lat: 35.7770, lng: 10.8263 },
      'Tebourba': { lat: 36.8333, lng: 9.8500 },
      'La Marsa': { lat: 36.8780, lng: 10.3250 },
      'Carthage': { lat: 36.8531, lng: 10.3231 }
    };

    // Try to find exact match
    const exactMatch = locationMap[location];
    if (exactMatch) {
      return exactMatch;
    }

    // Try to find partial match
    const partialMatch = Object.keys(locationMap).find(key => 
      location.toLowerCase().includes(key.toLowerCase()) ||
      key.toLowerCase().includes(location.toLowerCase())
    );

    if (partialMatch) {
      return locationMap[partialMatch];
    }

    // Default to Tunis if no match found
    return { lat: 36.8065, lng: 10.1815 };
  }
}