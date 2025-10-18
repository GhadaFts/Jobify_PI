import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Landing } from './shared/landing/landing';
import { Login } from './shared/login/login';
import { Signup } from './shared/signup/signup';

const routes: Routes = [
   { path: '', component: Landing },
   { path: 'login', component: Login },
   { path: 'signup', component: Signup },
  { path: 'job-seeker', loadChildren: () => import('./job-seeker/job-seeker-module').then(m => m.JobSeekerModule) },
  { path: 'recruiter', loadChildren: () => import('./recruiter/recruiter-module').then(m => m.RecruiterModule) },
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }