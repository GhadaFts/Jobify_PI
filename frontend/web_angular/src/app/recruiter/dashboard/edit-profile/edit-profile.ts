import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { ToastService } from '../../../services/toast.service';
import { Recruiter } from '../../../types';
import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faCamera, faXmark } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-edit-profile-recruiter',
  standalone: false,
  templateUrl: './edit-profile.html',
  styleUrls: ['./edit-profile.scss']
})
export class EditProfileRecruiter implements OnInit {
  faCamera = faCamera;
  faXmark = faXmark;
  
  @Input() profile: Recruiter = {
    id: 0,
    email: '',
    password: '',
    fullName: '',
    role: 'recruiter',
    photo_profil: '',
    twitter_link: '',
    web_link: '',
    github_link: '',
    facebook_link: '',
    description: '',
    phone_number: '',
    nationality: '',
    companyAddress: '',
    domaine: '',
    employees_number: 0,
    service: []
  };

  @Output() save = new EventEmitter<Recruiter>();
  editedProfile: Recruiter;
  newService: string = '';

  constructor(public toastService: ToastService, private iconLibrary: FaIconLibrary) {
    this.editedProfile = { ...this.profile };
    this.iconLibrary.addIcons(faCamera, faXmark);
  }

  ngOnInit() {
    this.editedProfile = { ...this.profile };
  }

  getInitials(): string {
    if (!this.editedProfile.fullName) return 'N/A';
    const words = this.editedProfile.fullName.split(' ').filter(word => word.length > 0);
    return words.length > 0 ? words.map(word => word[0]).join('') : 'N/A';
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      const validTypes = ['image/jpeg', 'image/png', 'image/gif'];
      const maxSize = 2 * 1024 * 1024; // 2MB in bytes

      if (!validTypes.includes(file.type)) {
        this.toastService.error('Invalid file type. Please upload JPG, PNG, or GIF.');
        return;
      }

      if (file.size > maxSize) {
        this.toastService.error('File size exceeds 2MB limit.');
        return;
      }

      const reader = new FileReader();
      reader.onload = () => {
        this.editedProfile = {
          ...this.editedProfile,
          photo_profil: reader.result as string
        };
        this.toastService.success('Profile photo uploaded successfully!');
      };
      reader.onerror = () => {
        this.toastService.error('Error reading file.');
      };
      reader.readAsDataURL(file);
    }
  }

  handleSave() {
    this.save.emit(this.editedProfile);
    this.toastService.success('Profile updated successfully!');
  }

  addService() {
    if (this.newService.trim() && !this.editedProfile.service.includes(this.newService.trim())) {
      this.editedProfile = {
        ...this.editedProfile,
        service: [...this.editedProfile.service, this.newService.trim()]
      };
      this.newService = '';
      this.toastService.success('Service added successfully!');
    }
  }

  removeService(serviceToRemove: string) {
    this.editedProfile = {
      ...this.editedProfile,
      service: this.editedProfile.service.filter(service => service !== serviceToRemove)
    };
    this.toastService.success('Service removed successfully!');
  }

  // Optional: For testing toasts in the template
  testToast() {
    this.toastService.success('Test toast!');
  }
  
  testToastErreur() {
    this.toastService.error('Test toast erreur!');
  }
}