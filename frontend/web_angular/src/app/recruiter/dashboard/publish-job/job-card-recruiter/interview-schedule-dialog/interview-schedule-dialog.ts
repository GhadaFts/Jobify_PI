import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Application } from '../../../../../types';

@Component({
  selector: 'app-interview-schedule-dialog',
  templateUrl: './interview-schedule-dialog.html',
  standalone: false,
})
export class InterviewScheduleDialog {
  interviewData = {
    interviewDate: '',
    interviewTime: '',
    location: '',
    additionalNotes: '',
    duration: '1 hour',
    interviewType: 'video_call'
  };

  interviewTypes = [
    { value: 'video_call', label: 'Appel vidéo' },
    { value: 'in_person', label: 'En personne' },
    { value: 'phone_call', label: 'Appel téléphonique' }
  ];

  durations = [
    { value: '30 minutes', label: '30 minutes' },
    { value: '1 hour', label: '1 heure' },
    { value: '1.5 hours', label: '1 heure 30' },
    { value: '2 hours', label: '2 heures' }
  ];

  constructor(
    public dialogRef: MatDialogRef<InterviewScheduleDialog>,
    @Inject(MAT_DIALOG_DATA) public data: { application: Application }
  ) {}

  submit(): void {
    if (this.interviewData.interviewDate && this.interviewData.interviewTime && this.interviewData.location) {
      this.dialogRef.close(this.interviewData);
    }
  }

  close(): void {
    this.dialogRef.close();
  }
}