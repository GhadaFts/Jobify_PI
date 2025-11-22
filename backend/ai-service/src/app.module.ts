import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { CvCorrectionModule } from './cv-correction/cv-correction.module';
import { GeminiModule } from './gemini/gemini.module';
import { InterviewBotModule } from './interview-bot/interview-bot.module'; // AJOUT
import { ApplicationRankingModule } from './application-ranking/application-ranking.module'; // AJOUT
@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
    }),
    GeminiModule,
    CvCorrectionModule,
    InterviewBotModule,
    ApplicationRankingModule, // AJOUT
  ],
})
export class AppModule {}
