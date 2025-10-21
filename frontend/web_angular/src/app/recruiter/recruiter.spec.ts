import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Recruiter } from './recruiter';

describe('Recruiter', () => {
  let component: Recruiter;
  let fixture: ComponentFixture<Recruiter>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [Recruiter]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Recruiter);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
