import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { AnalyticsService } from '../../services/analytics.service';

@Component({
  selector: 'app-jobs-posted-chart',
  templateUrl: './jobs-posted-chart.html',
  styleUrls: ['./jobs-posted-chart.scss'],
  standalone: false
})
export class JobsPostedChartComponent implements OnInit {
  chartData$: Observable<any>;
  chartOptions: any;

  constructor(private analyticsService: AnalyticsService) {
    this.chartData$ = new Observable();
    this.initChartOptions();
  }

  ngOnInit(): void {
    this.chartData$ = this.analyticsService.getJobsOverTime().pipe(
      tap(data => {
        console.log('📊 Jobs over time data received:', data);
      }),
      map(data => {
        // Handle both formats: { data: [...] } or direct array
        if (Array.isArray(data)) {
          console.log('✅ Data is array, wrapping it');
          return { data: data };
        }
        
        if (!data.data || data.data.length === 0) {
          console.warn('⚠️ No jobs data available');
          return { data: [] };
        }
        
        console.log('✅ Data format correct:', data);
        return data;
      })
    );
  }

  getMaxCount(data: any[]): number {
    if (!data || data.length === 0) return 1;
    return Math.max(...data.map(d => d.count));
  }

  private initChartOptions(): void {
    this.chartOptions = {
      responsive: true,
      maintainAspectRatio: true,
      plugins: {
        legend: {
          display: false
        },
        title: {
          display: true,
          text: 'Jobs Posted Over Time',
          font: {
            size: 16,
            weight: 600
          }
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            color: '#6B7280'
          },
          grid: {
            color: '#F3F4F6'
          }
        },
        x: {
          ticks: {
            color: '#6B7280'
          },
          grid: {
            color: '#F3F4F6'
          }
        }
      }
    };
  }

  getChartData(data: any): any {
    return {
      labels: data.map((d: any) => d.date),
      datasets: [
        {
          label: 'Jobs Posted',
          data: data.map((d: any) => d.count),
          borderColor: '#3B82F6',
          backgroundColor: 'rgba(59, 130, 246, 0.1)',
          borderWidth: 2,
          fill: true,
          tension: 0.4,
          pointRadius: 4,
          pointHoverRadius: 6,
          pointBackgroundColor: '#3B82F6',
          pointBorderColor: '#fff',
          pointBorderWidth: 2
        }
      ]
    };
  }
}
