import { Component , HostListener, OnInit } from '@angular/core';
import { Router } from '@angular/router'; 

@Component({
  selector: 'app-recruiter-dashboard',
  standalone: false,
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss']
})
export class RecruiterDashboard {
  activeSection = 'publish-job';
  leftSidebarOpen = false;
  rightSidebarOpen = false;
  window = window;


  navItems = [
  { id: 'publish-job', label: 'Publish Job' },
  { id: 'interviews', label: 'Interviews' },

];

  secondaryNavItems = [
    { id: 'edit-profile', label: 'Settings' },
    { id: 'logout', label: 'Logout' }
  ];

  constructor(private router: Router) {}
  ngOnInit() {
    // Sur desktop, les sidebars sont ouvertes par défaut
    if (window.innerWidth >= 1024) {
      this.leftSidebarOpen = true;
      this.rightSidebarOpen = true;
    }
  }

  onSectionChange(section: string) {
    this.activeSection = section;
    if (window.innerWidth < 1024) {
      this.leftSidebarOpen = false;
    }
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
  toggleLeftSidebar() {
    this.leftSidebarOpen = !this.leftSidebarOpen;
    // Fermer la droite si on ouvre la gauche sur mobile
    if (this.leftSidebarOpen && window.innerWidth < 1024) {
      this.rightSidebarOpen = false;
    }
  }

  toggleRightSidebar() {
    this.rightSidebarOpen = !this.rightSidebarOpen;
    // Fermer la gauche si on ouvre la droite sur mobile
    if (this.rightSidebarOpen && window.innerWidth < 1024) {
      this.leftSidebarOpen = false;
    }
  }
  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    // Réinitialiser l'état des sidebars sur desktop
    if (event.target.innerWidth >= 1024) {
      this.leftSidebarOpen = true;
      this.rightSidebarOpen = true;
    } else {
      this.leftSidebarOpen = false;
      this.rightSidebarOpen = false;
    }
  }
  
}