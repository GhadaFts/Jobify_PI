// chatbot-logo.component.ts
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-chatbot-logo',
  template: `
    <div class="flex flex-col items-center justify-center transition-all duration-500">
      <!-- Logo principal -->
      <div class="relative">
        <div class="w-32 h-32 rounded-full bg-blue-100 flex items-center justify-center shadow-xl border-4 border-blue-300 overflow-hidden">
          <img 
            *ngIf="logoUrl" 
            [src]="logoUrl" 
            alt="Coach IA Entretien"
            class="w-full h-full object-cover"
          />
          <div *ngIf="!logoUrl" class="w-full h-full bg-blue-500 flex items-center justify-center text-white font-bold text-2xl">
            🤖
          </div>
        </div>
        
        <!-- Indicateur d'activité -->
        <div *ngIf="isActive" class="absolute -top-2 -right-2 w-6 h-6 bg-green-500 rounded-full border-2 border-white animate-pulse"></div>
      </div>

      <!-- Message de bienvenue -->
      <div *ngIf="showWelcome" class="mt-8 bg-white rounded-2xl p-6 shadow-2xl border border-blue-200 max-w-md text-center animate-fade-in">
        <div class="flex items-center space-x-2 mb-3 justify-center">
          <div class="w-3 h-3 bg-green-500 rounded-full animate-pulse"></div>
          <span class="text-lg font-bold text-gray-800">Coach IA Entretien</span>
        </div>
        <p class="text-gray-600 leading-relaxed mb-4">
          Bonjour ! Je suis votre assistant personnel pour la préparation aux entretiens.
        </p>
        <p class="text-blue-600 font-semibold">Prêt à décrocher le job de vos rêves ?</p>
      </div>
    </div>
  `,
  standalone: false
})
export class ChatbotLogo {
  @Input() showWelcome = false;
  @Input() isActive = true;
  
  // ✅ Image importée dans le TS
  logoUrl: string | null = null;

  ngOnInit() {
    this.loadLogo();
  }

  private loadLogo() {
    // Essayez de charger l'image depuis assets
    const imagePath = 'assets/chatbot-logo.png';
    
    const img = new Image();
    img.onload = () => {
      this.logoUrl = imagePath;
    };
    img.onerror = () => {
      this.logoUrl = null; // Utilise l'emoji fallback
      console.log('Logo image not found, using emoji fallback');
    };
    img.src = imagePath;
  }

  // Méthodes d'animation simples
  public playTalkAnimation() {
    const element = document.querySelector('app-chatbot-logo .bg-blue-100');
    if (element) {
      element.classList.add('scale-110');
      setTimeout(() => {
        element.classList.remove('scale-110');
      }, 200);
    }
  }

  public playListeningAnimation() {
    const element = document.querySelector('app-chatbot-logo');
    if (element) {
      element.classList.add('translate-y-2');
      setTimeout(() => {
        element.classList.remove('translate-y-2');
      }, 500);
    }
  }

  public playThinkingAnimation() {
    const element = document.querySelector('app-chatbot-logo');
    if (element) {
      element.classList.add('animate-pulse');
      setTimeout(() => {
        element.classList.remove('animate-pulse');
      }, 800);
    }
  }
}