import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppliedJobCard } from './applied-job-card';

describe('AppliedJobCard', () => {
  let component: AppliedJobCard;
  let fixture: ComponentFixture<AppliedJobCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AppliedJobCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AppliedJobCard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
