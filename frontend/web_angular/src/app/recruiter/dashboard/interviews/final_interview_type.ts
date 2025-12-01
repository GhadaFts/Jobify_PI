import { ApplicationResponseDTO } from "../../../services/application.service";
import { InterviewStatus, InterviewType } from "../../../types";

export interface FinalInterview {
  id: number;
  application: ApplicationResponseDTO | null;
  jobSeeker: any;
  recruiter: any;
  scheduledDate: string; // ISO string representing LocalDateTime
  duration: number; // in minutes
  location?: string;
  interviewType: InterviewType;
  status: InterviewStatus;
  job_title: string;
  notes?: string;
  meetingLink?: string;
  createdAt?: string; // ISO string representing LocalDateTime
  updatedAt?: string; // ISO string representing LocalDateTime
}