import { Component, HostListener, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router'; 
import { Subject, takeUntil } from 'rxjs';
import { AuthService, User } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { Recruiter } from '../../types';

@Component({
  selector: 'app-recruiter-dashboard',
  standalone: false,
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss']
})
export class RecruiterDashboard implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  
  activeSection = 'publish-job';
  leftSidebarOpen = false;
  rightSidebarOpen = false;
  window = window;
  currentUser: User | null = null;
  profileImage: string = ''; // Start with empty, not default
  logoutInProgress = false;

  navItems = [
    { id: 'publish-job', label: 'Publish Job' },
    { id: 'interviews', label: 'Interviews' },
  ];

  secondaryNavItems = [
    { id: 'edit-profile', label: 'Settings' },
    { id: 'logout', label: 'Logout' }
  ];

  constructor(
    private router: Router, 
    private authService: AuthService,
    private userService: UserService
  ) {
    console.log('🏗️ RecruiterDashboard constructor called');
  }

  ngOnInit() {
    console.log('🚀 RecruiterDashboard ngOnInit called');
    
    // On desktop, sidebars are open by default
    if (window.innerWidth >= 1024) {
      this.leftSidebarOpen = true;
      this.rightSidebarOpen = true;
    }

    // Subscribe to current user
    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        console.log('👤 Current user updated:', user);
        this.currentUser = user;
      });

    // Load user profile with image IMMEDIATELY
    this.loadUserProfile();

    // If user is not loaded yet, fetch profile
    if (!this.currentUser) {
      console.log('⚠️ No current user, fetching profile...');
      this.authService.getUserProfile()
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (user) => {
            console.log('✅ User profile fetched:', user);
            this.currentUser = user;
          },
          error: (error) => {
            console.error('❌ Failed to load user profile:', error);
          }
        });
    }

    // Subscribe to profile changes (when user updates their profile)
    this.userService.currentProfile$
      .pipe(takeUntil(this.destroy$))
      .subscribe(profile => {
        console.log('📢 Profile change detected:', profile);
        if (profile && profile.role === 'recruiter') {
          const recruiterProfile = profile as Recruiter;
          const newImageUrl = this.userService.getImageUrl(recruiterProfile.photo_profil);
          console.log('🖼️ Updating profile image to:', newImageUrl);
          this.profileImage = newImageUrl;
        }
      });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Load full user profile to get the profile image
   */
  private loadUserProfile() {
    console.log('🔍 loadUserProfile() called');
    
    // First check localStorage
    const cachedProfile = localStorage.getItem('userProfile');
    if (cachedProfile) {
      try {
        const profile = JSON.parse(cachedProfile);
        console.log('📦 Found cached profile:', profile);
        
        if (profile.role === 'recruiter') {
          const imageUrl = this.userService.getImageUrl(profile.photo_profil);
          console.log('🖼️ Setting image from cache:', imageUrl);
          this.profileImage = imageUrl;
        }
      } catch (e) {
        console.error('❌ Failed to parse cached profile:', e);
      }
    }

    // Then fetch from backend
    this.userService.getUserProfile()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (profile: any) => {
          console.log('📥 Profile from backend:', profile);
          console.log('📥 Profile role:', profile.role);
          console.log('📥 Profile photo_profil:', profile.photo_profil);
          
          const normalizedRole = profile.role?.toLowerCase().replace('_', '');
          console.log('📥 Normalized role:', normalizedRole);
          
          if (normalizedRole === 'recruiter') {
            const imageUrl = this.userService.getImageUrl(profile.photo_profil);
            console.log('🖼️ Image URL from service:', imageUrl);
            
            this.profileImage = imageUrl;
            console.log('✅ Profile image SET to:', this.profileImage);
          } else {
            console.warn('⚠️ User is not a recruiter, role is:', profile.role);
          }
        },
        error: (error) => {
          console.error('❌ Failed to load profile from backend:', error);
          this.profileImage = '';
        }
      });
  }
  getProfileImageUrl(): string {
    const profile = this.userService.getCurrentProfile();
    //GET THE IMAGE USING THE USER SERVICE
    const image = this.userService.getImageUrl(profile?.photo_profil);
    return image;
  }

  onSectionChange(section: string) {
    this.activeSection = section;
    
    if (window.innerWidth < 1024) {
      this.leftSidebarOpen = false;
    }

    // Handle logout separately
    if (section === 'logout') {
      this.doLogout();
      return;
    }

    // Navigate to the corresponding route
    switch (section) {
      case 'publish-job':
        this.router.navigate(['/recruiter/dashboard/publish-job']);
        break;
      case 'interviews':
        this.router.navigate(['/recruiter/dashboard/interviews']);
        break;
      case 'edit-profile':
        this.router.navigate(['/recruiter/dashboard/edit-profile']);
        break;
    }
  }

  private doLogout() {
    if (this.logoutInProgress) return;
    this.logoutInProgress = true;

    this.authService.logout().subscribe({
      next: () => {
        this.logoutInProgress = false;
        this.router.navigate(['/login'], { replaceUrl: true });
      },
      error: (err) => {
        console.error('Logout error:', err);
        this.logoutInProgress = false;
        this.router.navigate(['/login'], { replaceUrl: true });
      }
    });
  }

  toggleLeftSidebar() {
    this.leftSidebarOpen = !this.leftSidebarOpen;
    if (this.leftSidebarOpen && window.innerWidth < 1024) {
      this.rightSidebarOpen = false;
    }
  }

  toggleRightSidebar() {
    this.rightSidebarOpen = !this.rightSidebarOpen;
    if (this.rightSidebarOpen && window.innerWidth < 1024) {
      this.leftSidebarOpen = false;
    }
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    if (event.target.innerWidth >= 1024) {
      this.leftSidebarOpen = true;
      this.rightSidebarOpen = true;
    } else {
      this.leftSidebarOpen = false;
      this.rightSidebarOpen = false;
    }
  }
}