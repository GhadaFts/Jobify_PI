import { Component } from '@angular/core';
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

  
}