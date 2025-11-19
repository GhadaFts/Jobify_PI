import { UserProfile } from './chat-request.dto';

export interface ChatResponseDto {
  response: string;
  conversationPhase: 'collect_info' | 'advice' | 'practice';
  nextStep?: string;
  userProfileUpdates?: Partial<UserProfile>;
  suggestions?: string[];
  questions?: string[];
}
