import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { PersonSearchComponent } from './person-search/person-search.component';
import { PersonViewComponent } from './person-view/person-view.component';
import { PersonEditComponent } from './person-edit/person-edit.component';

const routes: Routes = [
  {
    path: '',
    component: PersonSearchComponent,
  },
  {
    path: ':id',
    component: PersonViewComponent,
  },
  {
    path: 'edit/:id',
    component: PersonEditComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PersonRoutingModule {}
