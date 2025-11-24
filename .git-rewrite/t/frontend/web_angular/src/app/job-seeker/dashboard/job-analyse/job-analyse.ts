import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-job-analyse',
  templateUrl: './job-analyse.html',
  styleUrls: ['./job-analyse.scss'],
  standalone: false,
})
export class JobAnalyse {
  instructions: string[] = [''];
  country: string = '';
  education: string = '';
  certificate: string = '';
  skills: string = '';
  advice: any = null;
  isGenerating: boolean = false;

  addInstruction() {
    this.instructions.push('');
  }

  removeInstruction(index: number) {
    if (this.instructions.length > 1) {
      this.instructions.splice(index, 1);
    }
  }

  trackByIndex(index: number, item: any): number {
    return index;
  }

  isFormValid(): boolean {
    return !!this.instructions.some(inst => inst.trim()) && !!this.country.trim() && !!this.education.trim() && !!this.certificate.trim() && !!this.skills.trim();
  }

  generateAdvice() {
    if (!this.isFormValid()) {
      console.log('Please fill in all fields');
      return;
    }
    this.isGenerating = true;

    setTimeout(() => {
      const mockAdvice = {
        summary: `Based on your profile and current skills, here's a comprehensive career development plan tailored for ${this.country}.`,
        recommendations: [
          {
            title: 'Enhance Your Technical Stack',
            description: `Focus on deepening your knowledge in ${this.skills.split(',')[0]} and consider certifications like ${this.certificate} to align with ${this.country}'s market.`,
            priority: 'high'
          },
          {
            title: 'Build a Strong Portfolio',
            description: `Create projects showcasing ${this.skills} expertise, tailored to ${this.country}'s industry needs.`,
            priority: 'high'
          },
          {
            title: 'Improve Soft Skills',
            description: `Develop communication skills relevant to ${this.country}'s multicultural workforce.`,
            priority: 'medium'
          }
        ],
        skills: [
          { name: this.skills.split(',')[0], reason: `High demand in ${this.country}'s tech sector` },
          { name: 'System Design', reason: 'Essential for career progression' },
          { name: 'Cloud Computing', reason: `Complements your ${this.certificate} certification` }
        ],
        careerPath: `With your ${this.education} background and ${this.certificate}, you could target mid-level roles in ${this.country} within 6-12 months.`
      };
      this.advice = mockAdvice;
      this.isGenerating = false;
      console.log('Career advice generated!');
    }, 2000);
  }

  getPriorityColor(priority: string): string {
    switch (priority) {
      case 'high':
        return 'bg-orange-500 text-white';
      case 'medium':
        return 'bg-blue-500 text-white';
      default:
        return 'bg-gray-500 text-white';
    }
  }
}