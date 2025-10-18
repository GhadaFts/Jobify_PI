import { Component } from '@angular/core';

@Component({
  selector: 'app-dashboard',
  standalone: false,
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss']
})
export class Dashboard {
  activeSection = 'cv-correction';

  onSectionChange(section: string) { // Explicitly type the parameter as string
    this.activeSection = section;
  }
}