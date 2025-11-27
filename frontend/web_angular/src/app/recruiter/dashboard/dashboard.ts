import { Component , HostListener, OnInit } from '@angular/core';
import { Router } from '@angular/router'; 
import { AuthService } from '../../services/auth.service';

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

   logoutInProgress = false; // optional flag to disable UI during logout
  constructor(private router: Router, private authService: AuthService) {}
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
         this.doLogout();
        break;
    }
     
  }
  private doLogout() {
    if (this.logoutInProgress) return;
    this.logoutInProgress = true;

    // Call logout() which returns an Observable
    this.authService.logout().subscribe({
      next: () => {
        this.logoutInProgress = false;
        // navigate to login page (replaceUrl avoids going back to dashboard with back button)
        this.router.navigate(['/login'], { replaceUrl: true });
      },
      error: (err) => {
        // still clear local state, navigate to login, and optionally show error toast
        console.error('Logout error:', err);
        this.logoutInProgress = false;
        this.router.navigate(['/login'], { replaceUrl: true });
      }
    });
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