import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InterviewCard } from './interview-card';

describe('InterviewCard', () => {
  let component: InterviewCard;
  let fixture: ComponentFixture<InterviewCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [InterviewCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InterviewCard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
