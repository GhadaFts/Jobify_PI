import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastService } from '../../services/toast.service';
import { trigger, transition, style, animate } from '@angular/animations';
import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faXmark, faPlus } from '@fortawesome/free-solid-svg-icons';
import { UserService } from '../../services/user.service';
import { of, switchMap } from 'rxjs';

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
  faXmark = faXmark;
  faPlus = faPlus;
  currentStep = 1;
  progress = 25;
  imagePreview: string | null = null;
  newService: string = '';
  profile: {
    companyLogo: string;
    companyName: string;
    nationality: string;
    countryCode: string;
    phone: string;
    companyAddress: string;
    speciality: string;
    employeesNumber: number | null;
    services: string[];
    biography: string;
    webLink: string;
    github: string;
    twitter: string;
    facebook: string;
  } = {
    companyLogo: '',
    companyName: '',
    nationality: '',
    countryCode: '',
    phone: '',
    companyAddress: '',
    speciality: '',
    employeesNumber: null,
    services: [],
    biography: '',
    webLink: '',
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
  };

  constructor(private router: Router, public toastService: ToastService, private iconLibrary: FaIconLibrary, private userService: UserService) {
    this.iconLibrary.addIcons(faXmark, faPlus);
  }

  private dataURLtoFile(dataUrl: string, filename: string): File {
    const arr = dataUrl.split(',');
    const mime = arr[0].match(/:(.*?);/)![1];
    const bstr = atob(arr[1]);
    let n = bstr.length;
    const u8arr = new Uint8Array(n);

    while (n--) {
      u8arr[n] = bstr.charCodeAt(n);
    }

    return new File([u8arr], filename, { type: mime });
  }

  private submitProfile() {
    // Validate that all required fields are filled
    if (!this.validatePersonalForm()) {
      this.toastService.error('Please complete all required fields.');
      return;
    }

    let upload$;

    // If a new image was selected → upload it first
    if (this.profile.companyLogo && this.profile.companyLogo.startsWith("data:")) {
      const formData = new FormData();
      const file = this.dataURLtoFile(this.profile.companyLogo, "company-logo.png");
      formData.append("file", file);

      upload$ = this.userService.uploadProfilePhoto(formData);
    } else {
      // No new upload → return observable with current logo URL
      upload$ = of({ url: this.profile.companyLogo || '', message: 'No upload' });
    }

    upload$
      .pipe(
        switchMap((uploadRes: any) => {
          // Replace the base64 with the actual server URL
          const logoUrl = uploadRes.url || this.profile.companyLogo;

          // Prepare final data to send (match your updateUserProfile() shape)
          const payload = {
            companyName: this.profile.companyName,
            nationality: this.profile.nationality,
            phone_number: this.profile.countryCode + this.profile.phone,
            company_address: this.profile.companyAddress,
            photo_profil: logoUrl,

            speciality: this.profile.speciality,
            employees_number: this.profile.employeesNumber,
            services: this.profile.services,

            description: this.profile.biography,
            web_link: this.profile.webLink,
            github_link: this.profile.github,
            twitter_link: this.profile.twitter,
            facebook_link: this.profile.facebook
          };

          console.log('Submitting recruiter profile payload:', payload);
          return this.userService.updateUserProfile(payload);
        })
      )
      .subscribe({
        next: (response: any) => {
          console.log('Recruiter profile updated successfully:', response);
          this.toastService.success("Profile updated successfully!");
          this.router.navigate(['/recruiter/dashboard']);
        },
        error: (error: any) => {
          console.error('Error updating recruiter profile:', error);
          this.toastService.error("Failed to update profile. Please try again.");
        }
      });
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
    if (this.currentStep < 3) {
      this.currentStep++;
      this.updateProgress();
    }
  }

  validatePersonalForm(): boolean {
    return !!(
      this.profile.companyLogo &&
      this.profile.companyName &&
      this.profile.nationality &&
      this.profile.phone &&
      this.profile.companyAddress
    );
  }

  isProfileComplete(): boolean {
    return this.validatePersonalForm();
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
        this.profile.companyLogo = this.imagePreview;
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

  addService() {
    if (this.newService.trim()) {
      this.profile.services.push(this.newService.trim());
      this.newService = '';
    } else {
      this.toastService.error('Please enter a service.');
    }
  }

  removeService(index: number) {
    this.profile.services.splice(index, 1);
  }

  saveAndComplete() {
    this.currentStep = 4;
    this.updateProgress();
    // Submit profile after showing the completion screen
    setTimeout(() => {
      this.submitProfile();
    }, 500);
  }

  goToDashboard() {
    if (!this.isProfileComplete()) {
      this.toastService.error('Please complete all required fields to proceed to dashboard.');
      return;
    }
    this.router.navigate(['/recruiter/dashboard']);
    this.toastService.success('Redirecting to dashboard...');
  }
}