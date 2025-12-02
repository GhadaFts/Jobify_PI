import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AnalyticsService } from '../../services/analytics.service';

@Component({
  selector: 'app-geo-heatmap-chart',
  templateUrl: './geo-heatmap-chart.html',
  styleUrls: ['./geo-heatmap-chart.scss'],
  standalone: false
})
export class GeoHeatmapChartComponent implements OnInit {
  chartData$: Observable<any>;

  constructor(private analyticsService: AnalyticsService) {
    this.chartData$ = new Observable();
  }

  ngOnInit(): void {
    this.chartData$ = this.analyticsService.getGeographicDistribution().pipe(
      map(response => response.data || [])
    );
  }

  getMaxJobs(data: any[]): number {
    if (!data || data.length === 0) return 1;
    return Math.max(...data.map(d => d.jobCount || 0));
  }

  getMaxApplications(data: any[]): number {
    if (!data || data.length === 0) return 1;
    return Math.max(...data.map(d => d.totalApplications || 0));
  }

  getIntensity(applications: number, max: number): string {
    if (max === 0) return 'low';
    const intensity = applications / max;
    if (intensity > 0.6) return 'high';
    if (intensity > 0.3) return 'medium';
    return 'low';
  }
}
