import { Injectable, HttpException, HttpStatus } from '@nestjs/common';
import { GeminiService } from '../gemini/gemini.service';
import {
  ChatRequestDto,
  ConversationContext,
  UserProfile,
} from './dto/chat-request.dto';
import { ChatResponseDto } from './dto/chat-response.dto';

@Injectable()
export class InterviewBotService {
  constructor(private geminiService: GeminiService) {}

  async handleChat(chatRequest: ChatRequestDto): Promise<ChatResponseDto> {
    try {
      const { message, conversationContext, userProfile } = chatRequest;

      // DÉTECTION DES RÉPONSES "DÉJÀ RÉPONDU" - CORRECTION CRITIQUE
      if (this.isUserSayingAlreadyAnswered(message)) {
        // Correction: Vérifier si conversationContext existe avant de l'utiliser
        return this.handleAlreadyAnswered(
          conversationContext || { phase: 'collect_info' },
          userProfile || {},
        );
      }

      // Si pas de contexte, démarrer la collecte d'infos
      if (!conversationContext) {
        return this.startInformationCollection();
      }

      // Créer un profil par défaut si non fourni
      const currentProfile: UserProfile = userProfile || {};

      // Gérer selon la phase
      switch (conversationContext.phase) {
        case 'collect_info':
          return await this.handleInformationCollection(
            message,
            conversationContext,
            currentProfile,
          );

        case 'advice':
          return await this.handleAdvicePhase(
            message,
            conversationContext,
            currentProfile,
          );

        case 'practice':
          return await this.handlePracticePhase(
            message,
            conversationContext,
            currentProfile,
          );

        default:
          return this.startInformationCollection();
      }
    } catch (error) {
      console.error('Interview Bot Error:', error);
      throw new HttpException(
        'Error processing chat message',
        HttpStatus.INTERNAL_SERVER_ERROR,
      );
    }
  }

  // 🆕 Détection "déjà répondu"
  private isUserSayingAlreadyAnswered(message: string): boolean {
    const alreadyAnsweredKeywords = [
      'déjà',
      'déja',
      'already',
      'répondu',
      'repondu',
      'dit',
      'informé',
      'donné',
      'précisé',
      'expliqué',
      'parlé',
      'ahhh',
      'ahh',
      'ah',
      'oh',
      'encore',
      'toujours',
      'tjr',
      'stop',
      'arrête',
      'assez',
      'suffit',
      'bon',
      'quoi',
    ];

    const lowerMessage = message.toLowerCase().trim();
    return alreadyAnsweredKeywords.some((keyword) =>
      lowerMessage.includes(keyword),
    );
  }

  // 🆕 Gérer "déjà répondu" - CORRIGÉ pour accepter context undefined
  private async handleAlreadyAnswered(
    context: ConversationContext,
    userProfile: UserProfile,
  ): Promise<ChatResponseDto> {
    // Vérifier ce qui manque dans le profil
    const missingInfo = this.getMissingInformation(userProfile);

    if (missingInfo.length > 0) {
      // Proposer de passer aux conseils avec ce qu'on a
      return {
        response: `✅ **Je comprends que vous avez déjà partagé des informations !**\n\n**Ce que je sais :**\n${this.formatKnownInfo(userProfile)}\n\n**Pour mieux vous aider, j'ai besoin de :**\n${missingInfo.map((info) => `• ${info}`).join('\n')}\n\n💡 **Souhaitez-vous :**\n• 📝 **Compléter ces informations**\n• 🎯 **Passer directement aux conseils** avec ce que nous avons\n• 🎬 **Commencer une simulation**`,
        conversationPhase: 'collect_info',
        nextStep: 'handle_already_answered',
      };
    } else {
      // Tout est complet, passer aux conseils
      return this.transitionToAdvicePhase(userProfile);
    }
  }

