import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AnalyticsService } from '../../services/analytics.service';

@Component({
  selector: 'app-top-companies-chart',
  templateUrl: './top-companies-chart.html',
  styleUrls: ['./top-companies-chart.scss'],
  standalone: false
})
export class TopCompaniesChartComponent implements OnInit {
  chartData$: Observable<any>;

  constructor(private analyticsService: AnalyticsService) {
    this.chartData$ = new Observable();
  }

  ngOnInit(): void {
    this.chartData$ = this.analyticsService.getTopCompanies().pipe(
      map(response => response.data || [])
    );
  }

  getMaxApps(data: any): number {
    if (!data || !Array.isArray(data) || data.length === 0) return 1;
    return Math.max(...data.map((d: any) => d.totalApplications || 0));
  }

  getAvgAppsPerJob(company: any): string {
    if (!company) return '0.0';
    return company.avgAppsPerJob?.toFixed(1) || '0.0';
  }

  getProgressWidth(totalApplications: number, maxApps: number): string {
    return ((totalApplications || 0) / maxApps * 100) + '%';
  }
}
