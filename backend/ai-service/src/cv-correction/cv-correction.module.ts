import { Module } from '@nestjs/common';
import { CvCorrectionService } from './cv-correction.service';
import { CvCorrectionController } from './cv-correction.controller';
import { GeminiModule } from '../gemini/gemini.module';

@Module({
  imports: [GeminiModule],
  controllers: [CvCorrectionController],
  providers: [CvCorrectionService],
  exports: [CvCorrectionService],
})
export class CvCorrectionModule {}
