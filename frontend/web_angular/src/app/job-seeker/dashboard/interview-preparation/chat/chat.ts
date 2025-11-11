// chat.component.ts
import { Component, OnInit, ViewChild } from '@angular/core';
import { AiService } from '../../../../ai-service/ai-service';
import { ChatbotLogo } from '../chatbot-logo/chatbot-logo';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.html',
  standalone: false
})
export class Chat implements OnInit {
  @ViewChild(ChatbotLogo) chatbotLogo!: ChatbotLogo;

  messages: {text: string, isUser: boolean}[] = [];
  userInput = '';
  conversationPhase: 'greeting' | 'job_type' | 'experience' | 'skills' | 'preparation' | 'feedback' | 'practice' = 'greeting';
  userData: any = {
    jobType: '',
    experience: '',
    skills: '',
    practiceQuestions: [],
    currentQuestionIndex: 0,
    userResponses: {}
  };

  showWelcomeScreen = true;
  logoUrl: string | null = null;
  isWaitingForAnswer = false;

  constructor(private aiService: AiService) {}

  private loadLogo() {
    const imagePath = 'assets/chatbot-logo.png';
    const img = new Image();
    img.onload = () => this.logoUrl = imagePath;
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
    }, 7000);
  }

  private startConversation() {
    this.addBotMessage("🎯 **Bonjour ! Je suis votre coach IA pour la préparation aux entretiens.**");
    setTimeout(() => {
      this.addBotMessage("Je vais vous aider à vous préparer étape par étape pour décrocher le job de vos rêves !");
    }, 1000);
    setTimeout(() => {
      this.addBotMessage("Commençons par comprendre votre objectif professionnel...");
      this.askAboutJobType();
    }, 2500);
  }

  private askAboutJobType() {
    this.conversationPhase = 'job_type';
    this.addBotMessage("🧑‍💼 **Quel type de poste recherchez-vous ?**\n*(ex: Développeur Frontend, Chef de projet, Data Scientist, etc.)*");
  }

  private askAboutExperience() {
    this.conversationPhase = 'experience';
    this.addBotMessage("📊 **Parlez-moi de votre expérience professionnelle**\n*Combien d'années d'expérience et dans quel domaine spécifique ?*");
  }

  private askAboutSkills() {
    this.conversationPhase = 'skills';
    this.addBotMessage("🛠️ **Quelles sont vos compétences principales ?**\n*Mentionnez vos technologies maîtrisées et vos soft skills.*");
  }

  private async providePreparation() {
    this.conversationPhase = 'preparation';
    
    const prompt = `En tant que coach en recrutement expérimenté, donne des conseils PERSONNALISÉS et ACTIONNABLES pour un candidat qui:
    - Poste visé: ${this.userData.jobType}
    - Expérience: ${this.userData.experience}
    - Compétences: ${this.userData.skills}

    Donne 3-4 conseils SPÉCIFIQUES avec des exemples concrets. Sois direct, professionnel et motivant.`;

    this.addBotMessage("🎭 **Analyse de votre profil en cours...**");
    
    if (this.chatbotLogo) {
      this.chatbotLogo.playThinkingAnimation();
    }

    try {
      const response = await this.aiService.ask(prompt);
      this.addBotMessage("💡 **Voici mes conseils personnalisés pour vous :**\n\n" + response);
      this.offerPractice();
    } catch (error) {
      this.addBotMessage("💡 **Conseils généraux pour réussir votre entretien :**\n\n" +
        "• 🎯 **Préparez votre pitch** : Présentation claire de 2 minutes\n" +
        "• 🔍 **Recherche approfondie** : Connaître l'entreprise, ses valeurs et ses projets\n" + 
        "• 💪 **Valorisez vos compétences** : " + (this.userData.skills || 'Vos points forts') + "\n" +
        "• ❓ **Questions intelligentes** : Montrez votre intérêt pour le poste et l'entreprise");
      this.offerPractice();
    }
  }

  private offerPractice() {
    this.conversationPhase = 'feedback';
    this.addBotMessage("💪 **Souhaitez-vous faire une simulation d'entretien ?**\n\n" +
      "*Répondez par :*\n" +
      "• **'oui'** pour commencer la simulation\n" +
      "• **'non'** pour continuer à discuter\n" +
      "• Posez-moi **vos questions spécifiques** sur les entretiens");
  }

  async sendMessage() {
    if (!this.userInput.trim()) return;

    const userMessage = this.userInput.trim();
    
    if (this.chatbotLogo) {
      this.chatbotLogo.playListeningAnimation();
    }

    this.addUserMessage(userMessage);
    this.userInput = '';

    // Vérifier si hors sujet
    if (await this.isOffTopic(userMessage)) {
      this.addBotMessage("🚫 **Je suis spécialisé dans la préparation aux entretiens.**\n" +
        "*Concentrons-nous sur votre recherche d'emploi, vos compétences et vos techniques d'entretien.*");
      return;
    }

    try {
      if (this.chatbotLogo) {
        this.chatbotLogo.playThinkingAnimation();
      }

      // Gestion selon la phase de conversation
      switch (this.conversationPhase) {
        case 'job_type':
          await this.handleJobTypeResponse(userMessage);
          break;
        
        case 'experience':
          await this.handleExperienceResponse(userMessage);
          break;
        
        case 'skills':
          await this.handleSkillsResponse(userMessage);
          break;
        
        case 'preparation':
        case 'feedback':
          await this.handleFeedbackResponse(userMessage);
          break;
        
        case 'practice':
          await this.handlePracticeResponse(userMessage);
          break;
      }

      if (this.chatbotLogo) {
        setTimeout(() => this.chatbotLogo.playTalkAnimation(), 500);
      }
      
    } catch (error) {
      console.error('Erreur:', error);
      this.addBotMessage("😔 **Désolé, je rencontre un problème technique.**\n*Veuillez réessayer dans quelques instants.*");
    }
  }

  private async handleJobTypeResponse(message: string) {
    this.userData.jobType = message;
    
    // Validation et feedback
    if (message.length < 3) {
      this.addBotMessage("🤔 **Je n'ai pas bien saisi.** Pouvez-vous préciser le type de poste que vous recherchez ?");
      return;
    }
    
    this.addBotMessage(`✅ **${message}** - Excellent choix ! Passons à votre expérience.`);
    this.askAboutExperience();
  }

  private async handleExperienceResponse(message: string) {
    this.userData.experience = message;
    
    if (message.length < 5) {
      this.addBotMessage("📝 **Pouvez-vous développer un peu plus ?** Par exemple : '3 ans en développement web' ou 'Débutant en marketing digital'");
      return;
    }
    
    this.addBotMessage(`✅ **Expérience notée !** Maintenant parlons de vos compétences.`);
    this.askAboutSkills();
  }

  private async handleSkillsResponse(message: string) {
    this.userData.skills = message;
    
    if (message.length < 5) {
      this.addBotMessage("🛠️ **N'hésitez pas à détailler !** Quelles technologies maîtrisez-vous ? Quelles sont vos qualités professionnelles ?");
      return;
    }
    
    this.addBotMessage(`✅ **Compétences enregistrées !** Je prépare maintenant vos conseils personnalisés...`);
    this.providePreparation();
  }

  private async handleFeedbackResponse(message: string) {
    const lowerMessage = message.toLowerCase();
    
    if (lowerMessage.includes('oui') || lowerMessage.includes('simulation') || lowerMessage.includes('commencer')) {
      this.startPracticeSession();
    } else if (lowerMessage.includes('non') || lowerMessage.includes('pas maintenant')) {
      this.addBotMessage("👍 **Pas de problème !** Continuez à me poser vos questions sur les entretiens.\n" +
        "*Quand vous serez prêt, dites simplement 'simulation'.*");
    } else {
      await this.answerInterviewQuestion(message);
    }
  }

  private async handlePracticeResponse(message: string) {
    if (this.isWaitingForAnswer) {
      await this.processPracticeAnswer(message);
    } else {
      this.addBotMessage("💡 **Nous sommes en simulation d'entretien.**\n" +
        "*Pour revenir au mode normal, dites 'stop' ou 'arrêter'.*");
    }
  }

  private async processPracticeAnswer(message: string) {
    const lowerMessage = message.toLowerCase();
    
    // Détection des réponses indiquant que l'utilisateur a déjà répondu
    const alreadyAnsweredKeywords = [
      'déjà', 'déja', 'already', 'répondu', 'presenté', 'présenté', 
      'dit', 'expliqué', 'parlé', 'mentionné'
    ];
    
    const hasAlreadyAnswered = alreadyAnsweredKeywords.some(keyword => 
      lowerMessage.includes(keyword)
    );

    if (hasAlreadyAnswered) {
      this.addBotMessage("✅ **Je comprends que vous avez déjà abordé ce point.**\n" +
        "*Passons à la question suivante pour varier les sujets.*");
      this.askNextPracticeQuestion();
      return;
    }

    // Analyser la réponse de l'utilisateur
    const currentQuestion = this.userData.practiceQuestions[this.userData.currentQuestionIndex];
    this.userData.userResponses[currentQuestion] = message;

    // Donner un feedback court
    if (message.length > 10) {
      this.addBotMessage("✅ **Bonne réponse !** Vous structurez bien vos idées.");
    } else {
      this.addBotMessage("💡 **N'hésitez pas à développer un peu plus** - les recruteurs aiment les réponses détaillées.");
    }

    this.askNextPracticeQuestion();
  }

  private startPracticeSession() {
    this.conversationPhase = 'practice';
    this.userData.practiceQuestions = [
      "🗣️ **Pouvez-vous vous présenter brièvement ?** *(2-3 minutes maximum)*",
      "🎯 **Pourquoi avez-vous postulé spécifiquement pour ce poste de " + (this.userData.jobType || 'développeur') + " ?**",
      "⭐ **Quelle est votre plus grande réalisation professionnelle ?**",
      "🔄 **Comment gérez-vous les situations stressantes ou les délais serrés ?**",
      "📈 **Où vous voyez-vous dans 3 à 5 ans ?**",
      "🤝 **Pourquoi devrions-nous vous choisir vous plutôt qu'un autre candidat ?**"
    ];
    this.userData.currentQuestionIndex = 0;
    this.userData.userResponses = {};

    this.addBotMessage("🎬 **Parfait ! Simulation d'entretien lancée.**\n\n" +
      "*Je vais jouer le rôle du recruteur. Répondez naturellement comme en vrai entretien.*\n\n" +
      "📋 **Conseil :** Prenez votre temps, soyez authentique et structuré !");

    setTimeout(() => {
      this.askNextPracticeQuestion();
    }, 2000);
  }

  private askNextPracticeQuestion() {
    if (this.userData.currentQuestionIndex < this.userData.practiceQuestions.length) {
      const question = this.userData.practiceQuestions[this.userData.currentQuestionIndex];
      this.userData.currentQuestionIndex++;
      this.isWaitingForAnswer = true;
      
      setTimeout(() => {
        this.addBotMessage(question);
      }, 1000);
    } else {
      // Fin de la simulation
      this.endPracticeSession();
    }
  }

  private endPracticeSession() {
    this.conversationPhase = 'feedback';
    this.isWaitingForAnswer = false;
    
    this.addBotMessage("🎉 **Excellent ! Simulation terminée.**\n\n" +
      "💪 **Points forts identifiés :**\n" +
      "• Structure claire de vos réponses\n" +
      "• Expérience bien valorisée\n" +
      "• Motivation évidente\n\n" +
      "📚 **Suggestions d'amélioration :**\n" +
      "• Préparer davantage d'exemples concrets\n" +
      "• Varier les situations professionnelles\n\n" +
      "*Voulez-vous :*\n" +
      "• 🔁 **Refaire une simulation**\n" +
      "• 💡 **Recevoir plus de conseils**\n" +
      "• ❓ **Poser une question spécifique**");
  }

  // chat.component.ts - CORRECTION DE LA FONCTION isOffTopic
