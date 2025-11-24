import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { ToastService } from '../../../services/toast.service';
import { JobSeeker, User } from '../../../types'; // Import des nouveaux types
import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faCamera, faXmark } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-edit-profile',
  standalone: false,
  templateUrl: './edit-profile.html',
  styleUrls: ['./edit-profile.scss']
})
export class EditProfile implements OnInit {
  faCamera = faCamera;
  faXmark = faXmark;
  
  @Input() profile: JobSeeker = {
    id: 0,
    email: '',
    password: '',
    fullName: '',
    role: 'jobseeker',
    photo_profil: '',
    twitter_link: '',
    web_link: '',
    github_link: '',
    facebook_link: '',
    description: '',
    phone_number: '',
    nationality: '',
    skills: [],
    experience: [],
    education: [],
    title: '',
    date_of_birth: '',
    gender: ''
  };

  @Output() save = new EventEmitter<JobSeeker>();
  editedProfile: JobSeeker;
  newSkill: string = '';

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

  addSkill() {
    if (this.newSkill.trim() && !this.editedProfile.skills.includes(this.newSkill.trim())) {
      this.editedProfile = {
        ...this.editedProfile,
        skills: [...this.editedProfile.skills, this.newSkill.trim()]
      };
      this.newSkill = '';
      this.toastService.success('Skill added successfully!');
    }
  }

  removeSkill(skillToRemove: string) {
    this.editedProfile = {
      ...this.editedProfile,
      skills: this.editedProfile.skills.filter(skill => skill !== skillToRemove)
    };
    this.toastService.success('Skill removed successfully!');
  }

  addExperience() {
    this.editedProfile = {
      ...this.editedProfile,
      experience: [...this.editedProfile.experience, { 
        position: '', 
        company: '', 
        startDate: '', 
        endDate: '', 
        description: '' 
      }]
    };
    this.toastService.success('Experience added successfully!');
  }

  updateExperience(index: number, field: keyof JobSeeker['experience'][0], value: string) {
    this.editedProfile = {
      ...this.editedProfile,
      experience: this.editedProfile.experience.map((exp, i) =>
        i === index ? { ...exp, [field]: value } : exp
      )
    };
  }

  removeExperience(index: number) {
    this.editedProfile = {
      ...this.editedProfile,
      experience: this.editedProfile.experience.filter((_, i) => i !== index)
    };
    this.toastService.success('Experience removed successfully!');
  }

  addEducation() {
    this.editedProfile = {
      ...this.editedProfile,
      education: [...this.editedProfile.education, { 
        degree: '', 
        field: '', 
        school: '', 
        graduationDate: '' 
      }]
    };
    this.toastService.success('Education added successfully!');
  }

  updateEducation(index: number, field: keyof JobSeeker['education'][0], value: string) {
    this.editedProfile = {
      ...this.editedProfile,
      education: this.editedProfile.education.map((edu, i) =>
        i === index ? { ...edu, [field]: value } : edu
      )
    };
  }

  removeEducation(index: number) {
    this.editedProfile = {
      ...this.editedProfile,
      education: this.editedProfile.education.filter((_, i) => i !== index)
    };
    this.toastService.success('Education removed successfully!');
  }

  // Optional: For testing toasts in the template
  testToast() {
    this.toastService.success('Test toast!');
  }
  
  testToastErreur() {
    this.toastService.error('Test toast erreur!');
  }
}