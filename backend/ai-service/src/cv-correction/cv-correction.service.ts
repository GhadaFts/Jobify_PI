import { Injectable, HttpException, HttpStatus } from '@nestjs/common';
import { GeminiService } from '../gemini/gemini.service';
import { CorrectCvDto } from './dto/correct-cv.dto';
import {
  CvAnalysisResponse,
  CvSuggestion,
  ImprovedSummary,
  JobSeekerProfile,
} from './dto/cv-analysis.response';

@Injectable()
export class CvCorrectionService {
  private readonly defaultPrompt = `
You are a professional CV analyzer. 
You will receive the raw text extracted from a user's CV.

Your goal is to analyze it and return a JSON object (NO explanations, NO markdown, NO text outside JSON).
The JSON must strictly follow this structure:

{
  "cvScore": number, // overall score out of 100
  "cvSuggestions": [
    {
      "id": string, // unique id like "weak_1" or "missing_3"
      "type": "success" | "warning" | "info" | "missing",
      "title": string,
      "message": string
    }
  ],
  "improvedSummary": {
    "overallAssessment": string,
    "strengths": string[],
    "improvements": string[]
  },
  "profile": {
    "id": number,
    "email": string,
    "password": string,
    "fullName": string,
    "role": string,
    "photo_profil": string,
    "twitter_link": string,
    "web_link": string,
    "github_link": string,
    "facebook_link": string,
    "description": string,
    "phone_number": string,
    "nationality": string,
    "skills": string[],
    "experience": [
      {
        "position": string,
        "company": string,
        "startDate": string,
        "endDate": string,
        "description": string
      }
    ],
    "education": [
      {
        "degree": string,
        "field": string,
        "school": string,
        "graduationDate": string
      }
    ],
    "title": string,
    "date_of_birth": string,
    "gender": string
  }
}

Guidelines for the analysis:
- Identify STRONG sections (good content) → type = "success"
- Identify WEAK or unclear sections → type = "warning"
- Identify INFO or general improvement tips → type = "info"
- Identify MISSING sections (e.g., missing contact info, summary, education) → type = "missing"
- Score between 0–100 based on completeness, clarity, and structure.

Return only JSON.
Now analyze this CV:
`;

  constructor(private geminiService: GeminiService) {}

  async analyzeCv(correctCvDto: CorrectCvDto): Promise<CvAnalysisResponse> {
    const fullPrompt = `${this.defaultPrompt}\n\n${correctCvDto.cvContent}`;

    try {
      const result = await this.geminiService.generateContent(fullPrompt);
      return this.parseGeminiResponse(result);
    } catch (error) {
      console.error('CV Analysis Error:', error);
      throw new HttpException(
        'Error analyzing CV',
        HttpStatus.INTERNAL_SERVER_ERROR,
      );
    }
  }

  private parseGeminiResponse(result: string): CvAnalysisResponse {
    try {
      return JSON.parse(result) as CvAnalysisResponse;
    } catch (_parseError) {
      console.warn('JSON parse failed, trying to extract JSON from response');

      const jsonMatch = result.match(/\{[\s\S]*\}/);
      if (jsonMatch) {
        try {
          return JSON.parse(jsonMatch[0]) as CvAnalysisResponse;
        } catch (error) {
          console.error('Error parsing extracted JSON:', error);
          return this.handleNonJsonResponse(result);
        }
      } else {
        return this.handleNonJsonResponse(result);
      }
    }
  }

  private handleNonJsonResponse(result: string): CvAnalysisResponse {
    const scoreMatch = result.match(/(?:score|rating):\s*(\d+)/i);
    const score = scoreMatch ? parseInt(scoreMatch[1], 10) : 50;

    const sections = result.split(/[\n\r]+/);
    const suggestions: CvSuggestion[] = [];
    let currentType: 'success' | 'warning' | 'info' | 'missing' = 'info';

    sections.forEach((section, index) => {
      if (section.toLowerCase().includes('strength') || section.includes('✓')) {
        currentType = 'success';
      } else if (
        section.toLowerCase().includes('improve') ||
        section.toLowerCase().includes('weak') ||
        section.includes('⚠')
      ) {
        currentType = 'warning';
      } else if (
        section.toLowerCase().includes('missing') ||
        section.includes('❌')
      ) {
        currentType = 'missing';
      }

      if (
        section.trim() &&
        !section.toLowerCase().includes('section') &&
        section.length > 10
      ) {
        suggestions.push({
          id: `suggestion_${index}`,
          type: currentType,
          title: currentType.charAt(0).toUpperCase() + currentType.slice(1),
          message: section.trim(),
        });
      }
    });

    const defaultProfile: JobSeekerProfile = {
      id: 0,
      email: '',
      password: '',
      fullName: '',
      role: 'jobseeker',
      photo_profil: '',
      twitter_link: '',
      web_link: '',
      github_link: '',
      facebook_link: '',
      description: '',
      phone_number: '',
      nationality: '',
      skills: [],
      experience: [],
      education: [],
      title: '',
      date_of_birth: '',
      gender: '',
    };

    const improvedSummary: ImprovedSummary = {
      overallAssessment: result.split('\n')[0] || 'Analysis completed',
      strengths: suggestions
        .filter((s) => s.type === 'success')
        .map((s) => s.message),
      improvements: suggestions
        .filter((s) => s.type === 'warning' || s.type === 'missing')
        .map((s) => s.message),
    };

    return {
      cvScore: score,
      cvSuggestions: suggestions,
      improvedSummary,
      profile: defaultProfile,
    };
  }
}
