import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { AnalyticsFilters } from '../../analytics.types';
import { MockAnalyticsService } from '../../services/mock-analytics.service';

@Component({
  selector: 'app-filters-panel',
  templateUrl: './filters-panel.html',
  styleUrls: ['./filters-panel.scss'],
  standalone: false
})
export class FiltersPanelComponent implements OnInit {
  @Output() filtersChanged = new EventEmitter<AnalyticsFilters>();

  filters: AnalyticsFilters;

  jobTypes = ['Full-time', 'Part-time', 'Contract', 'Temporary', 'Freelance'];
  companies = ['Tech Corp', 'Microsoft', 'StartUp Inc', 'Cloud Solutions', 'Innovate Labs', 'Creative Studio', 'Google', 'Amazon'];
  categories = ['Frontend Development', 'Backend Development', 'Data Science', 'DevOps', 'Product Management', 'Design (UX/UI)', 'QA Engineering', 'Cloud Architecture'];
  regions = ['California', 'New York', 'Texas', 'Florida', 'Illinois', 'Pennsylvania', 'Ohio', 'Georgia'];

  showAdvanced = false;

  constructor(private analyticsService: MockAnalyticsService) {
    this.filters = this.analyticsService.getFilters();
  }

  ngOnInit(): void {
    this.analyticsService.filters$.subscribe(filters => {
      this.filters = filters;
    });
  }

  onFilterChange(): void {
    this.analyticsService.updateFilters(this.filters);
    this.filtersChanged.emit(this.filters);
  }

  resetFilters(): void {
    this.filters = {
      dateRange: { startDate: '2025-10-01', endDate: '2025-10-31' },
      granularity: 'day'
    };
    this.onFilterChange();
  }

  toggleAdvanced(): void {
    this.showAdvanced = !this.showAdvanced;
  }
}
