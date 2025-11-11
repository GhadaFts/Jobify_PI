import { Component } from '@angular/core';
import { Router } from '@angular/router'; 

@Component({
  selector: 'app-recruiter-dashboard',
  standalone: false,
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss']
})
export class RecruiterDashboard {
  activeSection = 'publish-job';

  navItems = [
  { id: 'publish-job', label: 'Publish Job' },
  { id: 'interviews', label: 'Interviews' },

];

  secondaryNavItems = [
    { id: 'edit-profile', label: 'Settings' },
    { id: 'logout', label: 'Logout' }
  ];

  constructor(private router: Router) {}

  onSectionChange(section: string) {
    this.activeSection = section;
    // Navigate to the corresponding route
    switch (section) {
      case 'publish-job':
        this.router.navigate(['/recruiter/dashboard/publish-job']);
        break;
      case 'edit-profile':
        this.router.navigate(['/recruiter/dashboard/edit-profile']);
        break;
      case 'logout':
        // Handle logout logic
        this.router.navigate(['/login']);
        break;
    }
  }
}