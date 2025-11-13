import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { MockAnalyticsService } from '../../services/mock-analytics.service';

@Component({
  selector: 'app-active-vs-expired-chart',
  templateUrl: './active-vs-expired-chart.html',
  styleUrls: ['./active-vs-expired-chart.scss'],
  standalone: false
})
export class ActiveVsExpiredChartComponent implements OnInit {
  chartData$: Observable<any>;

  constructor(private analyticsService: MockAnalyticsService) {
    this.chartData$ = new Observable();
  }

  ngOnInit(): void {
    this.chartData$ = this.analyticsService.getActiveVsExpiredJobs();
  }

  getLinePoints(data: any[], key: string): string {
    const maxValue = Math.max(...data.map(d => d[key === 'active' ? 'active' : 'expired']));
    return data
      .map((item, index) => {
        const x = (index / (data.length - 1)) * 800;
        const y = 180 - (item[key] / maxValue) * 150;
        return `${x},${y}`;
      })
      .join(' ');
  }

  getChartPoints(data: any[], key: string): any[] {
    const maxValue = Math.max(...data.map(d => d[key === 'active' ? 'active' : 'expired']));
    return data.map((item, index) => ({
      x: (index / (data.length - 1)) * 800,
      y: 180 - (item[key] / maxValue) * 150
    }));
  }
}