  // 🆕 Méthode pour formatter les infos connues
  private formatKnownInfo(profile: UserProfile): string {
    const known: string[] = [];

    if (profile.jobTitle) known.push(`• Poste : ${profile.jobTitle}`);
    if (profile.interviewType)
      known.push(`• Type d'entretien : ${profile.interviewType}`);
    if (profile.experienceLevel)
      known.push(`• Expérience : ${profile.experienceLevel}`);
    if (profile.skills?.length)
      known.push(`• Compétences : ${profile.skills.join(', ')}`);
    if (profile.industry) known.push(`• Secteur : ${profile.industry}`);

    return known.length > 0
      ? known.join('\n')
      : '• Aucune information spécifique pour le moment';
  }

  // 🆕 Méthode pour identifier les infos manquantes
  private getMissingInformation(profile: UserProfile): string[] {
    const missing: string[] = [];

    if (!profile.jobTitle) missing.push('Le poste que vous visez');
    if (!profile.interviewType)
      missing.push("Le type d'entretien (présentiel/en ligne)");
    if (!profile.experienceLevel) missing.push("Votre niveau d'expérience");
    if (!profile.skills?.length) missing.push('Vos compétences principales');

    return missing;
  }

  private async startInformationCollection(): Promise<ChatResponseDto> {
    // PROMPT AMÉLIORÉ avec instructions claires
    const prompt = `Tu es un coach expert en préparation aux entretiens. 

COMMENCE une conversation naturelle pour comprendre le profil du candidat.

**INSTRUCTIONS IMPORTANTES :**
- Pose UNE SEULE question à la fois
- Sois naturel et conversationnel
- Adapte tes questions aux réponses précédentes
- Ne répète jamais la même question
- Si le candidat semble impatient, propose de passer à l'étape suivante

Première question :`;

    try {
      const response = await this.geminiService.generateContent(prompt);
      return {
        response: this.cleanResponse(response),
        conversationPhase: 'collect_info',
        nextStep: 'collect_basic_info',
      };
    } catch (error) {
      // FALLBACK AMÉLIORÉ avec variété
      const fallbackQuestions = [
        '🎯 **Super ! Parlons de votre projet professionnel.**\n\nQuel type de poste visez-vous exactement ?',
        '🧑‍💼 **Excellent ! Pour vous préparer au mieux...**\n\nQuel est le poste qui vous intéresse ?',
        '💼 **Parfait ! Commençons par votre objectif.**\n\nQuel rôle souhaitez-vous obtenir ?',
      ];

      const randomQuestion =
        fallbackQuestions[Math.floor(Math.random() * fallbackQuestions.length)];

      return {
        response: randomQuestion,
        conversationPhase: 'collect_info',
        nextStep: 'collect_basic_info',
      };
    }
  }

