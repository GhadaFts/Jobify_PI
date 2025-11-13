import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { MockAnalyticsService } from '../../services/mock-analytics.service';

@Component({
  selector: 'app-geo-heatmap-chart',
  templateUrl: './geo-heatmap-chart.html',
  styleUrls: ['./geo-heatmap-chart.scss'],
  standalone: false
})
export class GeoHeatmapChartComponent implements OnInit {
  chartData$: Observable<any>;

  constructor(private analyticsService: MockAnalyticsService) {
    this.chartData$ = new Observable();
  }

  ngOnInit(): void {
    this.chartData$ = this.analyticsService.getGeoHeatmap();
  }

  getMaxJobs(data: any[]): number {
    return Math.max(...data.map(d => d.jobs));
  }

  getIntensity(jobs: number, max: number): string {
    const intensity = jobs / max;
    if (intensity > 0.8) return 'high';
    if (intensity > 0.5) return 'medium';
    return 'low';
  }
}
