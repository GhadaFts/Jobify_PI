import { Injectable } from '@angular/core';
import { JobSeeker, User } from '../types';

@Injectable({
  providedIn: 'root'
})
export class MockProfileService {
  
  /**
   * Génère un profil fictif basé sur le type de job
   */
  generateMockProfile(jobOffer: any): JobSeeker {
    const jobTitle = jobOffer.title.toLowerCase();
    
    // Profils prédéfinis par type de job
    const profiles: { [key: string]: () => JobSeeker } = {
      'developer': () => this.getDeveloperProfile(jobOffer),
      'engineer': () => this.getEngineerProfile(jobOffer),
      'designer': () => this.getDesignerProfile(jobOffer),
      'manager': () => this.getManagerProfile(jobOffer),
      'analyst': () => this.getAnalystProfile(jobOffer),
      'default': () => this.getDefaultProfile(jobOffer)
    };

    // Trouver le profil le plus approprié
    const profileKey = Object.keys(profiles).find(key => jobTitle.includes(key)) || 'default';
    return profiles[profileKey]();
  }

  /**
   * Crée un objet User de base avec toutes les propriétés requises
   */
  private createBaseUser(): User {
    return {
      id: 0,
      email: '',
      password: '',
      fullName: '',
      role: 'jobseeker',
      photo_profil: '',
      twitter_link: '',
      web_link: '',
      github_link: '',
      facebook_link: '',
      description: '',
      phone_number: '',
      nationality: ''
    };
  }

  /**
   * Crée un JobSeeker complet avec toutes les propriétés requises
   */
  private createJobSeeker(baseData: Partial<JobSeeker>): JobSeeker {
    const baseUser = this.createBaseUser();
    
    return {
      ...baseUser,
      skills: [],
      experience: [],
      education: [],
      title: '',
      date_of_birth: '',
      gender: '',
      ...baseData
    };
  }

  /**
   * Profil développeur
   */
  private getDeveloperProfile(jobOffer: any): JobSeeker {
    const skills = jobOffer.skills && jobOffer.skills.length > 0 
      ? jobOffer.skills 
      : ['JavaScript', 'TypeScript', 'Angular', 'Node.js', 'MongoDB', 'Git'];

    return this.createJobSeeker({
      id: 1001,
      fullName: 'Alexandre Martin',
      email: 'alexandre.martin@email.com',
      phone_number: '+216 12 345 678',
      title: 'Développeur Full Stack',
      description: 'Développeur passionné avec 4 ans d\'expérience dans la création d\'applications web modernes. Expertise en JavaScript, TypeScript et frameworks modernes.',
      nationality: 'Tunisien',
      skills: skills,
      experience: [
        {
          position: 'Développeur Full Stack',
          company: 'Tech Solutions SARL',
          startDate: '2020-03-01',
          endDate: '2024-01-01',
          description: 'Développement et maintenance d\'applications web avec Angular et Node.js. Implémentation de nouvelles fonctionnalités et optimisation des performances.'
        },
        {
          position: 'Développeur Frontend',
          company: 'Web Agency Tunis',
          startDate: '2018-06-01',
          endDate: '2020-02-28',
          description: 'Création d\'interfaces utilisateur responsive avec Angular et React. Collaboration avec les designers pour implémenter des maquettes pixel-perfect.'
        }
      ],
      education: [
        {
          degree: 'Master en Informatique',
          field: 'Génie Logiciel',
          school: 'Université Tunis El Manar',
          graduationDate: '2018-06-01'
        },
        {
          degree: 'Licence en Informatique',
          field: 'Informatique Fondamentale',
          school: 'Université de Tunis',
          graduationDate: '2016-06-01'
        }
      ],
      github_link: 'https://github.com/alexandremartin',
      web_link: 'https://alexandremartin.dev',
      date_of_birth: '1994-05-15',
      gender: 'Male',
      role: 'jobseeker',
      password: 'demo123', // Mot de passe fictif
      photo_profil: '/assets/default-avatar.png',
      twitter_link: 'https://twitter.com/alexandremartin',
      facebook_link: 'https://facebook.com/alexandremartin'
    });
  }