  private async handleInformationCollection(
    message: string,
    context: ConversationContext,
    userProfile: UserProfile,
  ): Promise<ChatResponseDto> {
    // PROMPT D'ANALYSE AMÉLIORÉ avec gestion des répétitions
    const analysisPrompt = `ANALYSE ce message de candidat et son contexte:

MESSAGE: "${message}"

PROFIL ACTUEL:
- Poste: ${userProfile.jobTitle || 'Non spécifié'}
- Type entretien: ${userProfile.interviewType || 'Non spécifié'}
- Expérience: ${userProfile.experienceLevel || 'Non spécifié'}
- Compétences: ${userProfile.skills?.join(', ') || 'Aucune'}
- Secteur: ${userProfile.industry || 'Non spécifié'}

**INSTRUCTIONS CRITIQUES:**
1. Si le candidat dit avoir DÉJÀ RÉPONDU, propose de passer à l'étape suivante
2. Si le message contient des informations, extrais-les
3. Si le message est une question, réponds naturellement
4. NE DEMANDE PAS ce qui est DÉJÀ CONNU

Extraction des informations (si présentes):
- jobTitle, interviewType, experienceLevel, skills, industry, concerns

Réponds au format JSON:
{
  "extractedInfo": {
    "jobTitle": "string ou null",
    "interviewType": "presentiel/en_ligne/hybride ou null", 
    "experienceLevel": "string ou null",
    "skills": ["array de compétences"],
    "industry": "string ou null",
    "concerns": ["array de préoccupations"]
  },
  "isAskingQuestion": true/false,
  "userMessageType": "information" | "question" | "frustration" | "already_answered",
  "nextQuestion": "question adaptée et VARIÉE"
}`;

    try {
      const analysis = await this.geminiService.generateContent(analysisPrompt);
      const analysisData = this.parseAnalysis(analysis);

      console.log('Analysis Data:', analysisData);

      // GESTION AMÉLIORÉE selon le type de message
      switch (analysisData.userMessageType) {
        case 'already_answered':
        case 'frustration':
          return this.handleAlreadyAnswered(context, userProfile);

        case 'question':
          // Répondre à la question de l'utilisateur
          const answer = await this.answerUserQuestion(message);
          return {
            response: answer,
            conversationPhase: 'collect_info',
            userProfileUpdates: userProfile,
          };

        case 'information':
          // Mettre à jour le profil et continuer
          const updatedProfile = this.updateUserProfile(
            userProfile,
            analysisData.extractedInfo,
          );
          return await this.continueInformationCollection(
            updatedProfile,
            analysisData,
          );

        default:
          // Comportement par défaut
          const updatedProfileDefault = this.updateUserProfile(
            userProfile,
            analysisData.extractedInfo || {},
          );
          return await this.continueInformationCollection(
            updatedProfileDefault,
            analysisData,
          );
      }
    } catch (error) {
      console.error('Analysis Error:', error);
      // FALLBACK INTELLIGENT en cas d'erreur
      return this.intelligentFallback(message, userProfile);
    }
  }

  // 🆕 Continuation intelligente
  private async continueInformationCollection(
    profile: UserProfile,
    analysisData: any,
  ): Promise<ChatResponseDto> {
    const hasEnoughInfo = this.checkInformationCompleteness(profile);

    if (hasEnoughInfo) {
      return this.transitionToAdvicePhase(profile);
    } else {
      // QUESTION VARIÉE basée sur ce qui manque
      const nextQuestion =
        analysisData.nextQuestion || this.generateVariedNextQuestion(profile);

      return {
        response: nextQuestion,
        conversationPhase: 'collect_info',
        userProfileUpdates: profile,
        nextStep: 'continue_collection',
      };
    }
  }

