import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { SharedModule} from './shared/shared-module';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Toast } from './toast/toast';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

@NgModule({
  declarations: [App, Toast],
  imports: [BrowserModule, AppRoutingModule, SharedModule, FontAwesomeModule, BrowserAnimationsModule],
  bootstrap: [App]
})
export class AppModule { }