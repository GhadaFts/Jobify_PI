import { Component, HostListener, OnInit } from '@angular/core';
import { Router } from '@angular/router'; 
import { AuthService, User } from '../../services/auth.service';

@Component({
  selector: 'app-recruiter-dashboard',
  standalone: false,
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss']
})
export class RecruiterDashboard implements OnInit {
  activeSection = 'publish-job';
  leftSidebarOpen = false;
  rightSidebarOpen = false;
  window = window;
  currentUser: User | null = null;
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
    private authService: AuthService
  ) {}

  ngOnInit() {
    // Sur desktop, les sidebars sont ouvertes par défaut
    if (window.innerWidth >= 1024) {
      this.leftSidebarOpen = true;
      this.rightSidebarOpen = true;
    }

    // Subscribe to current user
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });

    // If user is not loaded yet, fetch profile
    if (!this.currentUser) {
      this.authService.getUserProfile().subscribe({
        next: (user) => {
          this.currentUser = user;
        },
        error: (error) => {
          console.error('Failed to load user profile:', error);
        }
      });
    }
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