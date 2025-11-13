import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AnalyticsFilters } from './analytics.types';
import { MockAnalyticsService } from './services/mock-analytics.service';

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
    private router: Router
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
    // Clear any auth tokens or user data
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
    // Redirect to main page
    this.router.navigate(['/']);
  }
}
