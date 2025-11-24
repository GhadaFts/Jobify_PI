import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditJobDialog } from './edit-job-dialog';

describe('EditJobDialog', () => {
  let component: EditJobDialog;
  let fixture: ComponentFixture<EditJobDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditJobDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditJobDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
