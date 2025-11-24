import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { JobOffer } from '../../../../../types';
import { faCheck, faTimes, faPlus } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-edit-job-dialog',
  standalone: false,
  templateUrl: './edit-job-dialog.html',
  styleUrls: ['./edit-job-dialog.scss']
})
export class EditJobDialog {
  editedJob: JobOffer;
  newSkill: string = '';
  newRequirement: string = '';

  // Icônes FontAwesome
  faCheck = faCheck;
  faTimes = faTimes;
  faPlus = faPlus;

  constructor(
    public dialogRef: MatDialogRef<EditJobDialog>,
    @Inject(MAT_DIALOG_DATA) public data: { job: JobOffer }
  ) {
    // Créer une copie profonde pour l'édition
    this.editedJob = {
      ...data.job,
      skills: [...data.job.skills],
      requirements: data.job.requirements ? [...data.job.requirements] : []
    };
  }

  addSkill(): void {
    if (this.newSkill.trim() && !this.editedJob.skills.includes(this.newSkill.trim())) {
      this.editedJob.skills.push(this.newSkill.trim());
      this.newSkill = '';
    }
  }

  removeSkill(skillToRemove: string): void {
    this.editedJob.skills = this.editedJob.skills.filter(skill => skill !== skillToRemove);
  }

  addRequirement(): void {
    if (this.newRequirement.trim() && !this.editedJob.requirements!.includes(this.newRequirement.trim())) {
      this.editedJob.requirements!.push(this.newRequirement.trim());
      this.newRequirement = '';
    }
  }

  removeRequirement(requirementToRemove: string): void {
    this.editedJob.requirements = this.editedJob.requirements!.filter(req => req !== requirementToRemove);
  }

  onSave(): void {
    this.dialogRef.close(this.editedJob);
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}