// company-profile-dialog.component.ts
import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Recruiter } from '../../../../../types';

@Component({
  selector: 'app-company-profile-dialog',
  templateUrl: './company-profile-dialog.html',
  styleUrls: ['./company-profile-dialog.scss'],
  standalone: false
})
export class CompanyProfileDialog implements OnInit {
  companyName: string;
  recruiter: Recruiter | null = null;
  isLoading: boolean = true;

  constructor(
    public dialogRef: MatDialogRef<CompanyProfileDialog>,
    @Inject(MAT_DIALOG_DATA) public data: { companyName: string }
  ) {
    this.companyName = data.companyName;
  }

  ngOnInit() {
    // Simuler le chargement des données du recruteur
    this.loadRecruiterData();
  }

  private loadRecruiterData() {
    // Simulation de données - dans la réalité, vous feriez un appel API
    setTimeout(() => {
      this.recruiter = this.getMockRecruiterData(this.companyName);
      this.isLoading = false;
    }, 1000);
  }

  private getMockRecruiterData(companyName: string): Recruiter {
    // Données mockées basées sur le nom de l'entreprise
    const mockData: { [key: string]: Recruiter } = {
      'TekUp': {
        id: 1,
        email: 'contact@tekup.com',
        password: 'password',
        fullName: 'TekUp Recruiting Team',
        role: 'recruiter',
        photo_profil: 'https://images.unsplash.com/photo-1559136555-9303baea8ebd?w=400&h=400&fit=crop',
        twitter_link: 'https://twitter.com/tekup',
        web_link: 'https://tekup.com',
        github_link: 'https://github.com/tekup',
        facebook_link: 'https://facebook.com/tekup',
        description: 'Leading technology company specializing in web and mobile development solutions.',
        phone_number: '+216 70 000 000',
        nationality: 'Tunisian',
        companyAddress: 'Tunis Center, Tunisia',
        domaine: 'Information Technology',
        employees_number: 150,
        service: ['Web Development', 'Mobile Development', 'UI/UX Design', 'Digital Transformation']
      },
      'Tebourba Farming Coop': {
        id: 2,
        email: 'info@tebourbafarming.com',
        password: 'password',
        fullName: 'Tebourba Farming Management',
        role: 'recruiter',
        photo_profil: 'https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=400&h=400&fit=crop',
        twitter_link: '',
        web_link: 'https://tebourbafarming.com',
        github_link: '',
        facebook_link: 'https://facebook.com/tebourbafarming',
        description: 'Agricultural cooperative specializing in organic farming and sustainable agriculture practices.',
        phone_number: '+216 72 000 000',
        nationality: 'Tunisian',
        companyAddress: 'Tebourba, Mannouba, Tunisia',
        domaine: 'Agriculture',
        employees_number: 50,
        service: ['Organic Farming', 'Crop Production', 'Agricultural Consulting']
      },
      'Tebourba Secondary School': {
        id: 3,
        email: 'administration@tebourbaschool.edu.tn',
        password: 'password',
        fullName: 'School Administration',
        role: 'recruiter',
        photo_profil: 'https://images.unsplash.com/photo-1560452992-e3db5aa3c54e?w=400&h=400&fit=crop',
        twitter_link: '',
        web_link: '',
        github_link: '',
        facebook_link: '',
        description: 'Public secondary school committed to providing quality education in Tebourba region.',
        phone_number: '+216 71 000 000',
        nationality: 'Tunisian',
        companyAddress: 'Tebourba Center, Tunisia',
        domaine: 'Education',
        employees_number: 35,
        service: ['Secondary Education', 'Student Development', 'Community Education']
      }
    };

    return mockData[companyName] || this.getDefaultRecruiter(companyName);
  }

  private getDefaultRecruiter(companyName: string): Recruiter {
    return {
      id: 0,
      email: `contact@${companyName.toLowerCase().replace(/\s+/g, '')}.com`,
      password: 'password',
      fullName: `${companyName} Team`,
      role: 'recruiter',
      photo_profil: '',
      twitter_link: '',
      web_link: '',
      github_link: '',
      facebook_link: '',
      description: `Leading company in their field.`,
      phone_number: '+216 00 000 000',
      nationality: 'Tunisian',
      companyAddress: 'Tunisia',
      domaine: 'Various',
      employees_number: 25,
      service: ['Professional Services']
    };
  }

  closeDialog(): void {
    this.dialogRef.close();
  }
}