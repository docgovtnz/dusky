import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ThreeStateButtonComponent } from './three-state-button/three-state-button.component';
import { FullScreenToggleComponent } from './full-screen-toggle/full-screen-toggle.component';
import { BirdLabelToggleComponent } from './bird-label-toggle/bird-label-toggle.component';
import { BirdLegendComponent } from './bird-legend/bird-legend.component';
import { LocationMapComponent } from './location-map/location-map.component';
import { RecordMapComponent } from './record-map/record-map.component';
import { EsriMapComponent } from './esri-map.component';

import { SharedModule } from '../shared.module';

import { MapColorService } from './map-color.service';

@NgModule({
  imports: [CommonModule, SharedModule],
  declarations: [
    EsriMapComponent,
    LocationMapComponent,
    RecordMapComponent,
    BirdLegendComponent,
    ThreeStateButtonComponent,
    FullScreenToggleComponent,
    BirdLabelToggleComponent,
  ],
  providers: [MapColorService],
  exports: [
    CommonModule,
    EsriMapComponent,
    LocationMapComponent,
    RecordMapComponent,
    BirdLegendComponent,
    ThreeStateButtonComponent,
    FullScreenToggleComponent,
    BirdLabelToggleComponent,
  ],
})
export class MapModule {}
