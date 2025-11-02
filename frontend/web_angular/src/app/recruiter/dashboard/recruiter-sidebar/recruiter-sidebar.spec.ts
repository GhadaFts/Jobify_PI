import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecruiterSidebar } from './recruiter-sidebar';

describe('RecruiterSidebar', () => {
  let component: RecruiterSidebar;
  let fixture: ComponentFixture<RecruiterSidebar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RecruiterSidebar]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RecruiterSidebar);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
