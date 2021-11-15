import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { TransmitterEditComponent } from './transmitter-edit/transmitter-edit.component';
import { TransmitterViewComponent } from './transmitter-view/transmitter-view.component';
import { TransmitterSearchComponent } from './transmitter-search/transmitter-search.component';

const routes: Routes = [
  {
    path: '',
    component: TransmitterSearchComponent,
  },
  {
    path: ':id',
    component: TransmitterViewComponent,
  },
  {
    path: 'edit/:id',
    component: TransmitterEditComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TransmitterRoutingModule {}
