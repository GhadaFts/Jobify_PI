import { Component } from '@angular/core';
import { Router } from '@angular/router'; 


@Component({
  selector: 'app-dashboard',
  standalone: false,
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss']
})
export class Dashboard {
  activeSection = 'find-job';

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
    }
  }

  
}