export interface JobOffer {
  id: string;
  title: string;
  company: string;
  companyLogo?: string;
  location: string;
  type: string;
  salary: string;
  description: string;
  requirements?: string[]; // Changed to optional
  experience: string;
  applicants: number;
  posted: string; // date string
  urgent?: boolean;// remplacer par status ("urgent", "normal", "closed")
  verified?: boolean;// eleminer
  remote?: boolean;// eleminer
  skills: string[];
}

export interface UserProfile {
  name: string;
  title: string;
  email: string;
  phone: string;
  location: string;
  summary: string;
  skills: string[];
  experience: { position: string; company: string; startDate: string; endDate: string; description: string }[];
  education: { degree: string; field: string; school: string; graduationDate: string }[];
}