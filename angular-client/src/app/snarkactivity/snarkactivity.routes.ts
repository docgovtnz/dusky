import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { SnarkActivitySearchComponent } from './snarkactivity-search/snarkactivity-search.component';
import { SnarkActivityViewComponent } from './snarkactivity-view/snarkactivity-view.component';
import { SnarkActivityEditComponent } from './snarkactivity-edit/snarkactivity-edit.component';

const routes: Routes = [
  {
    path: '',
    component: SnarkActivitySearchComponent,
  },
  {
    path: ':id',
    component: SnarkActivityViewComponent,
  },
  {
    path: 'edit/:id',
    component: SnarkActivityEditComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SnarkActivityRoutingModule {}
