import { IsString, IsOptional, IsNotEmpty } from 'class-validator';

export interface ConversationContext {
  phase: 'collect_info' | 'advice' | 'practice';
  currentStep?: string;
  userProfile?: UserProfile;
}

export interface UserProfile {
  jobTitle?: string;
  interviewType?: 'presentiel' | 'en_ligne' | 'hybride';
  experienceLevel?: string;
  skills?: string[];
  industry?: string;
  companyType?: string;
  specificConcerns?: string[];
}

export class ChatRequestDto {
  @IsString()
  @IsNotEmpty()
  message: string;

  @IsOptional()
  conversationContext?: ConversationContext;

  @IsOptional()
  userProfile?: UserProfile;
}
