import { Injectable, HttpException, HttpStatus } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import axios from 'axios';

interface GeminiResponse {
  candidates: Array<{
    content: {
      parts: Array<{
        text: string;
      }>;
    };
  }>;
}

interface AxiosError {
  response?: {
    data: unknown;
  };
}

@Injectable()
export class GeminiService {
  private readonly apiKey: string;
  private readonly baseUrl: string =
    'https://generativelanguage.googleapis.com/v1beta/models';

  constructor(private configService: ConfigService) {
    const apiKey = this.configService.get<string>('GEMINI_API_KEY');
    if (!apiKey) {
      throw new Error('GEMINI_API_KEY is not defined in environment variables');
    }
    this.apiKey = apiKey;
  }

  async generateContent(prompt: string): Promise<string> {
    try {
      const response = await axios.post<GeminiResponse>(
        `${this.baseUrl}/gemini-2.5-flash-lite:generateContent?key=${this.apiKey}`,
        {
          contents: [
            {
              parts: [
                {
                  text: prompt,
                },
              ],
            },
          ],
        },
        {
          headers: {
            'Content-Type': 'application/json',
          },
        },
      );

      let result = '';
      if (response.data.candidates && response.data.candidates[0]?.content) {
        result = response.data.candidates[0].content.parts[0]?.text || '';
      }

      return result;
    } catch (error: unknown) {
      const errorMessage =
        error instanceof Error ? error.message : 'Unknown error occurred';
      const responseData = (error as AxiosError)?.response?.data;

      console.error('Gemini API Error:', responseData || errorMessage);
      throw new HttpException(
        'Error communicating with AI service',
        HttpStatus.INTERNAL_SERVER_ERROR,
      );
    }
  }
}
