import { Controller, Post, Body, HttpCode, HttpStatus } from '@nestjs/common';
import { CvCorrectionService } from './cv-correction.service';
import { CorrectCvDto } from './dto/correct-cv.dto';
import { CvAnalysisResponse } from './dto/cv-analysis.response';

@Controller('cv-correction')
export class CvCorrectionController {
  constructor(private readonly cvCorrectionService: CvCorrectionService) {}

  @Post('analyze')
  @HttpCode(HttpStatus.OK)
  async analyzeCv(
    @Body() correctCvDto: CorrectCvDto,
  ): Promise<CvAnalysisResponse> {
    return this.cvCorrectionService.analyzeCv(correctCvDto);
  }
}
