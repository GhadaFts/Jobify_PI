import { Component, HostListener, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router'; 
import { Subject, takeUntil } from 'rxjs';
import { NavigationItem } from '../../shared/sidebar/sidebar';
import { AuthService, User } from '../../services/auth.service';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-dashboard',
  standalone: false,
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss']
})
export class Dashboard implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  
  activeSection = 'find-job';
  leftOpen = false;
  rightOpen = false;
  currentUser: User | null = null;
  logoutInProgress = false;

  navItems: NavigationItem[] = [
    { id: 'find-job', label: 'Find Job' },
    { id: 'cv-correction', label: 'CV Correction' },
    { id: 'job-analyse', label: 'Job Analyse' },
    { id: 'interview-preparation', label: 'Interview Preparation' },
  ];

  secondaryNavItems: NavigationItem[] = [
    { id: 'edit-profile', label: 'Settings' },
    { id: 'logout', label: 'Logout' }
  ];

  constructor(
    private router: Router,
    private authService: AuthService,
    private userService: UserService
  ) {}

  ngOnInit() {
    // Subscribe to current user
    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.currentUser = user;
      });

    // If user is not loaded yet, fetch profile
    if (!this.currentUser) {
      this.authService.getUserProfile()
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (user) => {
            this.currentUser = user;
          },
          error: (error) => {
            console.error('Failed to load user profile:', error);
          }
        });

    }

    // Also load full profile for the edit-profile page
    this.userService.getUserProfile()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (profile) => {
          console.log('Full profile loaded in dashboard');
        },
        error: (error) => {
          console.error('Failed to load full profile:', error);
        }
      });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
  getProfileImageUrl(): string {
    const profile = this.userService.getCurrentProfile();
    //GET THE IMAGE USING THE USER SERVICE
    const image = this.userService.getImageUrl(profile?.photo_profil);
    return image;
  }

  onSectionChange(section: string) {
    this.activeSection = section;
    
    // Handle logout separately
    if (section === 'logout') {
      this.doLogout();
      return;
    }

    // Navigate to the corresponding route
    switch (section) {
      case 'cv-correction':
        this.router.navigate(['/job-seeker/dashboard/cv-correction']);
        break;
      case 'find-job':
        this.router.navigate(['/job-seeker/dashboard/find-job']);
        break;
      case 'job-analyse':
        this.router.navigate(['/job-seeker/dashboard/job-analyse']);
        break;
      case 'edit-profile':
        this.router.navigate(['/job-seeker/dashboard/edit-profile']);
        break;
      case 'interview-preparation':
        this.router.navigate(['/job-seeker/dashboard/interview-preparation']);
        break;
    }
  }

  private doLogout() {
    if (this.logoutInProgress) return;
    this.logoutInProgress = true;

    // Clear profile data
    this.userService.clearProfile();

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

  toggleLeft() {
    this.leftOpen = !this.leftOpen;
    if (this.leftOpen) { this.rightOpen = false; }
  }

  toggleRight() {
    this.rightOpen = !this.rightOpen;
    if (this.rightOpen) { this.leftOpen = false; }
  }

  closeAll() {
    this.leftOpen = false;
    this.rightOpen = false;
  }
  
  @HostListener('window:resize', ['$event'])
  onResize(event: Event) {
    const w = window.innerWidth || (event && (event.target as Window).innerWidth) || 0;
    if (w > 1023) {
      this.closeAll();
    }
  }
}