import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { FeedOutSearchComponent } from './feedout-search/feedout-search.component';

const routes: Routes = [
  {
    path: '',
    component: FeedOutSearchComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class FeedOutRoutingModule {}
