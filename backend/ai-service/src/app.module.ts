import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { CvCorrectionModule } from './cv-correction/cv-correction.module';
import { GeminiModule } from './gemini/gemini.module';
import { InterviewBotModule } from './interview-bot/interview-bot.module';
import { ApplicationRankingModule } from './application-ranking/application-ranking.module';
import { EurekaService } from './common/services/eureka.service';
import { HealthController } from './common/controllers/health.controller';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
    }),
    GeminiModule,
    CvCorrectionModule,
    InterviewBotModule,
    ApplicationRankingModule,
  ],
  controllers: [HealthController],
  providers: [EurekaService],
})
export class AppModule {}
