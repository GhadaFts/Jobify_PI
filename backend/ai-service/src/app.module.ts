import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { CvCorrectionModule } from './cv-correction/cv-correction.module';
import { GeminiModule } from './gemini/gemini.module';
import { InterviewBotModule } from './interview-bot/interview-bot.module'; // AJOUT

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
    }),
    GeminiModule,
    CvCorrectionModule,
    InterviewBotModule, // AJOUT
  ],
})
export class AppModule {}
