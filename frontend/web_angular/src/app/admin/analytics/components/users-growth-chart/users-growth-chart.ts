import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { MockAnalyticsService } from '../../services/mock-analytics.service';

@Component({
  selector: 'app-users-growth-chart',
  templateUrl: './users-growth-chart.html',
  styleUrls: ['./users-growth-chart.scss'],
  standalone: false
})
export class UsersGrowthChartComponent implements OnInit {
  chartData$: Observable<any>;
  lastTenItems: any[] = [];

  constructor(private analyticsService: MockAnalyticsService) {
    this.chartData$ = new Observable();
  }

  ngOnInit(): void {
    this.chartData$ = this.analyticsService.getUsersGrowth();
  }

  getTotalNewUsers(data: any[]): number {
    return data.reduce((sum: number, item: any) => sum + item.newUsers, 0);
  }

  getLastTenItems(data: any[]): any[] {
    return data.slice(-10);
  }

  getGrowthPercentage(item: any, index: number, allItems: any[]): string {
    if (index === 0) return '0.0';
    const prevItem = allItems[index - 1];
    if (!prevItem) return '0.0';
    const prevDau = prevItem.dau || 0;
    const currentDau = item.dau || 0;
    if (prevDau === 0) return '0.0';
    const growth = ((currentDau - prevDau) / prevDau * 100);
    return growth.toFixed(1);
  }
}
