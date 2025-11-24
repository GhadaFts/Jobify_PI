import { Component } from '@angular/core';

@Component({
  selector: 'app-job-seeker-sidebar',
  templateUrl: './job-seeker-sidebar.html',
  styleUrls: ['./job-seeker-sidebar.scss'],
  standalone: false,
})
export class JobSeekerSidebar {
  trendingCommunities = [
    { id: '1', name: 'Startup Founders India', description: 'Discuss ideas, funding, and growth', members: '12.5k', avatar: '🚀', category: 'Business' },
    { id: '2', name: 'Pune Weather Updates', description: 'Daily weather & alerts', members: '8.9k', avatar: '🌦️', category: 'Local' },
    { id: '3', name: 'Indie Music Lovers', description: 'For fans of Indian independent artists', members: '5.2k', avatar: '🎵', category: 'Music' },
    { id: '4', name: 'Tech Innovators', description: 'Tech trends and innovations', members: '4.8k', avatar: '💻', category: 'Technology' },
    { id: '5', name: 'Foodie Hub', description: 'Share recipes and food experiences', members: '3.5k', avatar: '🍲', category: 'Culture' }
  ];
}