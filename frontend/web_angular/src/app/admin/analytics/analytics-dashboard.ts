import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AnalyticsFilters } from './analytics.types';
import { MockAnalyticsService } from './services/mock-analytics.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-analytics-dashboard',
  templateUrl: './analytics-dashboard.html',
  styleUrls: ['./analytics-dashboard.scss'],
  standalone: false
})
export class AnalyticsDashboardComponent implements OnInit {
  activeFilters: AnalyticsFilters | null = null;

  constructor(
    private analyticsService: MockAnalyticsService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.analyticsService.filters$.subscribe(filters => {
      this.activeFilters = filters;
    });
  }

  onFiltersChanged(filters: AnalyticsFilters): void {
    this.activeFilters = filters;
  }

  openSettings(): void {
    this.router.navigate(['/admin/analytics/settings']);
  }

  logout(): void {
    // Use the proper auth service to clear all credentials
    this.authService.logout().subscribe({
      next: () => {
        console.log('✅ Logout successful, redirecting to login');
        // Force a clean state by reloading the app
        window.location.href = '/login';
      },
      error: (err) => {
        console.error('Logout error:', err);
        // Even if logout fails, force reload to login page
        window.location.href = '/login';
      }
    });
  }
}
