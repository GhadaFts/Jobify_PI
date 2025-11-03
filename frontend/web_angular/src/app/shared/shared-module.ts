import { NgModule } from '@angular/core';
import { CommonModule as AngularCommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Landing } from './landing/landing'; // Adjusted import
; // Adjusted import

import { Sidebar } from './sidebar/sidebar';

@NgModule({
  declarations: [Landing, Sidebar],
  imports: [AngularCommonModule, FormsModule],
  exports: [Landing, Sidebar] // Export for use in AppRoutingModule
})
export class SharedModule { }