import { Component } from '@angular/core';

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
export class RecruiterSidebar {
  mostAppliedJobs: MostAppliedJob[] = [
    { id: '1', title: 'Senior Frontend Developer', company: 'Tech Corp', applicants: 156, status: 'hot job' },
    { id: '2', title: 'Full Stack Engineer', company: 'StartUp Inc', applicants: 89, status: 'actively hiring' },
    { id: '3', title: 'DevOps Specialist', company: 'Cloud Solutions', applicants: 67, status: 'urgent hiring' },
    { id: '4', title: 'Product Manager', company: 'Innovate Labs', applicants: 45, status: 'limited openings' },
    { id: '5', title: 'UX/UI Designer', company: 'Creative Studio', applicants: 32, status: 'open' }
  ];

  jobStatusStats: JobStatusStat[] = [
    { status: 'open', count: 12 },
    { status: 'new', count: 8 },
    { status: 'hot job', count: 5 },
    { status: 'limited openings', count: 3 },
    { status: 'actively hiring', count: 7 },
    { status: 'urgent hiring', count: 2 }
  ];

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