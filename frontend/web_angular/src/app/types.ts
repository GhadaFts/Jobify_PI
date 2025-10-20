export interface JobOffer {
  id: string;
  title: string;
  company: string;
  companyLogo?: string;
  location: string;
  type: string;
  salary: string;
  description: string;
  requirements?: string[]; 
  experience: string;
  applicants: number;
  posted: string; 
  status?: 'open' | 'new' | 'hot job' | 'limited openings' | 'actively hiring' | 'urgent hiring'; // Remplace urgent
  skills: string[];
}
export interface UserProfile {
  name: string;
  title: string;
  email: string;
  phone: string;
  nationality: string; 
  dateOfBirth: string; 
  summary: string;
  skills: string[];
  experience: { position: string; company: string; startDate: string; endDate: string; description: string }[];
  education: { degree: string; field: string; school: string; graduationDate: string }[];
  profilePhoto?: string; // Added to store base64 image string
}