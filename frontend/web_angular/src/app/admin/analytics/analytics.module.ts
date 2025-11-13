import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AnalyticsRoutingModule } from './analytics-routing.module';
import { AnalyticsDashboardComponent } from './analytics-dashboard';

// Components
import { KpiCardsComponent } from './components/kpi-cards/kpi-cards';
import { JobsPostedChartComponent } from './components/jobs-posted-chart/jobs-posted-chart';
import { ActiveVsExpiredChartComponent } from './components/active-vs-expired-chart/active-vs-expired-chart';
import { UsersGrowthChartComponent } from './components/users-growth-chart/users-growth-chart';
import { ApplicationFunnelChartComponent } from './components/application-funnel-chart/application-funnel-chart';
import { AppsPerJobChartComponent } from './components/apps-per-job-chart/apps-per-job-chart';
import { TopCompaniesChartComponent } from './components/top-companies-chart/top-companies-chart';
import { TopCategoriesChartComponent } from './components/top-categories-chart/top-categories-chart';
import { GeoHeatmapChartComponent } from './components/geo-heatmap-chart/geo-heatmap-chart';
import { AlertsPanelComponent } from './components/alerts-panel/alerts-panel';
import { FiltersPanelComponent } from './components/filters-panel/filters-panel';
import { AdminSettingsComponent } from './components/admin-settings/admin-settings';

@NgModule({
  declarations: [
    AnalyticsDashboardComponent,
    KpiCardsComponent,
    JobsPostedChartComponent,
    ActiveVsExpiredChartComponent,
    UsersGrowthChartComponent,
    ApplicationFunnelChartComponent,
    AppsPerJobChartComponent,
    TopCompaniesChartComponent,
    TopCategoriesChartComponent,
    GeoHeatmapChartComponent,
    AlertsPanelComponent,
    FiltersPanelComponent,
    AdminSettingsComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    AnalyticsRoutingModule
  ]
})
export class AnalyticsModule { }
