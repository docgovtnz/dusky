import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ReportLandingComponent } from './report-landing.component';
import { ReportLocationComponent } from './/report-location/report-location.component';
import { ReportChecksheetComponent } from './report-checksheet/report-checksheet.component';
import { ReportMatingComponent } from './report-mating/report-mating.component';
import { ReportLatestWeightComponent } from './report-latest-weight/report-latest-weight.component';
import { ReportNoraNetErrorComponent } from './report-noranet-error/report-noranet-error.component';

import { ReportRoutingModule } from './report.routes';
import { SharedModule } from '../shared.module';

@NgModule({
  imports: [CommonModule, SharedModule, ReportRoutingModule],
  declarations: [
    ReportMatingComponent,
    ReportLatestWeightComponent,
    ReportLocationComponent,
    ReportLandingComponent,
    ReportChecksheetComponent,
    ReportNoraNetErrorComponent,
  ],
  exports: [CommonModule],
})
export class ReportModule {}