  /**
   * Profil ingénieur
   */
  private getEngineerProfile(jobOffer: any): JobSeeker {
    return this.createJobSeeker({
      id: 1002,
      fullName: 'Sophie Ben Ahmed',
      email: 'sophie.benahmed@email.com',
      phone_number: '+216 23 456 789',
      title: 'Ingénieur Logiciel',
      description: 'Ingénieur logiciel avec 5 ans d\'expérience en développement et architecture de systèmes. Spécialisée en solutions scalables et bonnes pratiques DevOps.',
      nationality: 'Tunisienne',
      skills: jobOffer.skills && jobOffer.skills.length > 0 ? jobOffer.skills : ['Java', 'Spring Boot', 'Microservices', 'Docker', 'AWS', 'SQL'],
      experience: [
        {
          position: 'Ingénieur Logiciel Senior',
          company: 'Software Engineering Corp',
          startDate: '2019-04-01',
          endDate: '2024-01-01',
          description: 'Conception et développement d\'architecture microservices. Mise en place de pipelines CI/CD et optimisation des performances systèmes.'
        },
        {
          position: 'Développeur Backend',
          company: 'Tech Innovations',
          startDate: '2017-02-01',
          endDate: '2019-03-31',
          description: 'Développement d\'APIs REST avec Spring Boot. Gestion de bases de données et intégration de services tiers.'
        }
      ],
      education: [
        {
          degree: 'Diplôme d\'Ingénieur',
          field: 'Informatique',
          school: 'École Nationale d\'Ingénieurs de Tunis',
          graduationDate: '2017-06-01'
        }
      ],
      github_link: 'https://github.com/sophieba',
      date_of_birth: '1993-08-22',
      gender: 'Female',
      role: 'jobseeker',
      password: 'demo123',
      photo_profil: '/assets/default-avatar.png',
      twitter_link: 'https://twitter.com/sophieba',
      facebook_link: 'https://facebook.com/sophieba'
    });
  }

  /**
   * Profil designer
   */
  private getDesignerProfile(jobOffer: any): JobSeeker {
    return this.createJobSeeker({
      id: 1003,
      fullName: 'Youssef Trabelsi',
      email: 'youssef.trabelsi@email.com',
      phone_number: '+216 34 567 890',
      title: 'UI/UX Designer',
      description: 'Designer créatif avec 3 ans d\'expérience en conception d\'interfaces utilisateur intuitives et expériences utilisateur optimisées.',
      nationality: 'Tunisien',
      skills: jobOffer.skills && jobOffer.skills.length > 0 ? jobOffer.skills : ['Figma', 'Adobe XD', 'Photoshop', 'Illustrator', 'Prototyping', 'User Research'],
      experience: [
        {
          position: 'UI/UX Designer',
          company: 'Digital Agency Tunis',
          startDate: '2021-01-01',
          endDate: '2024-01-01',
          description: 'Conception de maquettes et prototypes pour applications web et mobiles. Collaboration avec les développeurs pour assurer une implémentation fidèle.'
        },
        {
          position: 'Graphic Designer',
          company: 'Creative Studio',
          startDate: '2019-07-01',
          endDate: '2020-12-31',
          description: 'Création d\'identités visuelles et supports de communication pour divers clients.'
        }
      ],
      education: [
        {
          degree: 'Licence en Design Graphique',
          field: 'Arts et Design',
          school: 'Institut Supérieur des Beaux-Arts',
          graduationDate: '2019-06-01'
        }
      ],
      web_link: 'https://youssefdesign.dribbble.com',
      date_of_birth: '1996-11-30',
      gender: 'Male',
      role: 'jobseeker',
      password: 'demo123',
      photo_profil: '/assets/default-avatar.png',
      twitter_link: 'https://twitter.com/youssefdesign',
      facebook_link: 'https://facebook.com/youssefdesign'
    });
  }

  /**
   * Profil manager
   */
  private getManagerProfile(jobOffer: any): JobSeeker {
    return this.createJobSeeker({
      id: 1004,
      fullName: 'Leila Jlassi',
      email: 'leila.jlassi@email.com',
      phone_number: '+216 45 678 901',
      title: 'Project Manager',
      description: 'Chef de projet expérimentée avec 6 ans d\'expérience en gestion de projets IT. Expertise en méthodologies Agile et gestion d\'équipes cross-fonctionnelles.',
      nationality: 'Tunisienne',
      skills: jobOffer.skills && jobOffer.skills.length > 0 ? jobOffer.skills : ['Project Management', 'Agile', 'Scrum', 'JIRA', 'Team Leadership', 'Stakeholder Management'],
      experience: [
        {
          position: 'Chef de Projet IT',
          company: 'Solutions Entreprise SA',
          startDate: '2018-03-01',
          endDate: '2024-01-01',
          description: 'Gestion de projets digitaux de conception à la livraison. Coordination d\'équipes de développement et suivi des délais et budgets.'
        },
        {
          position: 'Assistante Chef de Projet',
          company: 'Tech Services',
          startDate: '2016-09-01',
          endDate: '2018-02-28',
          description: 'Support à la planification et exécution de projets. Préparation de rapports et suivi des indicateurs de performance.'
        }
      ],
      education: [
        {
          degree: 'Master en Management',
          field: 'Management de Projet',
          school: 'Institut des Hautes Études Commerciales',
          graduationDate: '2016-06-01'
        }
      ],
      date_of_birth: '1992-03-14',
      gender: 'Female',
      role: 'jobseeker',
      password: 'demo123',
      photo_profil: '/assets/default-avatar.png',
      twitter_link: 'https://twitter.com/leilajlassi',
      facebook_link: 'https://facebook.com/leilajlassi'
    });
  }

