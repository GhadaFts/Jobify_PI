import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { MockAnalyticsService } from '../../services/mock-analytics.service';

@Component({
  selector: 'app-apps-per-job-chart',
  templateUrl: './apps-per-job-chart.html',
  styleUrls: ['./apps-per-job-chart.scss'],
  standalone: false
})
export class AppsPerJobChartComponent implements OnInit {
  chartData$: Observable<any>;

  constructor(private analyticsService: MockAnalyticsService) {
    this.chartData$ = new Observable();
  }

  ngOnInit(): void {
    this.chartData$ = this.analyticsService.getAppsPerJob();
  }

  getMaxCount(data: any[]): number {
    return Math.max(...data.map(d => d.count));
  }

  getTotalApps(data: any[]): number {
    return data.reduce((sum: number, item: any) => sum + item.count, 0);
  }

  getAverageApps(data: any[]): number {
    const total = this.getTotalApps(data);
    return total / data.length;
  }
}
