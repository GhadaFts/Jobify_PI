import {
  Controller,
  Post,
  Body,
  Get,
  HttpException,
  HttpStatus,
  Logger,
} from '@nestjs/common';
import { CvGenerationService } from './cv-generation.service';
import { GenerateCVRequest } from './dto/generate-cv-request.dto';
import { GenerateCVResponse } from './dto/generate-cv-response.dto';
@Controller('cv-generation')
export class CvGenerationController {
  private readonly logger = new Logger(CvGenerationController.name);

  constructor(private readonly cvGenerationService: CvGenerationService) {}

  @Post('generate-ats-cv')
  async generateATSCV(
    @Body() request: GenerateCVRequest,
  ): Promise<GenerateCVResponse> {
    this.logger.log(
      `Generating ATS CV for ${request.jobSeeker.fullName} for ${request.jobOffer.title} at ${request.jobOffer.company}`,
    );

    try {
      // Validate required fields
      if (!request.jobSeeker?.fullName || !request.jobOffer?.title) {
        throw new HttpException(
          'Missing required fields: jobSeeker.fullName and jobOffer.title are required',
          HttpStatus.BAD_REQUEST,
        );
      }

      const result = await this.cvGenerationService.generateATSCV(request);
      this.logger.log(
        `Successfully generated ATS CV with score: ${result.atsScore}`,
      );

      return result;
    } catch (error) {
      this.logger.error('CV generation failed:', error);
      throw new HttpException(
        error.message || 'Failed to generate ATS CV',
        error.status || HttpStatus.INTERNAL_SERVER_ERROR,
      );
    }
  }

  @Get('health')
  async healthCheck(): Promise<{ status: string; service: string }> {
    return {
      status: 'OK',
      service: 'cv-generation',
    };
  }
}
