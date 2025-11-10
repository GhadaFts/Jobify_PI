import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TakeActionDialog } from './take-action-dialog';

describe('TakeActionDialog', () => {
  let component: TakeActionDialog;
  let fixture: ComponentFixture<TakeActionDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TakeActionDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TakeActionDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