  /**
   * Profil analyste
   */
  private getAnalystProfile(jobOffer: any): JobSeeker {
    return this.createJobSeeker({
      id: 1005,
      fullName: 'Mehdi Karray',
      email: 'mehdi.karray@email.com',
      phone_number: '+216 56 789 012',
      title: 'Data Analyst',
      description: 'Analyste de données avec 4 ans d\'expérience en extraction, transformation et analyse de données pour soutenir la prise de décision business.',
      nationality: 'Tunisien',
      skills: jobOffer.skills && jobOffer.skills.length > 0 ? jobOffer.skills : ['SQL', 'Python', 'Tableau', 'Excel', 'Data Visualization', 'Statistical Analysis', 'Machine Learning', 'Power BI'],
      experience: [
        {
          position: 'Data Analyst',
          company: 'Data Insights Ltd',
          startDate: '2020-02-01',
          endDate: '2024-01-01',
          description: 'Analyse de données business et création de dashboards pour différents départements. Développement de modèles prédictifs et rapports automatisés.'
        },
        {
          position: 'Analyste Junior',
          company: 'Business Solutions',
          startDate: '2018-08-01',
          endDate: '2020-01-31',
          description: 'Extraction et nettoyage de données. Support à l\'équipe d\'analyse pour la préparation de rapports périodiques.'
        }
      ],
      education: [
        {
          degree: 'Master en Statistique',
          field: 'Analyse de Données',
          school: 'Faculté des Sciences de Tunis',
          graduationDate: '2018-06-01'
        }
      ],
      github_link: 'https://github.com/mehdikarray',
      date_of_birth: '1994-07-08',
      gender: 'Male',
      role: 'jobseeker',
      password: 'demo123',
      photo_profil: '/assets/default-avatar.png',
      twitter_link: 'https://twitter.com/mehdikarray',
      facebook_link: 'https://facebook.com/mehdikarray'
    });
  }

  /**
   * Profil par défaut
   */
  private getDefaultProfile(jobOffer: any): JobSeeker {
    return this.createJobSeeker({
      id: 1000,
      fullName: 'Mohamed Ali',
      email: 'mohamed.ali@email.com',
      phone_number: '+216 98 765 432',
      title: 'Professional',
      description: 'Professionnel motivé avec une solide expérience dans le domaine. Compétences diversifiées et capacité d\'adaptation aux nouveaux défis.',
      nationality: 'Tunisien',
      skills: jobOffer.skills && jobOffer.skills.length > 0 ? jobOffer.skills : ['Communication', 'Teamwork', 'Problem Solving', 'Adaptability', 'Time Management'],
      experience: [
        {
          position: 'Professional',
          company: 'Various Companies',
          startDate: '2018-01-01',
          endDate: '2024-01-01',
          description: 'Expérience professionnelle diversifiée avec responsabilités croissantes dans différents environnements de travail.'
        }
      ],
      education: [
        {
          degree: 'Licence',
          field: 'Sciences',
          school: 'Université de Tunis',
          graduationDate: '2018-06-01'
        }
      ],
      date_of_birth: '1995-01-01',
      gender: 'Male',
      role: 'jobseeker',
      password: 'demo123',
      photo_profil: '/assets/default-avatar.png',
      twitter_link: 'https://twitter.com/mohamedali',
      facebook_link: 'https://facebook.com/mohamedali'
    });
  }

  /**
   * Vérifie si un profil utilisateur est complet
   */
  isProfileComplete(profile: JobSeeker | null): boolean {
    if (!profile) return false;
    
    return !!(profile.fullName && 
              profile.email && 
              profile.skills && 
              profile.skills.length > 0 && 
              profile.experience && 
              profile.experience.length > 0);
  }

  /**
   * Obtient un profil pour l'utilisation (réel ou fictif)
   */
  getProfileForCV(jobOffer: any, actualProfile: JobSeeker | null): JobSeeker {
    if (this.isProfileComplete(actualProfile)) {
      console.log('Using actual user profile');
      return actualProfile!;
    } else {
      console.log('Using mock profile for job:', jobOffer.title);
      const mockProfile = this.generateMockProfile(jobOffer);
      console.log('Generated mock profile:', mockProfile);
      return mockProfile;
    }
  }
}