import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Component, trigger, state, style, transition, animate } from '@angular/core';


import { ProfileInitial } from './profile-initial';

@Component({
  selector: 'app-profile-initial',
  templateUrl: './profile-initial.component.html',
  styleUrls: ['./profile-initial.component.css'],
  animations: [
    trigger('formAnimation', [
      state('void', style({ opacity: 0, transform: 'translateY(20px)' })),
      state('*', style({ opacity: 1, transform: 'translateY(0)' })),
      transition(':enter', [
        animate('0.5s ease-in-out')
      ]),
      transition(':leave', [
        animate('0.5s ease-in-out', style({ opacity: 0, transform: 'translateY(20px)' }))
      ])
    ])
  ]
})

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
