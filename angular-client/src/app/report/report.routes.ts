import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { ReportLandingComponent } from './report-landing.component';
import { ReportLocationComponent } from './report-location/report-location.component';
import { ReportChecksheetComponent } from './report-checksheet/report-checksheet.component';
import { ReportMatingComponent } from './report-mating/report-mating.component';
import { ReportLatestWeightComponent } from './report-latest-weight/report-latest-weight.component';
import { ReportNoraNetErrorComponent } from './report-noranet-error/report-noranet-error.component';

const routes: Routes = [
  {
    path: '',
    component: ReportLandingComponent,
  },
  {
    path: 'location',
    component: ReportLocationComponent,
  },
  {
    path: 'checksheet',
    component: ReportChecksheetComponent,
  },
  {
    path: 'mating',
    component: ReportMatingComponent,
  },
  {
    path: 'latestweight',
    component: ReportLatestWeightComponent,
  },
  {
    path: 'noraneterror',
    component: ReportNoraNetErrorComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ReportRoutingModule {}
