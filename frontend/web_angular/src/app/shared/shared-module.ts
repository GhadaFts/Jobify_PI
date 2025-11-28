import { NgModule } from '@angular/core';
import { CommonModule as AngularCommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Landing } from './landing/landing'; 
import { RouterModule } from '@angular/router';

import { Sidebar } from './sidebar/sidebar';
import { UnauthorizedComponent } from './unauthorized-component/unauthorized-component';

@NgModule({
  declarations: [Landing, Sidebar, UnauthorizedComponent],
  imports: [AngularCommonModule, FormsModule, RouterModule],
  exports: [Landing, Sidebar, RouterModule] // Export for use in AppRoutingModule
})
export class SharedModule { }