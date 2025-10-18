import { Component } from '@angular/core';

@Component({
  selector: 'app-profile-initial',
  standalone: false,
  templateUrl: './profile-initial.html',
  styleUrl: './profile-initial.scss'
})
export class ProfileInitial {
  currentStep = 1;
  progress = 25;
  imagePreview: string | null = null;

  setStep(step: number) {
    this.currentStep = step;
    this.updateProgress();
  }

  nextStep() {
    if (this.currentStep < 3) {
      this.currentStep++;
      this.updateProgress();
    }
  }

  saveAndComplete() {
    this.currentStep = 4;
    this.progress = 100;
  }

  private updateProgress() {
    this.progress = Math.round((this.currentStep / 3) * 100);
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      this.imagePreview = URL.createObjectURL(file);
    }
  }
}
