import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { WeanedChickEditComponent } from './weanedchick-edit/weanedchick-edit.component';
import { FledgedChickEditFormComponent } from './fledgedchick-edit/fledgedchick-edit-form.component';
import { NestChickEditFormComponent } from './nestchick-edit/nestchick-edit-form.component';
import { NestEggEditFormComponent } from './nestegg-edit/nestegg-edit-form.component';
import { MotherTripEditFormComponent } from './mothertrip-edit/mothertrip-edit-form.component';
import { NestObservationViewComponent } from './nestobservation-view/nestobservation-view.component';
import { NestObservationSearchComponent } from './nestobservation-search/nestobservation-search.component';
import { NestObservationPanelComponent } from './nestobservation-view/nestobservation-panel.component';
import { NestObservationEditComponent } from './nestobservation-edit/nestobservation-edit.component';
import { NestObservationEditFormComponent } from './nestobservation-edit/nestobservation-edit-form.component';
import { NewEggEditFormComponent } from './newegg-edit/newegg-edit-form.component';
import { NewEggEditComponent } from './newegg-edit/newegg-edit.component';
import { FertileEggEditFormComponent } from './fertileegg-edit/fertileegg-edit-form.component';
import { FertileEggEditComponent } from './fertileegg-edit/fertileegg-edit.component';
import { InfertileEggEditComponent } from './infertileegg-edit/infertileegg-edit.component';
import { HatchEditComponent } from './hatch-edit/hatch-edit.component';
import { InfertileEggEditFormComponent } from './infertileegg-edit/infertileegg-edit-form.component';
import { HatchEditFormComponent } from './hatch-edit/hatch-edit-form.component';
import { DeadEmbryoEditComponent } from './deadembryo-edit/deadembryo-edit.component';
import { DeadEmbryoEditFormComponent } from './deadembryo-edit/deadembryo-edit-form.component';
import { FledgedChickEditComponent } from './fledgedchick-edit/fledgedchick-edit.component';
import { WeanedChickEditFormComponent } from './weanedchick-edit/weanedchick-edit-form.component';
import { MotherTripPanelComponent } from './mothertrip-view/mothertrip-panel.component';
import { NestEggPanelComponent } from './nestegg-view/nestegg-panel.component';
import { NestChickPanelComponent } from './nestchick-view/nestchick-panel.component';
import { ObservationTimesEditFormComponent } from './observationtimes-edit/observationtimes-edit-form.component';
import { ObservationTimesPanelComponent } from './observationtimes-view/observationtimes-panel.component';
import { NestChamberEditFormComponent } from './nestchamber-edit/nestchamber-edit-form.component';
import { NestChamberPanelComponent } from './nestchamber-view/nestchamber-panel.component';

import { NestObservationRoutingModule } from './nestobservation.routes';
import { SharedModule } from '../shared.module';
import { BirdModule } from '../bird/bird.module';

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    NestObservationRoutingModule,
    BirdModule,
  ],
  declarations: [
    NestObservationEditComponent,
    NestObservationEditFormComponent,
    NestObservationPanelComponent,
    NestObservationSearchComponent,
    NestObservationViewComponent,
    MotherTripEditFormComponent,
    NestEggEditFormComponent,
    NewEggEditComponent,
    NewEggEditFormComponent,
    FertileEggEditComponent,
    FertileEggEditFormComponent,
    InfertileEggEditComponent,
    InfertileEggEditFormComponent,
    HatchEditComponent,
    HatchEditFormComponent,
    DeadEmbryoEditComponent,
    DeadEmbryoEditFormComponent,
    NestChickEditFormComponent,
    FledgedChickEditComponent,
    FledgedChickEditFormComponent,
    WeanedChickEditComponent,
    WeanedChickEditFormComponent,
    MotherTripPanelComponent,
    NestEggPanelComponent,
    NestChickPanelComponent,
    ObservationTimesEditFormComponent,
    ObservationTimesPanelComponent,
    NestChamberEditFormComponent,
    NestChamberPanelComponent,
  ],
  exports: [CommonModule],
})
export class NestObservationModule {}
