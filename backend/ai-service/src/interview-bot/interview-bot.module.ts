import { Module } from '@nestjs/common';
import { InterviewBotService } from './interview-bot.service';
import { InterviewBotController } from './interview-bot.controller';
import { GeminiModule } from '../gemini/gemini.module';

@Module({
  imports: [GeminiModule],
  controllers: [InterviewBotController],
  providers: [InterviewBotService],
  exports: [InterviewBotService],
})
export class InterviewBotModule {}
