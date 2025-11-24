import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JobAnalyse } from './job-analyse';

describe('JobAnalyse', () => {
  let component: JobAnalyse;
  let fixture: ComponentFixture<JobAnalyse>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [JobAnalyse]
    })
    .compileComponents();

    fixture = TestBed.createComponent(JobAnalyse);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
