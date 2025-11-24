import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FindJob } from './find-job';

describe('FindJob', () => {
  let component: FindJob;
  let fixture: ComponentFixture<FindJob>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FindJob]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FindJob);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
