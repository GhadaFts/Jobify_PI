import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile-initial',
  standalone: false,
  templateUrl: './profile-initial.html',
  styleUrls: ['./profile-initial.scss']
})
export class ProfileInitial {
  currentStep = 1;
  progress = 25;
  imagePreview: string | null = null;

  constructor(private router: Router) {}

  setStep(step: number) {
    this.currentStep = step;
    this.updateProgress();
  }

  nextStep() {
    if (this.currentStep < 4) { // Updated to 4 steps to match HTML
      this.currentStep++;
      this.updateProgress();
    }
  }

  saveAndComplete() {
    this.currentStep = 4; // Move to congratulations
    this.progress = 100;   // Set progress to 100%
  }

  private updateProgress() {
    this.progress = Math.round((this.currentStep / 4) * 100); // Updated to 4 steps
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      this.imagePreview = URL.createObjectURL(file);
    }
  }

  goToDashboard() {
    this.router.navigate(['/job-seeker/dashboard']); // Route to dashboard
  }
}