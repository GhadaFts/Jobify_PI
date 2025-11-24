import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfileInitial } from './profile-initial';

describe('ProfileInitial', () => {
  let component: ProfileInitial;
  let fixture: ComponentFixture<ProfileInitial>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ProfileInitial]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProfileInitial);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
