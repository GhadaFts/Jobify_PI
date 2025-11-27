export class JobSeekerCVData {
  id!: number;
  fullName!: string;
  email!: string;
  phone_number?: string;
  title?: string;
  description?: string;
  nationality?: string;
  skills!: string[];
  experience!: any[]; // Array of experience objects
  education!: any[]; // Array of education objects
  github_link?: string;
  web_link?: string;
  twitter_link?: string;
  facebook_link?: string;
  date_of_birth?: string;
  gender?: string;
}

export class JobOfferCVData {
  id!: string;
  title!: string;
  company!: string;
  description!: string;
  skills!: string[];
  requirements!: string[];
  experience!: string;
  type!: string;
  location!: string;
}

export class GenerateCVRequest {
  jobSeeker!: JobSeekerCVData;
  jobOffer!: JobOfferCVData;
  format: 'ats' | 'modern' | 'creative' = 'ats';
}
