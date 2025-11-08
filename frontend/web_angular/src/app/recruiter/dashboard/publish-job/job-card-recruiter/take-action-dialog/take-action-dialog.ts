import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Application } from '../../../../../types';

@Component({
  selector: 'app-take-action-dialog',
  templateUrl: './take-action-dialog.html',
  standalone: false,
})
export class TakeActionDialog {
  selectedAction: 'accepted' | 'rejected' | null = null;

  constructor(
    public dialogRef: MatDialogRef<TakeActionDialog>,
    @Inject(MAT_DIALOG_DATA) public data: { application: Application }
  ) {}

  submit(): void {
    if (this.selectedAction) {
      this.dialogRef.close(this.selectedAction);
    }
  }

  close(): void {
    this.dialogRef.close();
  }
}