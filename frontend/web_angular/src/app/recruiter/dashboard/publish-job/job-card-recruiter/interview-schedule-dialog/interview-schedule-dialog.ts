import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Application } from '../../../../../types';
import { ToastService } from '../../../../../services/toast.service'; // Assurez-vous d'avoir ce service

@Component({
  selector: 'app-interview-schedule-dialog',
  standalone: false,
  templateUrl: './interview-schedule-dialog.html',
  styleUrls: ['./interview-schedule-dialog.scss']
})
export class InterviewScheduleDialog {
  interviewDate: string = '';
  interviewTime: string = '';
  interviewType: string = 'local'; // 'local' ou 'online'
  interviewLocation: string = '';
  additionalNotes: string = '';
  meetingLink: string = '';
  duration: number = 60;
  
  // Propriété pour gérer les conflits
  timeConflict: boolean = false;

  constructor(
    public dialogRef: MatDialogRef<InterviewScheduleDialog>,  
    public toastService: ToastService,

    @Inject(MAT_DIALOG_DATA) public data: { application: Application }
  ) {}

  // Méthode pour vérifier si l'interview est en ligne
  get isOnlineInterview(): boolean {
    return this.interviewType === 'online';
  }

  // Méthode pour vérifier les conflits d'horaire
  checkTimeConflict(): void {
    if (!this.interviewDate || !this.interviewTime) {
      this.timeConflict = false;
      return;
    }

    const existingInterviews = this.getExistingInterviews();
    const selectedDateTime = new Date(this.interviewDate + 'T' + this.interviewTime);
    
    this.timeConflict = existingInterviews.some(interview => {
      const interviewDateTime = new Date(interview.date + 'T' + interview.time);
      const timeDiff = Math.abs(interviewDateTime.getTime() - selectedDateTime.getTime());
      return timeDiff < 30 * 60 * 1000; // Conflit si moins de 30 minutes d'écart
    });
  }

  // Méthode pour obtenir les interviews existantes (simulée)
  private getExistingInterviews(): any[] {
    return [
      {
        date: '2024-01-25',
        time: '14:00',
        candidate: 'Autre Candidat',
        duration: 60
      },
      {
        date: '2024-01-26',
        time: '10:30', 
        candidate: 'Un Autre Candidat',
        duration: 45
      }
    ];
  }

  // Méthode appelée quand la date change
  onDateChange(): void {
    this.checkTimeConflict();
  }

  // Méthode appelée quand l'heure change
  onTimeChange(): void {
    this.checkTimeConflict();
  }

  // Méthode appelée quand le type d'interview change
  onInterviewTypeChange(): void {
    // Réinitialiser la localisation si on passe en ligne
    if (this.isOnlineInterview) {
      this.interviewLocation = 'Jitsi Meet';
      this.generateJitsiLink();
    } else {
      this.interviewLocation = '';
      this.meetingLink = '';
    }
  }

  // Générer un lien Jitsi Meet
  generateJitsiLink(): void {
    const roomName = `interview-${this.data.application.jobSeeker.fullName.toLowerCase().replace(/\s+/g, '-')}-${Date.now().toString().slice(-6)}`;
    this.meetingLink = `https://meet.jit.si/${roomName}`;
  }

  // Méthode pour obtenir la date de demain (format YYYY-MM-DD)
  getTomorrowDate(): string {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    return tomorrow.toISOString().split('T')[0];
  }

  // Valider et soumettre le formulaire
  submit(): void {
  if (this.timeConflict) {
    this.toastService.error('Please choose a different time to avoid schedule conflict.');
    return;
  }

  if (!this.interviewDate || !this.interviewTime || !this.interviewLocation) {
    this.toastService.error('Please fill all required fields.');
    return;
  }

  if (this.isOnlineInterview && !this.meetingLink) {
    this.toastService.error('Please generate a meeting link for online interviews.');
    return;
  }

  const result = {
    interviewDate: this.interviewDate,
    interviewTime: this.interviewTime,
    interviewType: this.interviewType,
    interviewLocation: this.interviewLocation,
    additionalNotes: this.additionalNotes,
    meetingLink: this.meetingLink,
    duration: this.duration
  };

  this.dialogRef.close(result);
}
  cancel(): void {
    this.dialogRef.close();
  }
}