  // 🆕 Questions variées
  private generateVariedNextQuestion(profile: UserProfile): string {
    const questions = {
      noJobTitle: [
        '🧑‍💼 **Quel poste visez-vous dans votre recherche ?**',
        '🎯 **Vers quel type de rôle souhaitez-vous vous orienter ?**',
        '💼 **Quelle fonction recherchez-vous exactement ?**',
        '🚀 **Quel est votre objectif professionnel actuel ?**',
      ],
      noInterviewType: [
        "💻 **S'agit-il d'un entretien en présentiel ou à distance ?**",
        "🎥 **L'entretien se passe-t-il en ligne ou en personne ?**",
        '🏢 **Est-ce un entretien en présentiel ou en visioconférence ?**',
        "📞 **L'entretien est-il prévu en physique ou par vidéo ?**",
      ],
      noExperience: [
        "📊 **Quel est votre niveau d'expérience dans ce domaine ?**",
        '⏳ **Depuis combien de temps travaillez-vous dans ce secteur ?**',
        '🎓 **Quelle est votre background professionnel ?**',
        '💼 **Pouvez-vous me décrire votre parcours professionnel ?**',
      ],
      noSkills: [
        '🛠️ **Quelles sont vos compétences principales ?**',
        '💪 **Sur quelles technologies ou savoir-faire êtes-vous fort ?**',
        '🌟 **Quels sont vos atouts professionnels ?**',
        '🔧 **Quelles sont vos expertises techniques ?**',
      ],
      noIndustry: [
        '🏢 **Dans quel secteur souhaitez-vous travailler ?**',
        "💼 **Quel type d'entreprise vous intéresse ?**",
        "🌐 **Quel domaine d'activité visez-vous ?**",
        '🚀 **Dans quelle industrie cherchez-vous à évoluer ?**',
      ],
    };

    if (!profile.jobTitle) {
      return questions.noJobTitle[
        Math.floor(Math.random() * questions.noJobTitle.length)
      ];
    }
    if (!profile.interviewType) {
      return questions.noInterviewType[
        Math.floor(Math.random() * questions.noInterviewType.length)
      ];
    }
    if (!profile.experienceLevel) {
      return questions.noExperience[
        Math.floor(Math.random() * questions.noExperience.length)
      ];
    }
    if (!profile.skills?.length) {
      return questions.noSkills[
        Math.floor(Math.random() * questions.noSkills.length)
      ];
    }
    if (!profile.industry) {
      return questions.noIndustry[
        Math.floor(Math.random() * questions.noIndustry.length)
      ];
    }

    return "💡 **Y a-t-il d'autres informations importantes que je devrais connaître ?**";
  }

  // 🆕 Répondre aux questions utilisateur
  private async answerUserQuestion(question: string): Promise<string> {
    const prompt = `En tant que coach en recrutement, réponds BRIÈVEMENT et UTILEMENT à cette question sur la préparation aux entretiens:

QUESTION: "${question}"

Réponds en 2-3 phrases maximum. Sois direct et pratique.`;

    try {
      const response = await this.geminiService.generateContent(prompt);
      return (
        this.cleanResponse(response) +
        '\n\n💡 **Maintenant, pourriez-vous me parler du poste que vous visez ?**'
      );
    } catch (error) {
      return '💡 Je suis là pour vous aider à préparer vos entretiens ! Pour commencer, pourriez-vous me parler du poste qui vous intéresse ?';
    }
  }

  // 🆕 Fallback intelligent
  private intelligentFallback(
    message: string,
    userProfile: UserProfile,
  ): ChatResponseDto {
    // Analyser le message basiquement
    const lowerMessage = message.toLowerCase();

    // Extraction basique des informations
    if (
      lowerMessage.includes('frontend') ||
      lowerMessage.includes('developer') ||
      lowerMessage.includes('développeur')
    ) {
      userProfile.jobTitle = 'Frontend Developer';
    }
    if (lowerMessage.includes('backend') || lowerMessage.includes('back-end')) {
      userProfile.jobTitle = 'Backend Developer';
    }
    if (
      lowerMessage.includes('fullstack') ||
      lowerMessage.includes('full-stack')
    ) {
      userProfile.jobTitle = 'Fullstack Developer';
    }
    if (
      lowerMessage.includes('en ligne') ||
      lowerMessage.includes('online') ||
      lowerMessage.includes('distanciel') ||
      lowerMessage.includes('visio')
    ) {
      userProfile.interviewType = 'en_ligne';
    }
    if (
      lowerMessage.includes('présentiel') ||
      lowerMessage.includes('presentiel') ||
      lowerMessage.includes('physique') ||
      lowerMessage.includes('bureau')
    ) {
      userProfile.interviewType = 'presentiel';
    }
    if (lowerMessage.includes('hybride') || lowerMessage.includes('mixte')) {
      userProfile.interviewType = 'hybride';
    }
    if (lowerMessage.includes('devops') || lowerMessage.includes('cloud')) {
      userProfile.industry = 'DevOps / Cloud';
    }
    if (lowerMessage.includes('startup') || lowerMessage.includes('jeune')) {
      userProfile.companyType = 'Startup';
    }
    if (
      lowerMessage.includes('entreprise') ||
      lowerMessage.includes('grande')
    ) {
      userProfile.companyType = 'Grande entreprise';
    }

    // Compétences basiques
    const skillsKeywords = [
      'javascript',
      'typescript',
      'react',
      'angular',
      'vue',
      'node',
      'python',
      'java',
      'html',
      'css',
      'sql',
      'nosql',
      'docker',
      'kubernetes',
      'aws',
    ];

    const detectedSkills = skillsKeywords.filter((skill) =>
      lowerMessage.includes(skill),
    );

    if (detectedSkills.length > 0) {
      userProfile.skills = [...(userProfile.skills || []), ...detectedSkills];
    }

    // Vérifier si on a des infos
    const hasSomeInfo =
      userProfile.jobTitle || userProfile.interviewType || userProfile.industry;

    if (hasSomeInfo) {
      return {
        response: `✅ **Merci ! J'ai noté :**\n${this.formatKnownInfo(userProfile)}\n\n📝 **Pour compléter votre profil, quelle est votre niveau d'expérience ?**`,
        conversationPhase: 'collect_info',
        userProfileUpdates: userProfile,
        nextStep: 'ask_experience',
      };
    } else {
      return {
        response:
          "🧑‍💼 **Je vois que vous préparez un entretien !**\n\nPour vous préparer au mieux, pourriez-vous me préciser :\n• **Le titre exact du poste**\n• **Le type d'entretien** (présentiel/en ligne)\n• **Votre niveau d'expérience**",
        conversationPhase: 'collect_info',
        userProfileUpdates: userProfile,
        nextStep: 'collect_details',
      };
    }
  }

