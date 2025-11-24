import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastService } from '../../services/toast.service';
import { trigger, transition, style, animate } from '@angular/animations';
import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faXmark } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-profile-initial',
  standalone: false,
  templateUrl: './profile-initial.html',
  styleUrls: ['./profile-initial.scss'],
  animations: [
    trigger('formAnimation', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('500ms ease-in-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ]),
      transition(':leave', [
        animate('500ms ease-in-out', style({ opacity: 0, transform: 'translateY(20px)' }))
      ])
    ])
  ]
})
export class ProfileInitial implements OnInit {
  currentStep = 1;
  progress = 25;
  imagePreview: string | null = null;
  newSkill: string = '';
  profile: {
    profilePhoto: string;
    fullName: string;
    title: string;
    nationality: string;
    countryCode: string;
    phone: string;
    gender: string;
    dateOfBirth: string;
    skills: string[];
    experience: { position: string; company: string; startDate: string; endDate: string; description: string }[];
    education: { school: string; degree: string; field: string; graduationDate: string }[];
    biography: string;
    portfolio: string;
    github: string;
    twitter: string;
    facebook: string;
  } = {
    profilePhoto: '',
    fullName: '',
    title: '',
    nationality: '',
    countryCode: '',
    phone: '',
    gender: '',
    dateOfBirth: '',
    skills: [],
    experience: [],
    education: [],
    biography: '',
    portfolio: '',
    github: '',
    twitter: '',
    facebook: ''
  };

  // Country code mapping
  private countryCodeMap: { [key: string]: string } = {
    French: '+33',
    American: '+1',
    Canadian: '+1',
    British: '+44',
    German: '+49',
    Indian: '+91'
    // Add more mappings as needed
  };

  constructor(private router: Router, public toastService: ToastService, private iconLibrary: FaIconLibrary) {
    this.iconLibrary.addIcons(faXmark);
  }

  ngOnInit() {
    this.updateProgress();
  }

  setStep(step: number) {
    this.currentStep = step;
    this.updateProgress();
  }

  nextStep() {
    if (this.currentStep === 1 && !this.validatePersonalForm()) {
      this.toastService.error('Please fill out all required fields in Personal Information.');
      return;
    }
    if (this.currentStep === 2 && !this.validateProfileForm()) {
      this.toastService.error('Please fill out all required fields and add at least one skill, experience, and education entry.');
      return;
    }
    if (this.currentStep < 4) {
      this.currentStep++;
      this.updateProgress();
    }
  }

  validatePersonalForm(): boolean {
    return !!(
      this.profile.profilePhoto &&
      this.profile.fullName &&
      this.profile.title &&
      this.profile.nationality &&
      this.profile.phone &&
      this.profile.gender &&
      this.profile.dateOfBirth
    );
  }

  validateProfileForm(): boolean {
    // Check if skills, experience, and education arrays have at least one entry
    if (!this.profile.skills.length || !this.profile.experience.length || !this.profile.education.length) {
      return false;
    }
    // Check biography
    if (!this.profile.biography) {
      return false;
    }
    // Check all required fields in experience entries
    for (const exp of this.profile.experience) {
      if (!exp.position || !exp.company || !exp.startDate || !exp.description) {
        return false;
      }
    }
    // Check all required fields in education entries
    for (const edu of this.profile.education) {
      if (!edu.school || !edu.degree || !edu.field || !edu.graduationDate) {
        return false;
      }
    }
    return true;
  }

  isProfileComplete(): boolean {
    return this.validatePersonalForm() && this.validateProfileForm();
  }

  updateProgress() {
    this.progress = Math.round((this.currentStep / 4) * 100);
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      const validTypes = ['image/jpeg', 'image/png'];
      const maxSize = 5 * 1024 * 1024; // 5MB

      if (!validTypes.includes(file.type)) {
        this.toastService.error('Invalid file type. Please upload PNG or JPEG.');
        return;
      }

      if (file.size > maxSize) {
        this.toastService.error('File size exceeds 5MB limit.');
        return;
      }

      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreview = reader.result as string;
        this.profile.profilePhoto = this.imagePreview;
        this.toastService.success('Profile photo uploaded successfully!');
      };
      reader.onerror = () => {
        this.toastService.error('Error reading file.');
      };
      reader.readAsDataURL(file);
    }
  }

  updateCountryCode() {
    this.profile.countryCode = this.countryCodeMap[this.profile.nationality] || '';
  }

  addSkill() {
    if (this.newSkill.trim()) {
      this.profile.skills.push(this.newSkill.trim());
      this.newSkill = '';
      this.toastService.success('Skill added successfully!');
    } else {
      this.toastService.error('Please enter a skill.');
    }
  }

  removeSkill(index: number) {
    this.profile.skills.splice(index, 1);
    this.toastService.success('Skill removed successfully!');
  }

  addExperience() {
    this.profile.experience.push({
      position: '',
      company: '',
      startDate: '',
      endDate: '',
      description: ''
    });
    this.toastService.success('New experience entry added!');
  }

  removeExperience(index: number) {
    this.profile.experience.splice(index, 1);
    this.toastService.success('Experience entry removed!');
  }

  addEducation() {
    this.profile.education.push({
      school: '',
      degree: '',
      field: '',
      graduationDate: ''
    });
    this.toastService.success('New education entry added!');
  }

  removeEducation(index: number) {
    this.profile.education.splice(index, 1);
    this.toastService.success('Education entry removed!');
  }

  saveAndComplete() {
    if (!this.validatePersonalForm() || !this.validateProfileForm()) {
      this.toastService.error('Please complete all required fields in Personal and Profile sections.');
      return;
    }
    this.currentStep = 4;
    this.updateProgress();
    this.toastService.success('Profile saved successfully!');
  }

  goToDashboard() {
    if (!this.isProfileComplete()) {
      this.toastService.error('Please complete all required fields to browse jobs.');
      return;
    }
    this.router.navigate(['/job-seeker/dashboard']);
    this.toastService.success('Redirecting to dashboard...');
  }
}