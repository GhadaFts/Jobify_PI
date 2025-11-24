import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PublishJob } from './publish-job';

describe('PublishJob', () => {
  let component: PublishJob;
  let fixture: ComponentFixture<PublishJob>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PublishJob]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PublishJob);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