  // 🆕 Méthode Parse Analysis améliorée
  private parseAnalysis(analysis: string): any {
    try {
      // Essayer de trouver du JSON dans la réponse
      const jsonMatch = analysis.match(/\{[\s\S]*\}/);
      if (jsonMatch) {
        const parsed = JSON.parse(jsonMatch[0]);

        // Validation basique de la structure
        if (parsed.extractedInfo && parsed.userMessageType) {
          return parsed;
        }
      }

      // Fallback si pas de JSON valide
      return {
        extractedInfo: {},
        userMessageType: 'information',
        nextQuestion: this.generateVariedNextQuestion({}),
      };
    } catch (error) {
      console.error('Parse Analysis Error:', error);
      return {
        extractedInfo: {},
        userMessageType: 'information',
        nextQuestion: this.generateVariedNextQuestion({}),
      };
    }
  }

  private async handleAdvicePhase(
    message: string,
    context: ConversationContext,
    userProfile: UserProfile,
  ): Promise<ChatResponseDto> {
    // Vérifier si l'utilisateur demande une simulation
    if (this.isAskingForPractice(message)) {
      return await this.transitionToPracticePhase(userProfile);
    }

    // Vérifier si l'utilisateur veut retourner à la collecte d'infos
    if (this.isAskingForProfileUpdate(message)) {
      return {
        response:
          '🔄 **Bien sûr ! Reprenons depuis le début.**\n\nParlez-moi du poste que vous visez actuellement :',
        conversationPhase: 'collect_info',
        userProfileUpdates: {},
        nextStep: 'restart_collection',
      };
    }

    // Donner des conseils personnalisés
    const advicePrompt = `En tant que coach en recrutement expérimenté, donne des conseils PERSONNALISÉS et ACTIONNABLES pour ce candidat:

PROFIL:
- Poste visé: ${userProfile.jobTitle || 'Non spécifié'}
- Type d'entretien: ${userProfile.interviewType || 'Non spécifié'}
- Expérience: ${userProfile.experienceLevel || 'Non spécifié'}
- Compétences: ${userProfile.skills?.join(', ') || 'Non spécifiées'}
- Secteur: ${userProfile.industry || 'Non spécifié'}
- Préoccupations: ${userProfile.specificConcerns?.join(', ') || 'Aucune mentionnée'}

QUESTION/DEMANDE: "${message}"

Donne des conseils:
1. Concrets et applicables immédiatement
2. Adaptés au type d'entretien et au secteur
3. Basés sur les bonnes pratiques du recrutement
4. Avec des exemples si pertinent

Sois direct, professionnel et motivant. Utilise des émojis modérément.

Termine en proposant une simulation d'entretien.`;

    try {
      const response = await this.geminiService.generateContent(advicePrompt);

      return {
        response: this.cleanResponse(response),
        conversationPhase: 'advice',
        suggestions: this.extractSuggestions(response),
        nextStep: 'offer_practice',
      };
    } catch (error) {
      return {
        response:
          "💡 **Conseils personnalisés :**\n\n• Adaptez votre discours au type d'entretien\n• Mettez en valeur vos compétences spécifiques\n• Préparez des exemples concrets de vos réalisations\n• Anticipez les questions difficiles\n\n🔄 **Prêt à faire une simulation pour vous entraîner ?**",
        conversationPhase: 'advice',
        nextStep: 'offer_practice',
      };
    }
  }

