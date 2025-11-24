import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecruiterJobDetailsDialog } from './recruiter-job-details-dialog';

describe('RecruiterJobDetailsDialog', () => {
  let component: RecruiterJobDetailsDialog;
  let fixture: ComponentFixture<RecruiterJobDetailsDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RecruiterJobDetailsDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RecruiterJobDetailsDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
