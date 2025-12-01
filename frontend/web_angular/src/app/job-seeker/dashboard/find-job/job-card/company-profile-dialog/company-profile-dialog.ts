import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { RecruiterService } from '../../../../../services/recruiter.service';
import { Recruiter } from '../../../../../types';

@Component({
  selector: 'app-company-profile-dialog',
  templateUrl: './company-profile-dialog.html',
  styleUrls: ['./company-profile-dialog.scss'],
  standalone: false
})
export class CompanyProfileDialog implements OnInit {
  companyName: string;
  recruiterId: string;
  recruiter: Recruiter | null = null;
  isLoading: boolean = true;
  errorMessage: string | null = null;

  constructor(
    public dialogRef: MatDialogRef<CompanyProfileDialog>,
    @Inject(MAT_DIALOG_DATA) public data: { companyName: string; recruiterId: string },
    private recruiterService: RecruiterService
  ) {
    this.companyName = data.companyName;
    this.recruiterId = data.recruiterId;
  }

  ngOnInit() {
    this.loadRecruiterData();
  }

  loadRecruiterData() {
    if (!this.recruiterId) {
      this.errorMessage = 'Recruiter information not available';
      this.isLoading = false;
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;
    console.log('🔍 Loading recruiter data for ID:', this.recruiterId);

    this.recruiterService.getRecruiterProfile(this.recruiterId).subscribe({
      next: (recruiter) => {
        console.log('✅ Recruiter data loaded:', recruiter);
        this.recruiter = recruiter;
        this.isLoading = false;
        this.errorMessage = null;
      },
      error: (error) => {
        console.error('❌ Failed to load recruiter data:', error);
        this.errorMessage = 'Failed to load company profile. Please try again.';
        this.isLoading = false;
        
        // Fallback to mock data for development
        if (error.status === 404 || error.status === 403) {
          console.warn('⚠️ Using fallback mock data');
          this.recruiter = this.getMockRecruiterData(this.companyName);
          this.errorMessage = null;
        }
      }
    });
  }

  private getMockRecruiterData(companyName: string): Recruiter {
    // Mock data as fallback (same as before)
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