import { Injectable, HttpException, HttpStatus } from '@nestjs/common';
import { GeminiService } from '../gemini/gemini.service';
import { GenerateCVRequest } from './dto/generate-cv-request.dto';
import { GenerateCVResponse } from './dto/generate-cv-response.dto';
@Injectable()
export class CvGenerationService {
  constructor(private readonly geminiService: GeminiService) {}

  async generateATSCV(request: GenerateCVRequest): Promise<GenerateCVResponse> {
    const prompt = this.buildATSCVPrompt(request);

    try {
      const aiResponse = await this.geminiService.generateContent(prompt);
      return this.parseAIResponse(aiResponse, request);
    } catch (error) {
      console.error('CV Generation error:', error);
      throw new HttpException(
        'Failed to generate ATS-optimized CV',
        HttpStatus.INTERNAL_SERVER_ERROR,
      );
    }
  }

  private buildATSCVPrompt(request: GenerateCVRequest): string {
    const { jobSeeker, jobOffer } = request;

    return `
    ROLE: You are an expert ATS (Applicant Tracking System) CV optimizer and professional resume writer.
    
    TASK: Generate a perfectly optimized ATS-friendly CV that will pass through automated screening systems and impress recruiters for the specific job opportunity.
    
    JOB OPPORTUNITY DETAILS:
    - Position: ${jobOffer.title}
    - Company: ${jobOffer.company}
    - Location: ${jobOffer.location}
    - Type: ${jobOffer.type}
    - Experience Required: ${jobOffer.experience}
    - Key Skills: ${jobOffer.skills.join(', ')}
    - Requirements: ${jobOffer.requirements.join(', ')}
    - Job Description: ${jobOffer.description}
    
    CANDIDATE PROFILE:
    - Name: ${jobSeeker.fullName}
    - Title: ${jobSeeker.title || 'Professional'}
    - Email: ${jobSeeker.email}
    - Phone: ${jobSeeker.phone_number || 'Not provided'}
    - Nationality: ${jobSeeker.nationality || 'Not provided'}
    - Professional Summary: ${jobSeeker.description || 'Experienced professional'}
    - Skills: ${jobSeeker.skills.join(', ')}
    - Experience: ${JSON.stringify(jobSeeker.experience)}
    - Education: ${JSON.stringify(jobSeeker.education)}
    
    ATS OPTIMIZATION REQUIREMENTS:
    1. MUST include exact keywords from the job description
    2. Use standard section headers (Professional Summary, Work Experience, Education, Skills)
    3. Quantify achievements with numbers and metrics
    4. Match skills and experience to job requirements
    5. Use reverse chronological order
    6. Keep formatting clean and machine-readable
    
    RESPONSE FORMAT (JSON only):
    {
      "sections": [
        {
          "title": "Professional Summary",
          "content": "Dynamic professional with X years in [relevant field]...",
          "order": 1
        },
        {
          "title": "Work Experience", 
          "content": "[Job Title] at [Company] | [Dates]\\n• Achieved X% improvement in Y\\n• Managed team of Z people\\n• Implemented [specific technology]",
          "order": 2
        },
        {
          "title": "Education",
          "content": "[Degree] in [Field] | [School] | [Year]\\n[Degree] in [Field] | [School] | [Year]",
          "order": 3
        },
        {
          "title": "Technical Skills",
          "content": "• ${jobOffer.skills[0] || 'Programming'}: Advanced\\n• ${jobOffer.skills[1] || 'Tools'}: Proficient\\n• ${jobOffer.skills[2] || 'Methodologies'}: Experienced",
          "order": 4
        }
      ],
      "summary": "Overall candidate suitability summary",
      "optimizedSkills": ["skill1", "skill2", "skill3"],
      "atsScore": 85,
      "keywords": ["keyword1", "keyword2", "keyword3"],
      "rawContent": "Full text content for PDF generation"
    }

    IMPORTANT: Return ONLY valid JSON, no additional text or explanations.
    Focus on making this CV pass ATS systems and stand out for this specific job.
    `;
  }

