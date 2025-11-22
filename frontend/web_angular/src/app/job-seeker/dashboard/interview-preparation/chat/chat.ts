import { Component, OnInit, ViewChild } from '@angular/core';
import {
  AiService,
  ConversationContext,
  UserProfile,
  ChatResponse,
} from '../../../../ai-service/ai-service-backend';
import { ChatbotLogo } from '../chatbot-logo/chatbot-logo';

interface ChatMessage {
  text: string;
  isUser: boolean;
}

@Component({
  selector: 'app-chat',
  templateUrl: './chat.html',
  standalone: false,
})
export class Chat implements OnInit {
  @ViewChild(ChatbotLogo) chatbotLogo!: ChatbotLogo;

  messages: ChatMessage[] = [];
  userInput = '';

  // État de la conversation
  conversationContext: ConversationContext = {
    phase: 'collect_info',
  };

  userProfile: UserProfile = {};

  showWelcomeScreen = true;
  logoUrl: string | null = null;
  isLoading = false;

  constructor(private aiService: AiService) {}

  private loadLogo() {
    const imagePath = 'assets/chatbot-logo.png';
    const img = new Image();
    img.onload = () => (this.logoUrl = imagePath);
    img.onerror = () => {
      this.logoUrl = null;
      console.log('Logo image not found, using emoji fallback');
    };
    img.src = imagePath;
  }

  ngOnInit() {
    this.startOpeningSequence();
    this.loadLogo();
  }

  private startOpeningSequence() {
    setTimeout(() => {
      this.showWelcomeScreen = false;
      this.startConversation();
    }, 3000);
  }

  private startConversation() {
    // Le backend va démarrer automatiquement la collecte d'informations
    this.addBotMessage(
      '🎯 **Bonjour ! Je suis votre coach IA pour la préparation aux entretiens.**'
    );
    setTimeout(() => {
      this.addBotMessage('Je vais vous aider à vous préparer en 3 étapes :');
    }, 1000);
    setTimeout(() => {
      this.addBotMessage('1. 🧩 **Analyse de votre profil** - Pour comprendre vos besoins');
    }, 2000);
    setTimeout(() => {
      this.addBotMessage('2. 💡 **Conseils personnalisés** - Adaptés à votre situation');
    }, 3000);
    setTimeout(() => {
      this.addBotMessage(
        "3. 🎬 **Simulation d'entretien** - Pour vous entraîner en conditions réelles"
      );
      setTimeout(() => {
        this.addBotMessage(
          'Commençons par faire connaissance... Parlez-moi du poste que vous visez !'
        );
      }, 1000);
    }, 4000);
  }

  async sendMessage() {
    if (!this.userInput.trim() || this.isLoading) return;
    
    const userMessage = this.userInput.trim();
    this.addUserMessage(userMessage);
    this.userInput = '';
    this.isLoading = true;

    // 🆕 VÉRIFICATION ANTI-FRUSTRATION
    if (this.shouldResetConversation(this.messages)) {
      this.addBotMessage("🔄 **Je vois que je répète mes questions - désolé !**\n\nPassons directement à l'étape suivante avec les informations que vous m'avez déjà données.");
      this.conversationContext = { phase: 'advice' };
      this.isLoading = false;
      return;
    }

    if (this.chatbotLogo) {
      this.chatbotLogo.playThinkingAnimation();
    }

    try {
      // Appel au backend avec le contexte actuel
      const response = await this.aiService
        .chatWithInterviewBot(userMessage, this.conversationContext, this.userProfile)
        .toPromise();

      if (response) {
        // Mettre à jour le contexte de conversation
        this.conversationContext = {
          ...this.conversationContext,
          phase: response.conversationPhase,
          currentStep: response.nextStep,
        };

        // Mettre à jour le profil utilisateur si fourni
        if (response.userProfileUpdates) {
          this.userProfile = {
            ...this.userProfile,
            ...response.userProfileUpdates,
          };
        }

        // Ajouter la réponse du bot
        this.addBotMessage(response.response);

        // Stocker les questions si fournies (pour la phase pratique)
        if (response.questions && response.questions.length > 0) {
          (this.conversationContext as any).questions = response.questions;
        }
      }
    } catch (error) {
      console.error('Erreur de communication avec le chatbot:', error);
      this.addBotMessage(
        '😔 **Désolé, je rencontre un problème technique.**\n*Veuillez réessayer dans quelques instants.*'
      );
    } finally {
      this.isLoading = false;

      if (this.chatbotLogo) {
        setTimeout(() => this.chatbotLogo.playTalkAnimation(), 500);
      }
    }
  }

  private addUserMessage(text: string) {
    this.messages.push({ text, isUser: true });
    this.scrollToBottom();
  }

  private addBotMessage(text: string) {
    this.messages.push({ text, isUser: false });
    this.scrollToBottom();
  }

  private scrollToBottom() {
    setTimeout(() => {
      const messagesContainer = document.querySelector('.messages-container');
      if (messagesContainer) {
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
      }
    }, 100);
  }

  // Méthode pour réinitialiser la conversation
  resetConversation() {
    this.messages = [];
    this.conversationContext = { phase: 'collect_info' };
    this.userProfile = {};
    this.userInput = '';

    this.addBotMessage(
      '🔄 **Conversation réinitialisée !**\n\nParlez-moi du poste que vous visez et je vous aiderai à préparer votre entretien.'
    );
  }

  // Ajoutez cette méthode dans votre composant
  private shouldResetConversation(messages: ChatMessage[]): boolean {
    // Vérifier les 3 derniers messages du bot
    const lastBotMessages = this.messages
      .filter(m => !m.isUser)
      .slice(-3)
      .map(m => m.text);
    
    // Si le bot répète la même question
    const hasRepetition = lastBotMessages.length >= 2 && 
      lastBotMessages[0] === lastBotMessages[1];
    
    // Si l'utilisateur montre des signes de frustration dans son dernier message
    const lastUserMessage = this.messages.filter(m => m.isUser).slice(-1)[0];
    const userIsFrustrated = lastUserMessage && 
      (lastUserMessage.text.toLowerCase().includes('ahhh') ||
       lastUserMessage.text.toLowerCase().includes('pourquoi') ||
       lastUserMessage.text.toLowerCase().includes('encore'));
    
    return hasRepetition && userIsFrustrated;
  }
}