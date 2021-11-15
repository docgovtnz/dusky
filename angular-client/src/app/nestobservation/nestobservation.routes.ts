import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { NestObservationSearchComponent } from './nestobservation-search/nestobservation-search.component';
import { NestObservationViewComponent } from './nestobservation-view/nestobservation-view.component';
import { NestObservationEditComponent } from './nestobservation-edit/nestobservation-edit.component';

const routes: Routes = [
  {
    path: '',
    component: NestObservationSearchComponent,
  },
  {
    path: ':id',
    component: NestObservationViewComponent,
  },
  {
    path: 'edit/:id',
    component: NestObservationEditComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class NestObservationRoutingModule {}
