import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-sidebar',
  standalone: false,
  templateUrl: './sidebar.html',
  styleUrls: ['./sidebar.scss']
})
export class Sidebar {
  @Input() activeSection: string = 'cv-correction'; // Ensure @Input is present and typed as string
  @Output() sectionChange = new EventEmitter<string>(); // Ensure output emits string

  navigationItems = [
    { id: 'cv-correction', label: 'CV Correction' },
    { id: 'find-job', label: 'Find Job' },
    { id: 'job-analyse', label: 'Job Analyse' }
  ];

  secondaryItems = [
    { id: 'settings', label: 'Settings' },
    { id: 'logout', label: 'Logout' }
  ];

  onSectionChange(section: string) {
    this.sectionChange.emit(section);
  }

  getIconPath(id: string): string {
    switch (id) {
      case 'cv-correction':
        return 'M11 3H6a2 2 0 00-2 2v14a2 2 0 002 2h12a2 2 0 002-2V8l-6-6zm-1 14H8v-2h2v2zm4-4h-6v-2h6v2zm0-4h-6V7h6v2z';
      case 'find-job':
        return 'M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z';
      case 'job-analyse':
        return 'M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z';
      case 'settings':
        return 'M19.14 12.94c.04-.3.06-.61.06-.94 0-.32-.02-.64-.07-.94l2.03-1.58c.18-.14.23-.41.12-.61l-1.92-3.32c-.12-.22-.37-.29-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.74-2.46c-.05-.18-.23-.3-.43-.3H8.4c-.2 0-.38.12-.43.3l-.74 2.46c-.59.24-1.12.56-1.62.94l-2.39-.96c-.22-.07-.47 0-.59.22L2.61 8.3c-.11.2-.06.47.12.61l2.03 1.58c-.05.3-.07.62-.07.94 0 .32.02.64.07.94l-2.03 1.58c-.18.14-.23.41-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.74 2.46c.05.18.23.3.43.3h3.2c.2 0 .38-.12.43-.3l.74-2.46c.59-.24 1.12-.56 1.62-.94l2.39.96c.22.07.47 0 .59-.22l1.92-3.32c.11-.2.06-.47-.12-.61l-2.03-1.58zM12 15.5c-1.93 0-3.5-1.57-3.5-3.5s1.57-3.5 3.5-3.5 3.5 1.57 3.5 3.5-1.57 3.5-3.5 3.5z';
      case 'logout':
        return 'M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1';
      default:
        return '';
    }
  }
}