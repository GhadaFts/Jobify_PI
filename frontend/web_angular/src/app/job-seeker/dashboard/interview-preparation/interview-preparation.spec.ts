import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InterviewPreparation } from './interview-preparation';

describe('InterviewPreparation', () => {
  let component: InterviewPreparation;
  let fixture: ComponentFixture<InterviewPreparation>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [InterviewPreparation]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InterviewPreparation);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
