import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SampleSearchComponent } from './sample-search/sample-search.component';
import { SampleViewComponent } from './sample-view/sample-view.component';
import { SamplePanelComponent } from './sample-view/sample-panel.component';
import { SampleEditComponent } from './sample-edit/sample-edit.component';
import { SampleEditFormComponent } from './sample-edit/sample-edit-form.component';
import { BloodDetailPanelComponent } from './blooddetail-view/blooddetail-panel.component';
import { BloodDetailEditFormComponent } from './blooddetail-edit/blooddetail-edit-form.component';
import { SwabDetailPanelComponent } from './swabdetail-view/swabdetail-panel.component';
import { SwabDetailEditFormComponent } from './swabdetail-edit/swabdetail-edit-form.component';
import { OtherDetailPanelComponent } from './otherdetail-view/otherdetail-panel.component';
import { OtherDetailEditFormComponent } from './otherdetail-edit/otherdetail-edit-form.component';
import { SpermDetailPanelComponent } from './spermdetail-view/spermdetail-panel.component';
import { SpermDetailEditFormComponent } from './spermdetail-edit/spermdetail-edit-form.component';
import { ChemistryAssayPanelComponent } from './chemistryassay-view/chemistryassay-panel.component';
import { ChemistryAssayEditFormComponent } from './chemistryassay-edit/chemistryassay-edit-form.component';
import { ChemistryAssayStatsPanelComponent } from './chemistryassaystats-view/chemistryassaystats-panel.component';
import { HaematologyTestPanelComponent } from './haematologytest-view/haematologytest-panel.component';
import { HaematologyTestEditFormComponent } from './haematologytest-edit/haematologytest-edit-form.component';
import { HaematologyTestStatsPanelComponent } from './haematologyteststats-view/haematologyteststats-panel.component';
import { MicrobiologyAndParasitologyTestPanelComponent } from './microbiologyandparasitologytest-view/microbiologyandparasitologytest-panel.component';
import { MicrobiologyAndParasitologyTestEditFormComponent } from './microbiologyandparasitologytest-edit/microbiologyandparasitologytest-edit-form.component';
import { SpermMeasurePanelComponent } from './spermmeasure-view/spermmeasure-panel.component';
import { SpermMeasureEditFormComponent } from './spermmeasure-edit/spermmeasure-edit-form.component';
import { SampleResultAutofillComponent } from './sampleresult-autofill/sampleresult-autofill.component';

import { SampleRoutingModule } from './sample.routes';
import { SharedModule } from '../shared.module';

@NgModule({
  imports: [CommonModule, SharedModule, SampleRoutingModule],
  declarations: [
    SampleSearchComponent,
    SampleViewComponent,
    SamplePanelComponent,
    SampleEditComponent,
    SampleEditFormComponent,
    BloodDetailPanelComponent,
    BloodDetailEditFormComponent,
    SwabDetailPanelComponent,
    SwabDetailEditFormComponent,
    OtherDetailPanelComponent,
    OtherDetailEditFormComponent,
    SpermDetailPanelComponent,
    SpermDetailEditFormComponent,
    ChemistryAssayPanelComponent,
    ChemistryAssayEditFormComponent,
    ChemistryAssayStatsPanelComponent,
    HaematologyTestPanelComponent,
    HaematologyTestEditFormComponent,
    HaematologyTestStatsPanelComponent,
    MicrobiologyAndParasitologyTestPanelComponent,
    MicrobiologyAndParasitologyTestEditFormComponent,
    SpermMeasurePanelComponent,
    SpermMeasureEditFormComponent,
    SampleResultAutofillComponent,
  ],
  exports: [CommonModule],
})
export class SampleModule {}
