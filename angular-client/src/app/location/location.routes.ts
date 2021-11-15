import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { LocationSearchComponent } from './location-search/location-search.component';
import { LocationViewComponent } from './location-view/location-view.component';
import { LocationEditComponent } from './location-edit/location-edit.component';

const routes: Routes = [
  {
    path: '',
    component: LocationSearchComponent,
  },
  {
    path: ':id',
    component: LocationViewComponent,
  },
  {
    path: 'edit/:id',
    component: LocationEditComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LocationRoutingModule {}
