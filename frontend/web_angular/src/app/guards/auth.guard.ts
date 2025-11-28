import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    // Vérifier si l'utilisateur est authentifié
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
      return false;
    }

    // Vérifier le rôle si spécifié
    const expectedRole = route.data['role'];
    if (expectedRole) {
      const currentUser = this.authService.getCurrentUser();
      
      if (!currentUser) {
        this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
        return false;
      }

      // Normaliser les rôles pour la comparaison
      const userRole = currentUser.role?.toLowerCase().replace('_', '');
      const requiredRole = expectedRole.toLowerCase().replace('_', '');

      if (userRole === requiredRole || currentUser.role === expectedRole) {
        return true;
      } else {
        // Rediriger vers la page appropriée selon le rôle
        this.redirectToRoleDashboard(currentUser.role);
        return false;
      }
    }

    return true;
  }

  private redirectToRoleDashboard(role: string): void {
    switch (role?.toLowerCase()) {
      case 'recruiter':
        this.router.navigate(['/recruiter/dashboard/publish-job']);
        break;
      case 'job_seeker':
      case 'jobseeker':
        this.router.navigate(['/job-seeker/dashboard/find-job']);
        break;
      case 'admin':
        this.router.navigate(['/admin']);
        break;
      default:
        this.router.navigate(['/unauthorized']);
        break;
    }
  }
}