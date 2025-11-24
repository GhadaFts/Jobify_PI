import { Module } from '@nestjs/common';
import { CvGenerationService } from './cv-generation.service';
import { CvGenerationController } from './cv-generation.controller';
import { GeminiModule } from '../gemini/gemini.module';

@Module({
  imports: [GeminiModule],
  controllers: [CvGenerationController],
  providers: [CvGenerationService],
  exports: [CvGenerationService],
})
export class CvGenerationModule {}
