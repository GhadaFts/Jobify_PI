import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompanyProfileDialog } from './company-profile-dialog';

describe('CompanyProfileDialog', () => {
  let component: CompanyProfileDialog;
  let fixture: ComponentFixture<CompanyProfileDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CompanyProfileDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompanyProfileDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
