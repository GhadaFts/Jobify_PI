import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { AnalyticsService } from '../../services/analytics.service';

@Component({
  selector: 'app-application-funnel-chart',
  templateUrl: './application-funnel-chart.html',
  styleUrls: ['./application-funnel-chart.scss'],
  standalone: false
})
export class ApplicationFunnelChartComponent implements OnInit {
  funnelData$: Observable<any>;

  constructor(private analyticsService: AnalyticsService) {
    this.funnelData$ = new Observable();
  }

  ngOnInit(): void {
    this.funnelData$ = this.analyticsService.getApplicationFunnel();
  }

  calculateConversionRate(current: number, previous: number): number {
    if (previous === 0) return 0;
    return parseFloat(((current / previous) * 100).toFixed(1));
  }
}
