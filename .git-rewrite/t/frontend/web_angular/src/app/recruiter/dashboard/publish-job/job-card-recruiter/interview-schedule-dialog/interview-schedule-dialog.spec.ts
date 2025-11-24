import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InterviewScheduleDialog } from './interview-schedule-dialog';

describe('InterviewScheduleDialog', () => {
  let component: InterviewScheduleDialog;
  let fixture: ComponentFixture<InterviewScheduleDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [InterviewScheduleDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InterviewScheduleDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
