import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { IdSearchComponent } from './identification-search/identification-search.component';

const routes: Routes = [
  {
    path: '',
    component: IdSearchComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class IdentificationRoutingModule {}
