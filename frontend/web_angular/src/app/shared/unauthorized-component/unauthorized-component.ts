// unauthorized.component.ts
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-unauthorized',
  standalone: false,
  template: `
    <div class="unauthorized-container">
      <h2>Accès Refusé</h2>
      <p>Vous n'avez pas les permissions nécessaires pour accéder à cette page.</p>
      <p>Votre rôle actuel: <strong>{{ currentRole }}</strong></p>
      <button (click)="goHome()">Retour à l'accueil</button>
      <button (click)="logout()">Se déconnecter</button>
    </div>
  `,
  styles: [`
    .unauthorized-container {
      text-align: center;
      padding: 2rem;
    }
  `]
})
export class UnauthorizedComponent {
  currentRole: string;

  constructor(
    private router: Router,
    private authService: AuthService
  ) {
    const user = this.authService.getCurrentUser();
    this.currentRole = user?.role || 'Non connecté';
  }

  goHome() {
    this.router.navigate(['/']);
  }

  logout() {
    this.authService.logout().subscribe();
  }
}