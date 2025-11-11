import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Application } from '../../../../../types';
import { InterviewsService } from '../../../../../services/interviews.service';
import { ToastService } from '../../../../../services/toast.service';

@Component({
  selector: 'app-interview-schedule-dialog',
  standalone: false,
  templateUrl: './interview-schedule-dialog.html',
  styleUrls: ['./interview-schedule-dialog.scss']
})
export class InterviewScheduleDialog {
  interviewDate: string = '';
  interviewTime: string = '';
  interviewType: string = 'local';
  interviewLocation: string = '';
  additionalNotes: string = '';
  meetingLink: string = '';
  duration: number = 60;
  
  timeConflict: boolean = false;

  constructor(
    public dialogRef: MatDialogRef<InterviewScheduleDialog>,
    @Inject(MAT_DIALOG_DATA) public data: { application: Application },
    private interviewsService: InterviewsService,
    private toastService: ToastService
  ) {}

  get isOnlineInterview(): boolean {
    return this.interviewType === 'online';
  }

  // Vérifier les conflits en temps réel
  checkTimeConflict(): void {
    if (!this.interviewDate || !this.interviewTime) {
      this.timeConflict = false;
      return;
    }

    this.timeConflict = this.interviewsService.checkTimeConflict(
      this.interviewDate, 
      this.interviewTime, 
      this.duration
    );
  }

  onDateChange(): void {
    this.checkTimeConflict();
  }

  onTimeChange(): void {
    this.checkTimeConflict();
  }

  onDurationChange(): void {
    this.checkTimeConflict();
  }

  onInterviewTypeChange(): void {
    if (this.isOnlineInterview) {
      this.interviewLocation = 'Jitsi Meet';
      this.generateJitsiLink();
    } else {
      this.interviewLocation = '';
      this.meetingLink = '';
    }
  }

  generateJitsiLink(): void {
    const roomName = `interview-${this.data.application.jobSeeker.fullName.toLowerCase().replace(/\s+/g, '-')}-${Date.now().toString().slice(-6)}`;
    this.meetingLink = `https://meet.jit.si/${roomName}`;
  }

  getTomorrowDate(): string {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    return tomorrow.toISOString().split('T')[0];
  }

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