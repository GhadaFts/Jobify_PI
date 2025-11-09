export interface User {
  id: number;
  email: string;
  password: string;
  fullName: string;
  role: string;
  photo_profil: string;
  twitter_link: string;
  web_link: string;
  github_link: string;
  facebook_link: string;
  description: string;
  phone_number: string;
  nationality: string;
}

export interface JobSeeker extends User {
  skills: string[];
  experience: {
    position: string;
    company: string;
    startDate: string;
    endDate: string;
    description: string;
  }[];
  education: {
    degree: string;
    field: string;
    school: string;
    graduationDate: string;
  }[];
  title: string;
  date_of_birth: string;
  gender: string;
}

export interface Application {
  id: number;
  applicationDate: string;
  status: 'new' | 'under_review' | 'interview_scheduled' | 'interview_annulled' | 'offer_pending' | 'accepted' | 'rejected';
  cv_link: string;
  motivation_lettre: string;
  jobSeeker: JobSeeker;
  jobOfferId: string;
  interviewDate?: string;
  interviewLocation?: string;
  interviewNotes?: string;
  lastStatusChange?: string;
  
  // NOUVELLES PROPRIÉTÉS
  aiScore?: number;        // Score AI entre 0 et 100
  isFavorite?: boolean;    // Marqueur de favori
}

export interface InterviewNotification {
  interviewDate: string;
  interviewTime: string;
  location: string;
  additionalNotes: string;
  duration: string;
  interviewType: string;
}

export interface JobOffer {
  id: string;
  title: string;
  company: string;
  companyLogo?: string;
  location: string;
  type: string;
  experience: string;
  salary: string;
  description: string;
  skills: string[];
  requirements: string[];
  posted: string;
  applicants: number;
  status: string;
  published: boolean;
  applications?: Application[];
}
export interface Interview {
  id: string;
  candidateName: string;
  candidateTitle: string;
  candidatePhoto: string;
  jobOfferTitle: string;
  jobOfferId: string;
  applicationId: number;
  interviewDate: string; // ISO string
  interviewTime: string;
  interviewDuration: number; // in minutes
  interviewType: string;
  interviewLocation: string;
  interviewStatus: 'scheduled' | 'in-progress' | 'completed' | 'cancelled';
  interviewer: string;
  notes?: string;
  meetingLink?: string;
  rating?: number;
  feedback?: string;
}