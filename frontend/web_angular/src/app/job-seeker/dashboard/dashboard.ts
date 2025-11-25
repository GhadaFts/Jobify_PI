import { Component, HostListener } from '@angular/core';
import { Router } from '@angular/router'; 
import { NavigationItem } from '../../shared/sidebar/sidebar';



@Component({
  selector: 'app-dashboard',
  standalone: false,
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss']
})
export class Dashboard {
  activeSection = 'find-job';
  leftOpen = false;
  rightOpen = false;

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

  constructor(private router: Router) {}

  onSectionChange(section: string) {
    this.activeSection = section; // Update active section
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
      
    }
  }

  toggleLeft() {
    this.leftOpen = !this.leftOpen;
    // close right if opening left (optional UX)
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
      // ensure mobile sidebars are closed when returning to desktop size
      this.closeAll();
    }
  }
  
}