import { Injectable, HttpException, HttpStatus } from '@nestjs/common';
import { GeminiService } from '../gemini/gemini.service';
import {
  JobOfferAIRequest,
  AIRankingResponse,
  ApplicationScore,
} from './dto/job-offer-ai-request.dto';

@Injectable()
export class ApplicationRankingService {
  constructor(private readonly geminiService: GeminiService) {}

  private extractJSON(text: string): string {
    // Remove markdown code blocks
    text = text.replace(/```json/gi, '');
    text = text.replace(/```/g, '');

    // Find the first '{' and last '}'
    const first = text.indexOf('{');
    const last = text.lastIndexOf('}');
    if (first === -1 || last === -1) {
      throw new Error('No JSON object found in AI response:\n' + text);
    }

    return text.substring(first, last + 1);
  }

  async rankApplications(
    jobOfferRequest: JobOfferAIRequest,
  ): Promise<AIRankingResponse> {
    const prompt = this.buildRankingPrompt(jobOfferRequest);

    try {
      const aiResponse = await this.geminiService.generateContent(prompt);
      const jsonString = this.extractJSON(aiResponse);
      const rankingResponse: AIRankingResponse = JSON.parse(jsonString);

      // Validate the response structure
      this.validateRankingResponse(rankingResponse, jobOfferRequest);

      return rankingResponse;
    } catch (error) {
      console.error('AI Ranking error:', error);
      // Fallback: return applications with neutral scores
      return this.getFallbackRanking(jobOfferRequest);
    }
  }

  private buildRankingPrompt(jobOfferRequest: JobOfferAIRequest): string {
    return `
    You are an expert HR AI assistant. Your task is to evaluate job applications and assign relevance scores (0-100) based on how well each candidate matches the job requirements.

    JOB OFFER DETAILS:
    - Title: ${jobOfferRequest.title}
    - Company: ${jobOfferRequest.company}
    - Location: ${jobOfferRequest.location}
    - Type: ${jobOfferRequest.type}
    - Experience Required: ${jobOfferRequest.experience}
    - Salary: ${jobOfferRequest.salary}
    - Description: ${jobOfferRequest.description}
    - Required Skills: ${jobOfferRequest.skills.join(', ')}
    - Requirements: ${jobOfferRequest.requirements.join(', ')}

    EVALUATION CRITERIA:
    1. Skills Match (40%): How well the candidate's skills align with required skills
    2. Experience Relevance (30%): Relevance of candidate's experience to job requirements
    3. Education & Qualifications (20%): Match between candidate's education and job requirements
    4. Motivation & Profile Fit (10%): Based on motivation letter and profile description

    IMPORTANT INSTRUCTIONS:
    - Return ONLY valid JSON, no additional text or explanations
    - Score range: 0-100 (100 = perfect match)
    - Be fair and objective in your evaluation
    - Consider both technical and soft skills
    - Account for different experience levels and backgrounds

    RETURN FORMAT (JSON only):
    {
      "id": "${jobOfferRequest.id}",
      "applications": [
        {"id": 123, "score": 85},
        {"id": 124, "score": 72}
      ]
    }

    APPLICATIONS DATA:
    ${JSON.stringify(jobOfferRequest.applications, null, 2)}

    Now, evaluate each application and return the JSON response:
    `;
  }

  private validateRankingResponse(
    response: AIRankingResponse,
    originalRequest: JobOfferAIRequest,
  ): void {
    if (!response.id || !response.applications) {
      throw new Error('Invalid AI response structure');
    }

    if (response.applications.length !== originalRequest.applications.length) {
      throw new Error('AI response missing some applications');
    }

    // Validate each application has valid score
    response.applications.forEach((app) => {
      if (typeof app.score !== 'number' || app.score < 0 || app.score > 100) {
        throw new Error(
          `Invalid score for application ${app.id}: ${app.score}`,
        );
      }
    });
  }

  private getFallbackRanking(
    jobOfferRequest: JobOfferAIRequest,
  ): AIRankingResponse {
    // Fallback: assign neutral scores and sort by application date
    const applications: ApplicationScore[] = jobOfferRequest.applications.map(
      (app) => ({
        id: app.id,
        score: 50, // Neutral score
      }),
    );

    return {
      id: jobOfferRequest.id,
      applications,
    };
  }
}
