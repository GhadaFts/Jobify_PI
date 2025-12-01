import { Component, OnInit } from '@angular/core';
import { JobOfferDTO, JobService } from '../../../services/job.service';
import { ApplicationService } from '../../../services/application.service';

interface MostAppliedJob {
  id: string;
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
  private jobOffers: JobOfferDTO[] = []
  mostAppliedJobs: MostAppliedJob[] = [];
  jobStatusStats: JobStatusStat[] = []

  constructor(private jobOfferService: JobService, private appService: ApplicationService){}

  ngOnInit(): void {
    this.jobOfferService.getAllJobs().subscribe(jobs => this.jobOffers = jobs)
    this.jobOffers.forEach(job => {
      var applicants = 0
      this.appService.getApplicationsByJobOfferId(job.id).subscribe(result => applicants = result.length)
      const mostAppJob: MostAppliedJob = {
        id: job.id.toString(),
        title: job.title,
        company: job.company,
        status: job.status,
        applicants: applicants
      }
      this.mostAppliedJobs.push(mostAppJob)
    })
    this.mostAppliedJobs.sort((a,b) => b.applicants - a.applicants).slice(0,5);
    this.jobStatusStats = Array.from(
      this.mostAppliedJobs.reduce((map, job) => {
        map.set(job.status, (map.get(job.status) || 0) + 1);
        return map;
      }, new Map<string, number>())
    ).map(([status, count]) => ({status,count}))
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