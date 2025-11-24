import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JobCardRecruiter } from './job-card-recruiter';

describe('JobCardRecruiter', () => {
  let component: JobCardRecruiter;
  let fixture: ComponentFixture<JobCardRecruiter>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [JobCardRecruiter]
    })
    .compileComponents();

    fixture = TestBed.createComponent(JobCardRecruiter);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
