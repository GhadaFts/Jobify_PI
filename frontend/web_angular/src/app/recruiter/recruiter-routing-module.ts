import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Recruiter } from './recruiter';
import { ProfileInitial } from './profile-initial/profile-initial';
import { RecruiterDashboard } from './dashboard/dashboard';
import { PublishJob } from './dashboard/publish-job/publish-job';
import { Interviews } from './dashboard/interviews/interviews';
import { EditProfileRecruiter } from './dashboard/edit-profile/edit-profile';
const routes: Routes = [
  { path: 'profile-initial', component: ProfileInitial },
  { 
    path: 'dashboard', 
    component: RecruiterDashboard, 
    children: [
      { path: 'publish-job', component: PublishJob },
      {path: 'interviews', component: Interviews },
      {path :'edit-profile', component: EditProfileRecruiter },
      // Vous pourrez ajouter d'autres routes plus tard
      { path: '', redirectTo: 'publish-job', pathMatch: 'full' }
    ]
  },
  { path: '', component: Recruiter }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RecruiterRoutingModule { }
