import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  private toastsSubject = new BehaviorSubject<{ id: number; message: string; type: 'success' | 'error' }[]>([]);
  toasts$ = this.toastsSubject.asObservable();
  private idCounter = 0;

  success(message: string) {
    this.addToast(message, 'success');
  }

  error(message: string) {
    this.addToast(message, 'error');
  }

  private addToast(message: string, type: 'success' | 'error') {
    const id = this.idCounter++;
    const toasts = [...this.toastsSubject.value, { id, message, type }];
    this.toastsSubject.next(toasts);

    // Auto-remove after 5 seconds
    setTimeout(() => {
      this.removeToast(id);
    }, 5000);
  }

  removeToast(id: number) {
    const toasts = this.toastsSubject.value.filter(toast => toast.id !== id);
    this.toastsSubject.next(toasts);
  }
}