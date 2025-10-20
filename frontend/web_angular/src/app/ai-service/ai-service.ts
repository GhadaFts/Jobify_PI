import { Injectable } from '@angular/core';
import { GoogleGenAI } from '@google/genai';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AiService {
  private genAI = new GoogleGenAI({ apiKey: environment.Gemini_API_KEY });

  constructor() {}

  async ask(prompt: string): Promise<string> {
    const model = this.genAI.models.generateContentStream({
      model: "gemini-2.5-flash-lite",
      contents: prompt
    });
    var result = "";
    for await (const chunk of await model) {
      result += chunk.text;
    }
    return result;
  }
}
