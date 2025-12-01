import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';
import { ToastService } from '../../../services/toast.service';
import { UserService } from '../../../services/user.service';
import { AuthService } from '../../../services/auth.service';
import { JobSeeker } from '../../../types';
import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faCamera, faXmark } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-edit-profile',
  standalone: false,
  templateUrl: './edit-profile.html',
  styleUrls: ['./edit-profile.scss']
})
export class EditProfile implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  
  faCamera = faCamera;
  faXmark = faXmark;
  
  profile: JobSeeker = {
    id: 0,
    email: '',
    password: '',
    fullName: '',
    role: 'job_seeker',
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

  editedProfile: JobSeeker;
  newSkill: string = '';
  isLoading: boolean = true;
  isSaving: boolean = false;
  isUploadingPhoto: boolean = false;

  constructor(
    public toastService: ToastService,
    private iconLibrary: FaIconLibrary,
    public userService: UserService, // Made public so we can use it in template if needed
    private authService: AuthService
  ) {
    this.editedProfile = { ...this.profile };
    this.iconLibrary.addIcons(faCamera, faXmark);
  }

  ngOnInit() {
    this.loadProfile();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Load profile from localStorage or backend
   */
  private loadProfile() {
    this.isLoading = true;

    // First try to load from localStorage  
    const userProfileStr = localStorage.getItem('userProfile');
    if (userProfileStr) {
      try {
        const cachedProfile = JSON.parse(userProfileStr);
        console.log('📦 Found cached profile:', cachedProfile);
        
        const normalizedRole = cachedProfile.role?.toLowerCase().replace('_', '');
        
        if (normalizedRole === 'jobseeker') {
          // Use userService to get full image URL
          this.profile = {
            ...cachedProfile,
            skills: cachedProfile.skills || [],
            experience: cachedProfile.experience || [],
            education: cachedProfile.education || [],
            photo_profil: this.userService.getImageUrl(cachedProfile.photo_profil)
          } as JobSeeker;
          
          this.editedProfile = { ...this.profile };
          this.isLoading = false;
          console.log('✅ Loaded profile from cache:', this.profile);
        }
      } catch (e) {
        console.error('❌ Failed to parse cached profile:', e);
      }
    }

    // Then fetch fresh data from backend
    this.userService.getUserProfile()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (profile: any) => {
          console.log('🔥 Profile from backend:', profile);
          
          const normalizedRole = profile.role?.toLowerCase().replace('_', '');
          
          if (normalizedRole === 'jobseeker') {
            this.profile = {
              ...profile,
              skills: profile.skills || [],
              experience: profile.experience || [],
              education: profile.education || []
            } as JobSeeker;
            
            this.editedProfile = { ...this.profile };
            this.isLoading = false;
            console.log('✅ Loaded profile from backend:', this.profile);
          } else {
            console.error('❌ User role is:', profile.role);
            this.toastService.error('This page is only for job seekers');
            this.isLoading = false;
          }
        },
        error: (error) => {
          console.error('❌ Failed to load profile:', error);
          this.isLoading = false;
          
          if (this.profile.id !== 0) {
            this.toastService.error('Using cached profile data');
          } else {
            this.toastService.error('Failed to load profile. Please try again.');
          }
        }
      });
  }

  getInitials(): string {
    if (!this.editedProfile.fullName) return 'N/A';
    const words = this.editedProfile.fullName.split(' ').filter(word => word.length > 0);
    return words.length > 0 ? words.map(word => word[0]).join('') : 'N/A';
  }

  onImageError(event: any): void {
    console.error('Image failed to load:', this.editedProfile.photo_profil);
    event.target.style.display = 'none';
    this.editedProfile.photo_profil = '';
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      const validTypes = ['image/jpeg', 'image/png', 'image/gif'];
      const maxSize = 5 * 1024 * 1024; // 5MB

      if (!validTypes.includes(file.type)) {
        this.toastService.error('Invalid file type. Please upload JPG, PNG, or GIF.');
        return;
      }

      if (file.size > maxSize) {
        this.toastService.error('File size exceeds 5MB limit.');
        return;
      }

      // Show preview immediately
      const reader = new FileReader();
      reader.onload = () => {
        this.editedProfile = {
          ...this.editedProfile,
          photo_profil: reader.result as string
        };
      };
      reader.readAsDataURL(file);

      // Upload to backend via service
      this.uploadPhoto(file);
    }
  }

  /**
   * Upload photo using service
   */
  private uploadPhoto(file: File) {
    this.isUploadingPhoto = true;
    const formData = new FormData();
    formData.append('file', file);

    this.userService.uploadProfilePhoto(formData)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          // Service already returns full URL
          this.editedProfile.photo_profil = response.url;
          this.profile.photo_profil = response.url;
          this.isUploadingPhoto = false;
          this.toastService.success('Photo uploaded successfully!');
          console.log('✅ Photo uploaded, URL:', response.url);
        },
        error: (error) => {
          console.error('❌ Failed to upload photo:', error);
          this.isUploadingPhoto = false;
          this.toastService.error('Failed to upload photo');
          // Revert to old photo
          this.editedProfile.photo_profil = this.profile.photo_profil;
        }
      });
  }

  handleSave() {
    if (this.isSaving) return;

    // Validate required fields
    if (!this.editedProfile.fullName?.trim()) {
      this.toastService.error('Full name is required');
      return;
    }

    if (!this.editedProfile.email?.trim()) {
      this.toastService.error('Email is required');
      return;
    }

    this.isSaving = true;

    // Prepare data - remove MongoDB internal fields, sensitive data, and photo
    const { 
      password, 
      id, 
      _id, 
      __v, 
      createdAt, 
      updatedAt,
      deleted,
      keycloakId,
      role,
      photo_profil, // Photo is handled separately via upload endpoint
      ...profileData 
    } = this.editedProfile as any;

    // Ensure arrays are properly formatted
    const cleanedData = {
      ...profileData,
      skills: profileData.skills || [],
      experience: (profileData.experience || []).map((exp: any) => ({
        position: exp.position || '',
        company: exp.company || '',
        startDate: exp.startDate || '',
        endDate: exp.endDate || '',
        description: exp.description || ''
      })),
      education: (profileData.education || []).map((edu: any) => ({
        degree: edu.degree || '',
        field: edu.field || '',
        school: edu.school || '',
        graduationDate: edu.graduationDate || ''
      }))
    };

    console.log('💾 Saving profile:', cleanedData);

    this.userService.updateUserProfile(cleanedData)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (updatedProfile) => {
          // Preserve the photo_profil from editedProfile (it's already uploaded)
          this.profile = {
            ...updatedProfile,
            photo_profil: this.editedProfile.photo_profil
          } as JobSeeker;
          this.editedProfile = { ...this.profile };
          this.isSaving = false;
          this.toastService.success('Profile updated successfully!');
          console.log('✅ Profile saved:', updatedProfile);
        },
        error: (error) => {
          console.error('❌ Failed to save profile:', error);
          this.isSaving = false;
          
          if (error.status === 400) {
            this.toastService.error('Invalid profile data. Please check your input.');
          } else if (error.status === 401) {
            this.toastService.error('Session expired. Please login again.');
          } else {
            this.toastService.error('Failed to update profile. Please try again.');
          }
        }
      });
  }

  addSkill() {
    if (this.newSkill.trim() && !this.editedProfile.skills.includes(this.newSkill.trim())) {
      this.editedProfile = {
        ...this.editedProfile,
        skills: [...this.editedProfile.skills, this.newSkill.trim()]
      };
      this.newSkill = '';
      this.toastService.success('Skill added successfully!');
    } else if (this.editedProfile.skills.includes(this.newSkill.trim())) {
      this.toastService.error('Skill already exists');
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

  removeEducation(index: number) {
    this.editedProfile = {
      ...this.editedProfile,
      education: this.editedProfile.education.filter((_, i) => i !== index)
    };
    this.toastService.success('Education removed successfully!');
  }

  /**
   * Discard changes and reload from saved profile
   */
  discardChanges() {
    this.editedProfile = { ...this.profile };
    this.toastService.success('Changes discarded');
  }
}