  private parseAIResponse(
    aiResponse: string,
    request: GenerateCVRequest,
  ): GenerateCVResponse {
    try {
      // Extract JSON from AI response
      const jsonString = this.extractJSON(aiResponse);
      const response: GenerateCVResponse = JSON.parse(jsonString);

      // Validate response structure
      this.validateCVResponse(response);

      return response;
    } catch (error) {
      console.error('Failed to parse AI response:', error);
      // Fallback to basic CV structure
      return this.getFallbackCV(request);
    }
  }

  private extractJSON(text: string): string {
    text = text.replace(/```json/gi, '');
    text = text.replace(/```/g, '');

    const first = text.indexOf('{');
    const last = text.lastIndexOf('}');
    if (first === -1 || last === -1) {
      throw new Error('No JSON object found in AI response');
    }

    return text.substring(first, last + 1);
  }

  private validateCVResponse(response: GenerateCVResponse): void {
    if (!response.sections || !Array.isArray(response.sections)) {
      throw new Error('Invalid CV response structure');
    }

    const requiredSections = [
      'Professional Summary',
      'Work Experience',
      'Education',
      'Technical Skills',
    ];
    const sectionTitles = response.sections.map((s) => s.title);

    requiredSections.forEach((section) => {
      if (!sectionTitles.includes(section)) {
        throw new Error(`Missing required section: ${section}`);
      }
    });
  }

  private getFallbackCV(request: GenerateCVRequest): GenerateCVResponse {
    const { jobSeeker, jobOffer } = request;

    return {
      sections: [
        {
          title: 'Professional Summary',
          content: `Results-driven ${jobSeeker.title || 'professional'} with expertise in ${jobSeeker.skills.slice(0, 3).join(', ')}. Seeking ${jobOffer.title} position at ${jobOffer.company}.`,
          order: 1,
        },
        {
          title: 'Work Experience',
          content: jobSeeker.experience
            .map(
              (exp) =>
                `${exp.position || 'Role'} at ${exp.company || 'Company'} | ${exp.startDate || 'Date'} - ${exp.endDate || 'Present'}\n${exp.description || 'Responsible for key deliverables'}`,
            )
            .join('\n\n'),
          order: 2,
        },
        {
          title: 'Education',
          content: jobSeeker.education
            .map(
              (edu) =>
                `${edu.degree || 'Degree'} in ${edu.field || 'Field'} | ${edu.school || 'School'} | ${edu.graduationDate || 'Year'}`,
            )
            .join('\n'),
          order: 3,
        },
        {
          title: 'Technical Skills',
          content: jobSeeker.skills.map((skill) => `• ${skill}`).join('\n'),
          order: 4,
        },
      ],
      summary: `Strong candidate for ${jobOffer.title} with relevant background in ${jobSeeker.skills.slice(0, 2).join(' and ')}.`,
      optimizedSkills: jobSeeker.skills,
      atsScore: 70,
      keywords: jobOffer.skills,
      rawContent: this.generateRawContent(jobSeeker, jobOffer),
    };
  }

  private generateRawContent(jobSeeker: any, jobOffer: any): string {
    return `
${jobSeeker.fullName}
${jobSeeker.email} | ${jobSeeker.phone_number || ''}

PROFESSIONAL SUMMARY
Experienced ${jobSeeker.title} seeking ${jobOffer.title} position at ${jobOffer.company}.

WORK EXPERIENCE
${jobSeeker.experience
  .map(
    (exp: any) =>
      `${exp.position} at ${exp.company}
  ${exp.startDate} - ${exp.endDate}
  ${exp.description}`,
  )
  .join('\n\n')}

EDUCATION
${jobSeeker.education
  .map(
    (edu: any) =>
      `${edu.degree} in ${edu.field}
  ${edu.school}, ${edu.graduationDate}`,
  )
  .join('\n\n')}

SKILLS
${jobSeeker.skills.join(' • ')}
    `.trim();
  }
}