  private async handlePracticePhase(
    message: string,
    context: ConversationContext,
    userProfile: UserProfile,
  ): Promise<ChatResponseDto> {
    // Vérifier si c'est une réponse à une question d'entretien
    if (context.currentStep?.startsWith('question_')) {
      return await this.evaluatePracticeAnswer(message, context, userProfile);
    }

    // Vérifier si l'utilisateur veut arrêter la simulation
    if (this.isAskingToStopPractice(message)) {
      return {
        response:
          "🔄 **Simulation arrêtée.**\n\n💡 **Que souhaitez-vous faire maintenant ?**\n\n• 🎯 Recevoir d'autres conseils\n• 🔄 Modifier votre profil\n• 🎬 Recommencer une simulation",
        conversationPhase: 'advice',
        nextStep: 'practice_stopped',
      };
    }

    // Commencer une nouvelle session de simulation
    return await this.startPracticeSession(userProfile);
  }

  private async startPracticeSession(
    userProfile: UserProfile,
  ): Promise<ChatResponseDto> {
    const questionsPrompt = `Génère 5-6 questions d'entretien PERSONNALISÉES pour ce candidat:

POSTE: ${userProfile.jobTitle}
SECTEUR: ${userProfile.industry || 'Technologie'}
COMPÉTENCES: ${userProfile.skills?.join(', ') || 'Développement'}
TYPE ENTRE TIEN: ${userProfile.interviewType}
EXPÉRIENCE: ${userProfile.experienceLevel || 'Non spécifiée'}

Les questions doivent couvrir:
1. Présentation et motivation
2. Compétences techniques et expérience  
3. Situations professionnelles
4. Objectifs et ambitions
5. Adaptabilité et culture d'entreprise

Retourne uniquement les questions, une par ligne, sans numérotation. Sois spécifique au poste.`;

    try {
      const questionsResponse =
        await this.geminiService.generateContent(questionsPrompt);
      const questions = this.parseQuestions(questionsResponse);

      return {
        response: `🎬 **Simulation d'entretien lancée !**\n\nJe vais jouer le rôle du recruteur. Répondez naturellement comme en vrai entretien.\n\n💡 **Conseil :** Prenez votre temps, structurez vos réponses.\n\n**Première question :**\n\n${questions[0]}`,
        conversationPhase: 'practice',
        questions: questions,
        nextStep: 'question_1',
      };
    } catch (error) {
      const defaultQuestions = this.getDefaultPracticeQuestions(userProfile);

      return {
        response: `🎬 **Simulation d'entretien lancée !**\n\nJe vais jouer le rôle du recruteur. Répondez naturellement.\n\n💡 **Conseil :** Soyez authentique et précis.\n\n**Première question :**\n\n${defaultQuestions[0]}`,
        conversationPhase: 'practice',
        questions: defaultQuestions,
        nextStep: 'question_1',
      };
    }
  }

