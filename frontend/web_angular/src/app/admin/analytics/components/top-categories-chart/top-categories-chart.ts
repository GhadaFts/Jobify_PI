import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AnalyticsService } from '../../services/analytics.service';

@Component({
  selector: 'app-top-categories-chart',
  templateUrl: './top-categories-chart.html',
  styleUrls: ['./top-categories-chart.scss'],
  standalone: false
})
export class TopCategoriesChartComponent implements OnInit {
  chartData$: Observable<any>;

  constructor(private analyticsService: AnalyticsService) {
    this.chartData$ = new Observable();
  }

  ngOnInit(): void {
    this.chartData$ = this.analyticsService.getTopCategories().pipe(
      map(response => response.data || [])
    );
  }

  getMaxJobs(data: any[]): number {
    return Math.max(...data.map(d => d.jobCount || 0));
  }
}
