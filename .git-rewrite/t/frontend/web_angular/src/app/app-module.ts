import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http'; // ✅ Add this
import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { SharedModule } from './shared/shared-module';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Toast } from './toast/toast';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

// ✅ Import your standalone components
import { LoginComponent } from './shared/login/login';
import { SignupComponent } from './shared/signup/signup';

@NgModule({
  declarations: [App, Toast],
  imports: [
    BrowserModule,
    HttpClientModule, // ✅ Add HttpClientModule for HTTP requests
    AppRoutingModule,
    SharedModule,
    FontAwesomeModule, 
    BrowserAnimationsModule,
    LoginComponent, // ✅ Import standalone login component
    SignupComponent // ✅ Import standalone signup component
  ],
  bootstrap: [App]
})
export class AppModule { }