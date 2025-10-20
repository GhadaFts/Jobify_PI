import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CvCorrection } from './cv-correction';

describe('CvCorrection', () => {
  let component: CvCorrection;
  let fixture: ComponentFixture<CvCorrection>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CvCorrection]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CvCorrection);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