  private async evaluatePracticeAnswer(
    answer: string,
    context: ConversationContext,
    userProfile: UserProfile,
  ): Promise<ChatResponseDto> {
    const currentQuestionIndex =
      parseInt(context.currentStep?.split('_')[1] || '1') - 1;
    const questions = (context as any).questions || [];
    const currentQuestion = questions[currentQuestionIndex];

    const evaluationPrompt = `En tant que coach en recrutement, évalue cette réponse d'entretien:

QUESTION: "${currentQuestion}"
RÉPONSE: "${answer}"
POSTE VISÉ: ${userProfile.jobTitle}
TYPE ENTRE TIEN: ${userProfile.interviewType}

Donne un feedback CONCIS (3-4 phrases) qui:
1. ✅ Souligne 1-2 points positifs
2. 💡 Donne 1 suggestion d'amélioration spécifique  
3. 🎯 Propose une alternative plus efficace si pertinent
4. 📈 Encourage pour la suite

Sois constructif, professionnel et bienveillant.`;

    try {
      const feedback =
        await this.geminiService.generateContent(evaluationPrompt);

      // Passer à la question suivante ou terminer
      const nextQuestionIndex = currentQuestionIndex + 1;

      if (nextQuestionIndex < questions.length) {
        const nextQuestion = questions[nextQuestionIndex];

        return {
          response: `${this.cleanResponse(feedback)}\n\n**Question suivante :**\n\n${nextQuestion}`,
          conversationPhase: 'practice',
          nextStep: `question_${nextQuestionIndex + 1}`,
        };
      } else {
        return {
          response: `${this.cleanResponse(feedback)}\n\n🎉 **Simulation terminée !**\n\nVous avez répondu à toutes les questions. Voulez-vous:\n• 🔁 **Refaire une simulation**\n• 💡 **Recevoir d'autres conseils**\n• 📝 **Modifier votre profil**\n• ❓ **Poser une question spécifique**`,
          conversationPhase: 'practice',
          nextStep: 'session_complete',
        };
      }
    } catch (error) {
      // Fallback simple
      const nextQuestionIndex = currentQuestionIndex + 1;

      if (nextQuestionIndex < questions.length) {
        const nextQuestion = questions[nextQuestionIndex];

        return {
          response: `✅ **Bonne réponse !** Vous structurez bien vos idées.\n\n**Question suivante :**\n\n${nextQuestion}`,
          conversationPhase: 'practice',
          nextStep: `question_${nextQuestionIndex + 1}`,
        };
      } else {
        return {
          response: `✅ **Excellent ! Simulation terminée.**\n\nVous avez répondu à toutes les questions. Souhaitez-vous refaire une simulation ou recevoir d'autres conseils ?`,
          conversationPhase: 'practice',
          nextStep: 'session_complete',
        };
      }
    }
  }

  private async transitionToPracticePhase(
    profile: UserProfile,
  ): Promise<ChatResponseDto> {
    return await this.startPracticeSession(profile);
  }

  // Méthodes utilitaires
  private isAskingForPractice(message: string): boolean {
    const practiceKeywords = [
      'simulation',
      'entraînement',
      'pratique',
      'exercice',
      'répéter',
      "s'entraîner",
      'train',
      'practice',
    ];
    return practiceKeywords.some((keyword) =>
      message.toLowerCase().includes(keyword),
    );
  }

  private isAskingForProfileUpdate(message: string): boolean {
    const updateKeywords = [
      'changer',
      'modifier',
      'profil',
      'information',
      'corriger',
      'mettre à jour',
      'recommencer',
    ];
    return updateKeywords.some((keyword) =>
      message.toLowerCase().includes(keyword),
    );
  }

