import { Component, OnInit } from '@angular/core';
import { JobOfferDTO, JobService } from '../../../services/job.service';
import { ApplicationService } from '../../../services/application.service';
import { forkJoin } from 'rxjs';
import { switchMap, map } from 'rxjs/operators';

interface MostAppliedJob {
  id: number;
  title: string;
  company: string;
  applicants: number;
  status: string;
}

interface JobStatusStat {
  status: string;
  count: number;
}

@Component({
  selector: 'app-recruiter-sidebar',
  templateUrl: './recruiter-sidebar.html',
  styleUrls: ['./recruiter-sidebar.scss'],
  standalone: false,
})
export class RecruiterSidebar implements OnInit {
  mostAppliedJobs: MostAppliedJob[] = [];
  jobStatusStats: JobStatusStat[] = []

  constructor(private jobOfferService: JobService, private appService: ApplicationService){}

  ngOnInit(): void {
    this.jobOfferService
      .getAllJobs()
      .pipe(
        switchMap(jobs => {
          // Build parallel application count requests
          const requests = jobs.map(job =>
            this.appService.getApplicationsByJobOfferId(job.id).pipe(
              map(applications => ({
                id: job.id,
                title: job.title,
                company: job.company,
                status: job.status,
                applicants: applications.length,
              }))
            )
          );

          return forkJoin(requests);
        })
      )
      .subscribe(result => {
        // 1. Save most applied jobs
        this.mostAppliedJobs = result
          .sort((a, b) => b.applicants - a.applicants)
          .slice(0, 5);

        // 2. Build status stats
        this.jobStatusStats = Array.from(
          this.mostAppliedJobs.reduce((map, job) => {
            map.set(job.status, (map.get(job.status) || 0) + 1);
            return map;
          }, new Map<string, number>())
        ).map(([status, count]) => ({ status, count }));
      });
  }

  getStatusColor(status: string): string {
    const colors: { [key: string]: string } = {
      'open': '#10B981', // Vert
      'new': '#3B82F6', // Bleu
      'hot job': '#DC2626', // Rouge
      'limited openings': '#F59E0B', // Orange
      'actively hiring': '#8B5CF6', // Violet
      'urgent hiring': '#EF4444' // Rouge vif
    };
    return colors[status] || '#6B7280'; // Gris par défaut
  }

  getTotalJobs(): number {
    return this.jobStatusStats.reduce((total, stat) => total + stat.count, 0);
  }
}