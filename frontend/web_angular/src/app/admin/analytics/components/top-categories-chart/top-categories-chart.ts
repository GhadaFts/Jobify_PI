import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { MockAnalyticsService } from '../../services/mock-analytics.service';

@Component({
  selector: 'app-top-categories-chart',
  templateUrl: './top-categories-chart.html',
  styleUrls: ['./top-categories-chart.scss'],
  standalone: false
})
export class TopCategoriesChartComponent implements OnInit {
  chartData$: Observable<any>;

  constructor(private analyticsService: MockAnalyticsService) {
    this.chartData$ = new Observable();
  }

  ngOnInit(): void {
    this.chartData$ = this.analyticsService.getTopCategories();
  }

  getMaxJobs(data: any[]): number {
    return Math.max(...data.map(d => d.jobs));
  }
}
