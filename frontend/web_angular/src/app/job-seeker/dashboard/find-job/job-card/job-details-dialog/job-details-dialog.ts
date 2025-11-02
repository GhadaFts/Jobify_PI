import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { JobOffer } from '../../../../../types'; // Adjust the import path
import {  faCheck, faListCheck, } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-job-details-dialog',
  standalone: false,
  templateUrl: './job-details-dialog.html',
  styleUrls: ['./job-details-dialog.scss']
})
export class JobDetailsDialog {
  faListCheck = faListCheck;
    faCheck = faCheck;
  constructor(
    public dialogRef: MatDialogRef<JobDetailsDialog>,
    @Inject(MAT_DIALOG_DATA) public data: { job: JobOffer }
  ) {}
}