import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Landing } from './shared/landing/landing';
import { LoginComponent } from './shared/login/login';
import { SignupComponent } from './shared/signup/signup';
import { UnauthorizedComponent } from './shared/unauthorized-component/unauthorized-component';
const routes: Routes = [
   { path: '', component: Landing },
   { path: 'login', component: LoginComponent },
   { path: 'signup', component: SignupComponent },
  { path: 'job-seeker', loadChildren: () => import('./job-seeker/job-seeker-module').then(m => m.JobSeekerModule) },
  { path: 'recruiter', loadChildren: () => import('./recruiter/recruiter-module').then(m => m.RecruiterModule) },
  { path: 'admin', loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule) },
  { path: '**', component: UnauthorizedComponent },
  { path: 'unauthorized', component: UnauthorizedComponent },

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }