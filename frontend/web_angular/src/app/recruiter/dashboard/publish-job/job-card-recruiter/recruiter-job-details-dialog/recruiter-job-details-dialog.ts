import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { JobOffer } from '../../../../../types';
import {  faCheck, faListCheck, } from '@fortawesome/free-solid-svg-icons';


@Component({
  selector: 'app-recruiter-job-details-dialog',
  standalone: false,
  templateUrl: './recruiter-job-details-dialog.html',
  styleUrls: ['./recruiter-job-details-dialog.scss']
})
export class RecruiterJobDetailsDialog {
  faListCheck = faListCheck;
    faCheck = faCheck;
  constructor(
    public dialogRef: MatDialogRef<RecruiterJobDetailsDialog>,
    @Inject(MAT_DIALOG_DATA) public data: { job: JobOffer }
  ) {}
}