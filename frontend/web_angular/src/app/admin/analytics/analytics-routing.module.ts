import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AnalyticsDashboardComponent } from './analytics-dashboard';
import { AdminSettingsComponent } from './components/admin-settings/admin-settings';

const routes: Routes = [
  {
    path: '',
    component: AnalyticsDashboardComponent
  },
  {
    path: 'settings',
    component: AdminSettingsComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AnalyticsRoutingModule { }
