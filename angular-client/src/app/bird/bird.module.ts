import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { EggMeasurementsPanelComponent } from './eggmeasurements-view/eggmeasurements-panel.component';
import { EmbryoMeasurementsEditFormComponent } from './embryomeasurements-edit/embryomeasurements-edit-form.component';
import { EggMeasurementsEditFormComponent } from './eggmeasurements-edit/eggmeasurements-edit-form.component';
import { BirdSearchComponent } from './bird-search/bird-search.component';
import { BirdViewComponent } from './bird-view/bird-view.component';
import { BirdPanelComponent } from './bird-view/bird-panel.component';
import { BirdEditComponent } from './bird-edit/bird-edit.component';
import { WeightChartComponent } from './bird-view/weight/weight-chart.component';
import { BirdAgeLabelComponent } from './bird-age-label/bird-age-label.component';
import { AgeLabelComponent } from './age-label/age-label.component';
import { BirdAgeClassLabelComponent } from './bird-age-class-label/bird-age-class-label.component';
import { BirdEditFormComponent } from './bird-edit/bird-edit-form.component';
import { BandHistoryComponent } from './bird-view/band-history/band-history.component';
import { ChipHistoryComponent } from './bird-view/chip-history/chip-history.component';
import { MorphometricsComponent } from './bird-view/morphometrics/morphometrics.component';
import { EggWeightChartComponent } from './bird-view/weight/egg-weight-chart.component';
import { EmbryoMeasurementsPanelComponent } from './embryomeasurements-view/embryomeasurements-panel.component';
import { MultiBirdWeightChartComponent } from './bird-view/weight/multi-bird-weight-chart.component';
import { MultiBirdEggWeightChartComponent } from './bird-view/weight/multi-bird-egg-weight-chart.component';
import { BirdLocationMapComponent } from './bird-view/bird-location-map/bird-location-map.component';
import { TransmitterHistoryComponent } from './bird-view/transmitter-history/transmitter-history.component';

import { BirdRoutingModule } from './bird.routes';

import { MapModule } from '../map/map.module';
import { SharedModule } from '../shared.module';

@NgModule({
  imports: [CommonModule, SharedModule, MapModule, BirdRoutingModule],
  declarations: [
    BirdAgeLabelComponent,
    AgeLabelComponent,
    BirdAgeClassLabelComponent,
    BirdSearchComponent,
    BirdViewComponent,
    BirdPanelComponent,
    BirdEditComponent,
    BirdEditFormComponent,
    BandHistoryComponent,
    ChipHistoryComponent,
    MorphometricsComponent,
    TransmitterHistoryComponent,
    WeightChartComponent,
    BirdLocationMapComponent,
    EggMeasurementsEditFormComponent,
    EggMeasurementsPanelComponent,
    EggWeightChartComponent,
    EmbryoMeasurementsPanelComponent,
    EmbryoMeasurementsEditFormComponent,
    MultiBirdWeightChartComponent,
    MultiBirdEggWeightChartComponent,
  ],
  exports: [
    CommonModule,
    EggMeasurementsEditFormComponent,
    EmbryoMeasurementsEditFormComponent,
  ],
})
export class BirdModule {}
