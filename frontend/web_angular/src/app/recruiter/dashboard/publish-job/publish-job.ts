import { Component } from '@angular/core';
import { JobOffer } from '../../../types';
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

  jobs: JobOffer[] = [
  {
    id: '1',
    title: 'Senior Frontend Developer',
    company: 'Tech Corp',
    companyLogo: 'https://images.unsplash.com/photo-1559136555-9303baea8ebd?w=800&h=200&fit=crop',
    location: 'New York',
    type: 'Full-time',
    experience: '5+ years',
    salary: '$120,000 - $150,000',
    description: 'We are looking for an experienced Frontend Developer to join our team...',
    skills: ['React', 'TypeScript', 'Next.js', 'CSS'],
    requirements: [
      'Bachelor\'s degree in Computer Science or related field',
      '5+ years of professional frontend development experience',
      'Strong proficiency in React and TypeScript',
      'Experience with modern build tools and CI/CD'
    ],
    posted: '2 days ago',
    applicants: 45,
    status: 'actively hiring',
    published: true
  },
  {
    id: '2',
    title: 'Product Manager',
    company: 'StartUp Inc',
    companyLogo: 'https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=40&h=40&fit=crop',
    location: 'San Francisco',
    type: 'Full-time',
    experience: '3+ years',
    salary: '$100,000 - $130,000',
    description: 'Lead product development and strategy for our growing platform...',
    skills: ['Product Strategy', 'Agile', 'User Research', 'Analytics'],
    requirements: [
      '3+ years of product management experience',
      'Proven track record of launching successful products',
      'Strong analytical and problem-solving skills',
      'Excellent communication and leadership abilities'
    ],
    posted: '1 day ago',
    applicants: 23,
    status: 'new',
    published: false
  }
];

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
      const job: JobOffer = {
        id: Date.now().toString(),
        title: this.newJob.title!,
        company: this.newJob.company!,
        location: this.newJob.location!,
        type: this.newJob.type!,
        experience: this.newJob.experience!,
        salary: this.newJob.salary!,
        description: this.newJob.description!,
        skills: this.newJob.skills!,
        requirements: this.newJob.requirements || [],
        posted: 'Just now',
        applicants: 0,
        status: this.newJob.status!,
        published: true
      };

      this.jobs.unshift(job);
      this.isComposerExpanded = false;
      this.resetForm();
    }
  }

  handlePublishJob(jobId: string): void {
    const job = this.jobs.find(j => j.id === jobId);
    if (job) {
      job.published = true;
    }
  }

  handleEditJob(updatedJob: JobOffer): void {
  const index = this.jobs.findIndex(j => j.id === updatedJob.id);
  if (index !== -1) {
    this.jobs[index] = updatedJob;
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
  }
}