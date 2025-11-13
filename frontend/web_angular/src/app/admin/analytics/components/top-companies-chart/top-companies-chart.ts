import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { MockAnalyticsService } from '../../services/mock-analytics.service';

@Component({
  selector: 'app-top-companies-chart',
  templateUrl: './top-companies-chart.html',
  styleUrls: ['./top-companies-chart.scss'],
  standalone: false
})
export class TopCompaniesChartComponent implements OnInit {
  chartData$: Observable<any>;

  constructor(private analyticsService: MockAnalyticsService) {
    this.chartData$ = new Observable();
  }

  ngOnInit(): void {
    this.chartData$ = this.analyticsService.getTopCompanies();
  }

  getMaxApps(data: any): number {
    if (!data || !Array.isArray(data) || data.length === 0) return 1;
    return Math.max(...data.map((d: any) => d.applicationCount || 0));
  }

  getAvgAppsPerJob(company: any): string {
    if (!company || company.jobCount === 0) return '0.0';
    return (company.applicationCount / company.jobCount).toFixed(1);
  }

  getProgressWidth(applicationCount: number, maxApps: number): string {
    return ((applicationCount || 0) / maxApps * 100) + '%';
  }
}
