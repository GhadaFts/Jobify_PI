import { Component } from '@angular/core';
import { JobOffer } from '../../../types'; // Adjust the import path

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
      posted: '2 days ago',
      applicants: 50,
      remote: true,
      verified: true,
      urgent: false,
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
      posted: '5 days ago',
      applicants: 30,
      remote: false,
      verified: false,
      urgent: true,
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
      posted: '1 week ago',
      applicants: 20,
      remote: true,
      verified: true,
      urgent: false,
    },
  ];
  appliedJobs: Set<string> = new Set();
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

  handleApplyJob(jobId: string) {
    this.appliedJobs.add(jobId); // Add the job ID to the appliedJobs Set
  }

  handleBookmarkJob(jobId: string) {
    if (this.bookmarkedJobs.has(jobId)) {
      this.bookmarkedJobs.delete(jobId);
    } else {
      this.bookmarkedJobs.add(jobId);
    }
  }
}