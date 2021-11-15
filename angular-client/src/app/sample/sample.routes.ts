import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { SampleSearchComponent } from './sample-search/sample-search.component';
import { SampleViewComponent } from './sample-view/sample-view.component';
import { SampleEditComponent } from './sample-edit/sample-edit.component';

const routes: Routes = [
  {
    path: '',
    component: SampleSearchComponent,
  },
  {
    path: ':id',
    component: SampleViewComponent,
  },
  {
    path: 'edit/:id',
    component: SampleEditComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SampleRoutingModule {}
