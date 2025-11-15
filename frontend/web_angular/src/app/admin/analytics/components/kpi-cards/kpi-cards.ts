import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { KpiData } from '../../analytics.types';
import { MockAnalyticsService } from '../../services/mock-analytics.service';

@Component({
  selector: 'app-kpi-cards',
  templateUrl: './kpi-cards.html',
  styleUrls: ['./kpi-cards.scss'],
  standalone: false
})
export class KpiCardsComponent implements OnInit {
  kpiData$: Observable<any>;

  constructor(private analyticsService: MockAnalyticsService) {
    this.kpiData$ = new Observable();
  }

  ngOnInit(): void {
    this.kpiData$ = this.analyticsService.getKpiData();
  }
}
