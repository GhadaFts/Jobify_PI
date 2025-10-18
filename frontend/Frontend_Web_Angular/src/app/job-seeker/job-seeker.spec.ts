import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JobSeeker } from './job-seeker';

describe('JobSeeker', () => {
  let component: JobSeeker;
  let fixture: ComponentFixture<JobSeeker>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [JobSeeker]
    })
    .compileComponents();

    fixture = TestBed.createComponent(JobSeeker);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
