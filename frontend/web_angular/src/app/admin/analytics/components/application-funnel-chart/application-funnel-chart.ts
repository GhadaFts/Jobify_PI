import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { MockAnalyticsService } from '../../services/mock-analytics.service';

@Component({
  selector: 'app-application-funnel-chart',
  templateUrl: './application-funnel-chart.html',
  styleUrls: ['./application-funnel-chart.scss'],
  standalone: false
})
export class ApplicationFunnelChartComponent implements OnInit {
  funnelData$: Observable<any>;

  constructor(private analyticsService: MockAnalyticsService) {
    this.funnelData$ = new Observable();
  }

  ngOnInit(): void {
    this.funnelData$ = this.analyticsService.getApplicationFunnel();
  }

  calculateConversionRate(current: number, previous: number): number {
    return parseFloat(((current / previous) * 100).toFixed(1));
  }
}
