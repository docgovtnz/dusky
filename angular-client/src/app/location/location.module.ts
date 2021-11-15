import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { LocationEditComponent } from './location-edit/location-edit.component';
import { LocationViewComponent } from './location-view/location-view.component';
import { LocationPanelComponent } from './location-view/location-panel.component';
import { LocationSearchComponent } from './location-search/location-search.component';
import { LocationEditFormComponent } from './location-edit/location-edit-form.component';
import { NestDetailsPanelComponent } from './nestdetails-view/nestdetails-panel.component';
import { NestDetailsEditFormComponent } from './nestdetails-edit/nestdetails-edit-form.component';

import { LocationRoutingModule } from './location.routes';

import { MapModule } from '../map/map.module';
import { SharedModule } from '../shared.module';

@NgModule({
  imports: [CommonModule, SharedModule, MapModule, LocationRoutingModule],
  declarations: [
    LocationEditComponent,
    LocationEditFormComponent,
    LocationPanelComponent,
    LocationSearchComponent,
    LocationViewComponent,
    NestDetailsPanelComponent,
    NestDetailsEditFormComponent,
  ],
  exports: [CommonModule],
})
export class LocationModule {}
