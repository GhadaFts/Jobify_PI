import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { AnalyticsService } from '../../services/analytics.service';

@Component({
  selector: 'app-alerts-panel',
  templateUrl: './alerts-panel.html',
  styleUrls: ['./alerts-panel.scss'],
  standalone: false
})
export class AlertsPanelComponent implements OnInit {
  alertsData$: Observable<any>;

  constructor(private analyticsService: AnalyticsService) {
    this.alertsData$ = new Observable();
  }

  ngOnInit(): void {
    this.alertsData$ = this.analyticsService.getAllAlerts();
  }

  getIcon(alert: boolean, change: number): string {
    if (alert) return '⚠️';
    return change > 0 ? '📈' : '📉';
  }

  getChangeColor(change: number): string {
    return change > 0 ? 'positive' : 'negative';
  }
}
