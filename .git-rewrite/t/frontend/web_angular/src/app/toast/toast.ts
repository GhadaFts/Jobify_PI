import { Component, OnInit } from '@angular/core';
import { ToastService } from '../services/toast.service';
import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faXmark } from '@fortawesome/free-solid-svg-icons';
import { trigger, state, style, transition, animate } from '@angular/animations';

@Component({
  selector: 'app-toast',
  standalone: false,
  templateUrl: './toast.html',
  animations: [
    trigger('fadeAnimation', [
      state('in', style({ opacity: 1, transform: 'translateY(0)' })),
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(-10px)' }),
        animate('300ms ease-in')
      ]),
      transition(':leave', [
        animate('300ms ease-out', style({ opacity: 0, transform: 'translateY(-10px)' }))
      ])
    ])
  ]
})
export class Toast implements OnInit {
  toasts: { id: number; message: string; type: 'success' | 'error' }[] = [];
  faXmark = faXmark;

  constructor(private toastService: ToastService, private iconLibrary: FaIconLibrary) {
    this.iconLibrary.addIcons(faXmark);
  }

  ngOnInit() {
    this.toastService.toasts$.subscribe(toasts => {
      this.toasts = toasts;
    });
  }

  removeToast(id: number) {
    this.toastService.removeToast(id);
  }
}