import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { NoraNetEditComponent } from './noranet-edit/noranet-edit.component';

import { NoraNetSearchComponent } from './noranet-search/noranet-search.component';
import { NoraNetViewComponent } from './noranet-view/noranet-view.component';

import { NoraNetSnarkSearchComponent } from './noranetsnark-search/noranetsnark-search.component';
import { NoraNetStationSearchComponent } from './noranetstation-search/noranetstation-search.component';

const routes: Routes = [
  {
    path: '',
    component: NoraNetSearchComponent,
  },
  {
    path: 'snark',
    component: NoraNetSnarkSearchComponent,
  },
  {
    path: 'station',
    component: NoraNetStationSearchComponent,
  },
  {
    path: ':id',
    component: NoraNetViewComponent,
  },
  {
    path: 'edit/:id',
    component: NoraNetEditComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class NoraNetRoutingModule {}
