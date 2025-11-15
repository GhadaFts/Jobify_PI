// Analytics-specific types derived from existing project models

export interface KpiData {
  totalJobs: number;
  jobsLast7Days: number;
  totalUsers: number;
  newUsersLast7Days: number;
  totalApplications: number;
  avgApplicationsPerJob: number;
}

export interface JobsPostedData {
  date: string;
  count: number;
}

export interface ActiveVsExpiredData {
  date: string;
  active: number;
  expired: number;
}

export interface UsersGrowthData {
  date: string;
  newUsers: number;
  dau: number; // Daily Active Users
}

export interface ApplicationFunnelData {
  views: number;
  applyClicks: number;
  applications: number;
  interviews: number;
  hires: number;
}

export interface AppsPerJobData {
  jobId: string;
  jobTitle: string;
  count: number;
}

export interface TopCompaniesData {
  companyName: string;
  jobCount: number;
  applicationCount: number;
}

export interface TopCategoriesData {
  category: string;
  jobs: number;
  applications: number;
}

export interface GeoHeatmapData {
  region: string;
  jobs: number;
  applications: number;
}

export interface AlertData {
  metric: string;
  change: number;
  alert: boolean;
}

// Filters interface
export interface AnalyticsFilters {
  dateRange: {
    startDate: string;
    endDate: string;
  };
  jobType?: string;
  company?: string;
  category?: string;
  region?: string;
  granularity: 'day' | 'week' | 'month';
}

export interface AnalyticsResponse<T> {
  data: T;
  timestamp: string;
  filters?: AnalyticsFilters;
}
