import { Component, OnInit, OnDestroy } from '@angular/core';

@Component({
  selector: 'app-landing',
  standalone: false,
  templateUrl: './landing.html',
  styleUrl: './landing.scss'
})
export class Landing implements OnInit, OnDestroy {
  // Variables pour le carrousel de flip
  currentSlide = 0;
  totalSlides = 4;
  autoRotateInterval: any;
  readonly ROTATE_INTERVAL = 4000; // 4 secondes entre chaque flip

  // Variables pour le menu mobile
  isMobileMenuOpen = false;

  constructor() { }

  ngOnInit(): void {
    this.startAutoRotate();
  }

  ngOnDestroy(): void {
    this.stopAutoRotate();
  }

  /**
   * Démarrer la rotation automatique du carrousel
   */
  private startAutoRotate(): void {
    this.autoRotateInterval = setInterval(() => {
      this.nextSlide();
    }, this.ROTATE_INTERVAL);
  }

  /**
   * Arrêter la rotation automatique du carrousel
   */
  private stopAutoRotate(): void {
    if (this.autoRotateInterval) {
      clearInterval(this.autoRotateInterval);
    }
  }

  /**
   * Passer à la slide suivante
   */
  nextSlide(): void {
    this.currentSlide = (this.currentSlide + 1) % this.totalSlides;
  }

  /**
   * Aller à une slide spécifique
   * @param index Index de la slide (0-3)
   */
  goToSlide(index: number): void {
    if (index === this.currentSlide) return;
    
    this.currentSlide = index;
    
    // Réinitialiser le timer d'auto-rotation
    this.stopAutoRotate();
    this.startAutoRotate();
  }

  /**
   * Basculer l'état du menu mobile
   */
  toggleMobileMenu(): void {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  /**
   * Fermer le menu mobile
   */
  closeMobileMenu(): void {
    this.isMobileMenuOpen = false;
  }

  /**
   * Défiler vers une section spécifique
   * @param sectionId ID de la section cible
   */
  scrollToSection(sectionId: string): void {
    this.closeMobileMenu(); // Fermer le menu mobile si ouvert
    
    const element = document.getElementById(sectionId);
    if (element) {
      element.scrollIntoView({ 
        behavior: 'smooth',
        block: 'start'
      });
    }
  }
}