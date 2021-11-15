import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { BirdSearchComponent } from './bird-search/bird-search.component';
import { BirdViewComponent } from './bird-view/bird-view.component';
import { BirdEditComponent } from './bird-edit/bird-edit.component';
import { MultiBirdWeightChartComponent } from './bird-view/weight/multi-bird-weight-chart.component';
import { MultiBirdEggWeightChartComponent } from './bird-view/weight/multi-bird-egg-weight-chart.component';

const routes: Routes = [
  { path: '', component: BirdSearchComponent },
  {
    path: 'multiweightgraph',
    component: MultiBirdWeightChartComponent,
  },
  {
    path: 'multieggweightgraph',
    component: MultiBirdEggWeightChartComponent,
  },
  { path: ':id', component: BirdViewComponent },
  {
    path: 'edit/:id',
    component: BirdEditComponent,
  },
  {
    path: ':id/:idSearch',
    component: BirdViewComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class BirdRoutingModule {}
