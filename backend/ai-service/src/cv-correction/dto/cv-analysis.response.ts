export interface CvSuggestion {
  id: string;
  type: 'success' | 'warning' | 'info' | 'missing';
  title: string;
  message: string;
}

export interface ImprovedSummary {
  overallAssessment: string;
  strengths: string[];
  improvements: string[];
}

export interface JobSeekerProfile {
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
  skills: string[];
  experience: Experience[];
  education: Education[];
  title: string;
  date_of_birth: string;
  gender: string;
}

export interface Experience {
  position: string;
  company: string;
  startDate: string;
  endDate: string;
  description: string;
}

export interface Education {
  degree: string;
  field: string;
  school: string;
  graduationDate: string;
}

export interface CvAnalysisResponse {
  cvScore: number;
  cvSuggestions: CvSuggestion[];
  improvedSummary: ImprovedSummary;
  profile: JobSeekerProfile;
}
