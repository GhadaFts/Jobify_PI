import {
  Controller,
  Post,
  Body,
  HttpException,
  HttpStatus,
  Logger,
  Get,
} from '@nestjs/common';
import { ApplicationRankingService } from './application-ranking.service';
import {
  JobOfferAIRequest,
  AIRankingResponse,
} from './dto/job-offer-ai-request.dto';

@Controller('application-ranking')
export class ApplicationRankingController {
  private readonly logger = new Logger(ApplicationRankingController.name);

  constructor(
    private readonly applicationRankingService: ApplicationRankingService,
  ) {}

  @Post('rank')
  async rankApplications(
    @Body() jobOfferRequest: JobOfferAIRequest,
  ): Promise<AIRankingResponse> {
    this.logger.log(
      `Received ranking request for job offer: ${jobOfferRequest.id}`,
    );
    this.logger.debug(
      `Job title: ${jobOfferRequest.title}, Applications: ${jobOfferRequest.applications?.length || 0}`,
    );

    // Validation des données d'entrée
    if (!jobOfferRequest.id) {
      this.logger.warn('Missing job offer ID in request');
      throw new HttpException(
        {
          status: HttpStatus.BAD_REQUEST,
          error: 'Job offer ID is required',
        },
        HttpStatus.BAD_REQUEST,
      );
    }

    if (
      !jobOfferRequest.applications ||
      jobOfferRequest.applications.length === 0
    ) {
      this.logger.warn(
        `No applications provided for job offer: ${jobOfferRequest.id}`,
      );
      throw new HttpException(
        {
          status: HttpStatus.BAD_REQUEST,
          error: 'At least one application is required for ranking',
        },
        HttpStatus.BAD_REQUEST,
      );
    }

    if (jobOfferRequest.applications.length > 50) {
      this.logger.warn(
        `Too many applications (${jobOfferRequest.applications.length}) for job offer: ${jobOfferRequest.id}`,
      );
      throw new HttpException(
        {
          status: HttpStatus.BAD_REQUEST,
          error:
            'Too many applications. Maximum 50 applications allowed per ranking request',
        },
        HttpStatus.BAD_REQUEST,
      );
    }

    try {
      const startTime = Date.now();
      const rankingResult =
        await this.applicationRankingService.rankApplications(jobOfferRequest);
      const processingTime = Date.now() - startTime;

      this.logger.log(
        `Successfully ranked ${rankingResult.applications.length} applications for job ${jobOfferRequest.id} in ${processingTime}ms`,
      );

      // Log des statistiques des scores
      const scores = rankingResult.applications.map((app) => app.score);
      const averageScore = scores.reduce((a, b) => a + b, 0) / scores.length;
      const maxScore = Math.max(...scores);
      const minScore = Math.min(...scores);

      this.logger.debug(
        `Score statistics - Avg: ${averageScore.toFixed(2)}, Max: ${maxScore}, Min: ${minScore}`,
      );

      return rankingResult;
    } catch (error) {
      this.logger.error(
        `Failed to rank applications for job ${jobOfferRequest.id}:`,
        error instanceof Error ? error.stack : error,
      );

      if (error instanceof HttpException) {
        throw error;
      }

      throw new HttpException(
        {
          status: HttpStatus.INTERNAL_SERVER_ERROR,
          error: 'Failed to rank applications',
          details:
            process.env.NODE_ENV === 'development'
              ? error instanceof Error
                ? error.message
                : 'Unknown error'
              : 'Internal server error',
          timestamp: new Date().toISOString(),
        },
        HttpStatus.INTERNAL_SERVER_ERROR,
      );
    }
  }

  @Post('batch-rank')
  async batchRankApplications(
    @Body() jobOffersRequests: JobOfferAIRequest[],
  ): Promise<AIRankingResponse[]> {
    this.logger.log(
      `Received batch ranking request for ${jobOffersRequests.length} job offers`,
    );

    if (!Array.isArray(jobOffersRequests)) {
      throw new HttpException(
        {
          status: HttpStatus.BAD_REQUEST,
          error: 'Request body must be an array of job offers',
        },
        HttpStatus.BAD_REQUEST,
      );
    }

    if (jobOffersRequests.length > 10) {
      throw new HttpException(
        {
          status: HttpStatus.BAD_REQUEST,
          error:
            'Too many job offers in batch request. Maximum 10 job offers allowed',
        },
        HttpStatus.BAD_REQUEST,
      );
    }

    try {
      const results = await Promise.all(
        jobOffersRequests.map((request) => this.rankApplications(request)),
      );

      this.logger.log(
        `Successfully processed batch ranking for ${results.length} job offers`,
      );
      return results;
    } catch (error) {
      this.logger.error(
        'Batch ranking failed:',
        error instanceof Error ? error.stack : error,
      );
      throw new HttpException(
        {
          status: HttpStatus.INTERNAL_SERVER_ERROR,
          error: 'Batch ranking failed',
          details:
            process.env.NODE_ENV === 'development'
              ? error instanceof Error
                ? error.message
                : 'Unknown error'
              : 'Internal server error',
        },
        HttpStatus.INTERNAL_SERVER_ERROR,
      );
    }
  }

  @Get('health')
  async healthCheck(): Promise<{
    status: string;
    timestamp: string;
    service: string;
    version?: string;
  }> {
    return {
      status: 'OK',
      timestamp: new Date().toISOString(),
      service: 'application-ranking',
      version: process.env.npm_package_version || '1.0.0',
    };
  }

  @Post('validate')
  async validateRequest(@Body() jobOfferRequest: JobOfferAIRequest): Promise<{
    valid: boolean;
    errors: string[];
    warnings: string[];
  }> {
    const errors: string[] = [];
    const warnings: string[] = [];

    // Validation de base
    if (!jobOfferRequest.id) {
      errors.push('Job offer ID is required');
    }

    if (!jobOfferRequest.title) {
      warnings.push('Job title is missing');
    }

    if (
      !jobOfferRequest.applications ||
      jobOfferRequest.applications.length === 0
    ) {
      errors.push('At least one application is required');
    } else {
      // Validation des applications
      jobOfferRequest.applications.forEach((app, index) => {
        if (!app.id) {
          errors.push(`Application at index ${index} is missing ID`);
        }
        if (!app.jobSeeker) {
          errors.push(
            `Application ${app.id} is missing job seeker information`,
          );
        } else {
          if (!app.jobSeeker.skills || app.jobSeeker.skills.length === 0) {
            warnings.push(
              `Job seeker in application ${app.id} has no skills listed`,
            );
          }
        }
      });
    }

    return {
      valid: errors.length === 0,
      errors,
      warnings,
    };
  }
}