private async isOffTopic(message: string): Promise<boolean> {
  const lowerMessage = message.toLowerCase().trim();
  
  // ✅ LISTE BLANCHE - mots toujours acceptés
  const allowedWords = [
    'oui', 'non', 'yes', 'no', 'ok', 'd\'accord', 'simulation', 
    'entretien', 'entretiens', 'préparation', 'cv', 'lettre', 
    'motivation', 'compétence', 'compétences', 'experience', 
    'expérience', 'poste', 'emploi', 'job', 'carrière', 'recrutement',
    'salaire', 'négociation', 'question', 'questions', 'réponse',
    'conseil', 'conseils', 'aide', 'merci'
  ];

  // ✅ Vérifier si le message contient des mots autorisés
  const hasAllowedWord = allowedWords.some(word => 
    lowerMessage.includes(word)
  );

  if (hasAllowedWord) {
    return false;
  }

  // ✅ Vérification avec IA seulement pour les messages longs
  if (lowerMessage.length > 10) {
    try {
      const prompt = `La question "${message}" est-elle en rapport avec la préparation aux entretiens d'embauche, la recherche d'emploi, les CV, les lettres de motivation ou les compétences professionnelles ? Réponds uniquement par "oui" ou "non".`;
      const response = await this.aiService.ask(prompt);
      return response.toLowerCase().includes('non');
    } catch (error) {
      return false; // En cas d'erreur, être permissif
    }
  }

  // ✅ Pour les messages courts sans mots autorisés, considérer comme hors sujet
  return false; // Temporairement plus permissif pour tester
}

  private async answerInterviewQuestion(question: string) {
    const prompt = `En tant que coach en recrutement expérimenté, réponds de manière CONCISE et PRATIQUE à cette question sur les entretiens d'embauche: "${question}"
    
    Donne des conseils actionnables en maximum 3-4 phrases. Sois direct et professionnel.`;

    try {
      const response = await this.aiService.ask(prompt);
      this.addBotMessage(response);
    } catch (error) {
      this.addBotMessage("💡 **Je peux vous aider sur :**\n" +
        "• Les techniques d'entretien\n" +
        "• La préparation de CV et lettres de motivation\n" +
        "• Les questions types et leurs réponses\n" +
        "• La négociation salariale\n" +
        "• Les compétences recherchées par les employeurs");
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
}