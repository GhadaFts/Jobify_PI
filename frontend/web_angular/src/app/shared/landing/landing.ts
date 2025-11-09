import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-landing',
  standalone: false,
  templateUrl: './landing.html',
  styleUrl: './landing.scss'
})
export class Landing implements OnInit {

  constructor() { }

  ngOnInit(): void {
    // You can add any initialization logic here
  }

  // Optional: Add methods for interactive elements
  scrollToSection(sectionId: string): void {
    const element = document.getElementById(sectionId);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth' });
    }
  }
}