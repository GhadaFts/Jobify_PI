export class JobSeekerAIRequest {
  id!: number;
  email!: string;
  fullName!: string;
  description!: string;
  nationality!: string;
  skills!: string[];
  experience!: string;
  education!: string;
  title!: string;
  date_of_birth!: string;
  gender!: string;
}

export class ApplicationAIRequest {
  id!: number;
  applicationDate!: string;
  status!: string;
  motivation_lettre!: string;
  jobSeeker!: JobSeekerAIRequest;
  jobOfferId!: string;
}

export class JobOfferAIRequest {
  id!: string;
  title!: string;
  company!: string;
  location!: string;
  type!: string;
  experience!: string;
  salary!: string;
  description!: string;
  skills!: string[];
  requirements!: string[];
  applications!: ApplicationAIRequest[];
}

export class ApplicationScore {
  id!: number;
  score!: number;
}

export class AIRankingResponse {
  id!: string;
  applications!: ApplicationScore[];
}
