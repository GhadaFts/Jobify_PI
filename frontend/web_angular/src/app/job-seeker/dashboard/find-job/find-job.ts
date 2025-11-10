import { Component } from '@angular/core';
import { JobOffer } from '../../../types'; // Adjust the import path
interface ApplicationData {
  generatedCV?: string;
  uploadedFile?: File;
  coverLetter?: string;
  applicationDate: Date;
}


@Component({
  selector: 'app-find-job',
  standalone: false,
  templateUrl: './find-job.html',
  styleUrls: ['./find-job.scss']
})
export class FindJob {
  activeTab: string = 'all';
  searchQuery: string = '';
  jobs: JobOffer[] = [
    {
      id: '1',
      title: 'Software Engineer',
      company: 'Tech Corp',
      companyLogo: 'https://images.unsplash.com/photo-1559136555-9303baea8ebd?w=800&h=200&fit=crop',
      location: 'New York',
      type: 'Full-time',
      experience: '3+ years',
      salary: '$120,000',
      description: 'Develop web applications...',
      skills: ['JavaScript', 'React', 'Node.js'],
      requirements: ['Bachelor\'s degree in Computer Science', '3+ years of experience in software development'],
      posted: '2 days ago',
      applicants: 50,
      status: 'open',
      published: true,
      applications: []
    },
    {
      id: '2',
      title: 'Data Analyst',
      company: 'Data Insights',
      companyLogo: 'https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=40&h=40&fit=crop',
      location: 'San Francisco',
      type: 'Full-time',
      experience: '2+ years',
      salary: '$95,000',
      description: 'Analyze data to provide actionable insights...',
      skills: ['Python', 'SQL', 'Excel'],
      requirements: ['Bachelor\'s degree in Statistics or related field', '2+ years of data analysis experience'],
      posted: '5 days ago',
      applicants: 30,
      status: 'urgent hiring',
      published: true,
      applications: []
    },
    {
      id: '3',
      title: 'UI/UX Designer',
      company: 'Creative Studio',
      companyLogo: 'https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=40&h=40&fit=crop',
      location: 'Los Angeles',
      type: 'Part-time',
      experience: '1+ years',
      salary: '$80,000',
      description: 'Design user-friendly interfaces and experiences...',
      skills: ['Figma', 'Adobe XD', 'Prototyping'],
      requirements: ['Portfolio demonstrating UI/UX skills', 'Experience with design tools'],
      posted: '1 week ago',
      applicants: 20,
      status: 'hot job',
      published: true,
      applications: []
    },
    {
      id: '4',
      title: 'Product Manager',
      company: 'Innovate Inc',
      companyLogo: 'https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=40&h=40&fit=crop',
      location: 'Chicago',
      type: 'Full-time',
      experience: '5+ years',
      salary: '$140,000',
      description: 'Lead product development...',
      skills: ['Product Strategy', 'Agile', 'User Research'],
      requirements: ['5+ years of product management experience', 'Strong analytical skills'],
      posted: '1 day ago',
      applicants: 15,
      status: 'new',
      published: true,
      applications: []
    },
    {
      id: '5',
      title: 'DevOps Engineer',
      company: 'Cloud Solutions',
      companyLogo: 'https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=40&h=40&fit=crop',
      location: 'Austin',
      type: 'Full-time',
      experience: '4+ years',
      salary: '$130,000',
      description: 'Manage cloud infrastructure...',
      skills: ['AWS', 'Docker', 'Kubernetes'],
      requirements: ['Experience with cloud platforms', 'Knowledge of containerization technologies'],
      posted: '3 days ago',
      applicants: 25,
      status: 'limited openings',
      published: true,
      applications: []
    },
    {
      id: '6',
      title: 'Frontend Developer',
      company: 'Web Masters',
      companyLogo: 'https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=40&h=40&fit=crop',
      location: 'Miami',
      type: 'Full-time',
      experience: '2+ years',
      salary: '$110,000',
      description: 'Build responsive web applications...',
      skills: ['TypeScript', 'React', 'CSS'],
      requirements: ['Proficiency in modern frontend frameworks', 'Understanding of responsive design'],
      posted: '4 days ago',
      applicants: 40,
      status: 'actively hiring',
      published: true,
      applications: []
    },
  ];
   appliedJobs: Map<string, {
    generatedCV?: string;
    uploadedFile?: File;
    coverLetter?: string;
    applicationDate: Date;
  }> = new Map();
  bookmarkedJobs: Set<string> = new Set();

  get allJobs(): JobOffer[] {
    return this.jobs.filter(job =>
      job.title.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
      job.company.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
      job.skills.some(skill => skill.toLowerCase().includes(this.searchQuery.toLowerCase()))
    );
  }

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

  // Méthode pour obtenir la couleur selon le statut
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

  // Méthode pour formater le nombre d'applicants
  formatCount(count: number): string {
    return count >= 1000 ? (count / 1000).toFixed(1) + 'k' : count.toString();
  }
}