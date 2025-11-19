import { Module } from '@nestjs/common';
import { ApplicationRankingService } from './application-ranking.service';
import { ApplicationRankingController } from './application-ranking.controller';
import { GeminiModule } from '../gemini/gemini.module';

@Module({
  imports: [GeminiModule],
  controllers: [ApplicationRankingController],
  providers: [ApplicationRankingService],
  exports: [ApplicationRankingService],
})
export class ApplicationRankingModule {}