  private isAskingToStopPractice(message: string): boolean {
    const stopKeywords = [
      'stop',
      'arrêter',
      'arrête',
      'stoppe',
      'fin',
      'terminer',
      'assez',
    ];
    return stopKeywords.some((keyword) =>
      message.toLowerCase().includes(keyword),
    );
  }

  private updateUserProfile(
    currentProfile: UserProfile,
    extractedInfo: any,
  ): UserProfile {
    return {
      ...currentProfile,
      jobTitle: extractedInfo.jobTitle || currentProfile.jobTitle,
      interviewType:
        extractedInfo.interviewType || currentProfile.interviewType,
      experienceLevel:
        extractedInfo.experienceLevel || currentProfile.experienceLevel,
      skills: extractedInfo.skills || currentProfile.skills,
      industry: extractedInfo.industry || currentProfile.industry,
      specificConcerns:
        extractedInfo.concerns || currentProfile.specificConcerns,
    };
  }

  private checkInformationCompleteness(profile: UserProfile): boolean {
    // CRITÈRE PLUS FLEXIBLE
    const requiredFields = [
      profile.jobTitle,
      profile.interviewType,
      profile.experienceLevel,
    ];

    return requiredFields.filter(Boolean).length >= 2; // Au moins 2 sur 3
  }

  private transitionToAdvicePhase(profile: UserProfile): ChatResponseDto {
    return {
      response: `🎉 **Parfait ! J'ai maintenant une bonne vision de votre profil.**\n\n**Récapitulatif :**\n${this.formatKnownInfo(profile)}\n\n💡 **Maintenant, comment puis-je vous aider ?**\n\n• 🎯 **Conseils spécifiques** pour votre entretien ${profile.interviewType}\n• 💪 **Préparation technique** pour le poste de ${profile.jobTitle}\n• 🎬 **Simulation d'entretien** immédiate\n• ❓ **Réponses à vos questions** spécifiques`,
      conversationPhase: 'advice',
      userProfileUpdates: profile,
      nextStep: 'provide_advice',
    };
  }

  private parseQuestions(questionsResponse: string): string[] {
    return questionsResponse
      .split('\n')
      .filter((q) => q.trim().length > 10)
      .slice(0, 6)
      .map((q) =>
        q
          .trim()
          .replace(/^[•\-]\s*/, '')
          .replace(/^\d+\.\s*/, ''),
      );
  }

  private getDefaultPracticeQuestions(userProfile: UserProfile): string[] {
    const jobTitle = userProfile.jobTitle || 'développeur';

    return [
      `Pouvez-vous vous présenter et expliquer pourquoi vous êtes intéressé par ce poste de ${jobTitle} ?`,
      `Quelles sont vos compétences les plus pertinentes pour ce rôle de ${jobTitle} ?`,
      `Parlez-moi d'une réalisation dont vous êtes fier dans votre carrière.`,
      `Comment gérez-vous les défis ou les situations stressantes ?`,
      `Où vous voyez-vous dans les 3-5 prochaines années ?`,
      `Pourquoi pensez-vous être le meilleur candidat pour ce poste ?`,
    ];
  }

  private cleanResponse(response: string): string {
    return response
      .replace(/\*\*(.*?)\*\*/g, '**$1**') // Garder le gras
      .replace(/\*(.*?)\*/g, '*$1*') // Garder l'italique
      .trim();
  }

  private extractSuggestions(response: string): string[] {
    const suggestions: string[] = [];
    const lines = response.split('\n');

    lines.forEach((line) => {
      const cleanLine = line.trim();
      if (
        (cleanLine.startsWith('•') ||
          cleanLine.startsWith('-') ||
          cleanLine.match(/^\d+\./)) &&
        cleanLine.length > 10
      ) {
        suggestions.push(cleanLine);
      }
    });

    return suggestions.length > 0 ? suggestions : [];
  }
}
