import { Component } from '@angular/core';
import { JobOffer, Application } from '../../../types';
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
      applicants: 3,
      status: 'actively hiring',
      published: true,
      applications: [
        {
          id: 1,
          applicationDate: '2024-01-20',
          status: 'new',
          cv_link: '/cv/cv1.pdf',
          motivation_lettre: 'Je suis très intéressé par ce poste de développeur frontend...',
          jobOfferId: '1',
          jobSeeker: {
            id: 1,
            email: "mohamed.ali@email.com",
            password: "password",
            fullName: "Mohamed Ali",
            role: "jobseeker",
            photo_profil: "assets/condidat_profile.png",
            twitter_link: "https://twitter.com/mohamed",
            web_link: "https://mohamed.dev",
            github_link: "https://github.com/mohamed",
            facebook_link: "https://facebook.com/mohamed",
            description: "Développeur passionné avec 5 ans d'expérience en React et TypeScript...",
            phone_number: "+212 612-345678",
            nationality: "Marocaine",
            skills: ["React", "TypeScript", "Next.js", "CSS", "Node.js"],
            experience: [
              {
                position: "Tech Lead",
                company: "XYZ Company",
                startDate: "2020-01-01",
                endDate: "2024-01-01",
                description: "Lead d'une équipe de 5 développeurs sur des projets React/Node.js"
              },
              {
                position: "Développeur Frontend",
                company: "ABC Corp",
                startDate: "2018-01-01",
                endDate: "2020-01-01",
                description: "Développement d'applications web avec React et Redux"
              }
            ],
            education: [
              {
                degree: "Master",
                field: "Informatique",
                school: "Université Hassan II",
                graduationDate: "2018-06-01"
              },
              {
                degree: "Licence",
                field: "Génie Logiciel",
                school: "ENSA Marrakech",
                graduationDate: "2016-06-01"
              }
            ],
            title: "Développeur Full Stack Senior",
            date_of_birth: "1990-05-15",
            gender: "Male"
          }
        },
        {
          id: 2,
          applicationDate: '2024-01-18',
          status: 'new',
          cv_link: '/cv/cv2.pdf',
          motivation_lettre: 'Votre offre correspond parfaitement à mon profil de développeuse frontend...',
          jobOfferId: '1',
          jobSeeker: {
            id: 2,
            email: "fatima.zahra@email.com",
            password: "password",
            fullName: "Fatima Zahra",
            role: "jobseeker",
            photo_profil: "assets/condidat_profile.png",
            twitter_link: "https://twitter.com/fatima",
            web_link: "https://fatima.dev",
            github_link: "https://github.com/fatima",
            facebook_link: "https://facebook.com/fatima",
            description: "Développeuse front-end créative avec 4 ans d'expérience...",
            phone_number: "+212 612-987654",
            nationality: "Marocaine",
            skills: ["React", "Vue.js", "JavaScript", "CSS", "SASS"],
            experience: [
              {
                position: "Frontend Developer",
                company: "Digital Agency",
                startDate: "2021-01-01",
                endDate: "2024-01-01",
                description: "Développement d'interfaces utilisateur responsive avec Vue.js et React"
              }
            ],
            education: [
              {
                degree: "Licence",
                field: "Génie Logiciel",
                school: "ENSET",
                graduationDate: "2020-06-01"
              }
            ],
            title: "Développeuse Frontend",
            date_of_birth: "1995-08-22",
            gender: "Female"
          }
        }
      ]
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
      applicants: 1,
      status: 'new',
      published: false,
      applications: [
        {
          id: 3,
          applicationDate: '2024-01-22',
          status: 'new',
          cv_link: '/cv/cv3.pdf',
          motivation_lettre: 'Je suis impatient de contribuer à votre équipe produit...',
          jobOfferId: '2',
          jobSeeker: {
            id: 3,
            email: "karim.benjelloun@email.com",
            password: "password",
            fullName: "Karim Benjelloun",
            role: "jobseeker",
            photo_profil: "assets/condidat_profile.png",
            twitter_link: "https://twitter.com/karim",
            web_link: "https://karim.dev",
            github_link: "https://github.com/karim",
            facebook_link: "https://facebook.com/karim",
            description: "Product Manager avec expertise en stratégie produit et développement agile...",
            phone_number: "+212 600-112233",
            nationality: "Marocaine",
            skills: ["Product Strategy", "Agile", "User Research", "Analytics", "Roadmapping"],
            experience: [
              {
                position: "Product Manager",
                company: "Tech Startup",
                startDate: "2019-01-01",
                endDate: "2024-01-01",
                description: "Gestion du cycle de vie produit et collaboration avec les équipes techniques"
              }
            ],
            education: [
              {
                degree: "Diplôme d'Ingénieur",
                field: "Informatique",
                school: "EMI",
                graduationDate: "2018-06-01"
              }
            ],
            title: "Product Manager",
            date_of_birth: "1992-11-30",
            gender: "Male"
          }
        }
      ]
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
        published: true,
        applications: []
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