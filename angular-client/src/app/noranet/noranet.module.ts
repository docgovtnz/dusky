import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { NoraNetSearchComponent } from './noranet-search/noranet-search.component';
import { NoraNetDetectedPanelComponent } from './noranet-search/panels/noranet-detected.component';
import { NoraNetUndetectedPanelComponent } from './noranet-search/panels/noranet-undetected.component';

import { NoraNetViewComponent } from './noranet-view/noranet-view.component';
import { NoraNetPanelComponent } from './noranet-view/noranet-panel.component';
import { NoraNetDetectionPanelComponent } from './noranet-view/panels/noranet-detection.component';
import { NoraNetStandardPanelComponent } from './noranet-view/panels/noranet-standard.component';
import { NoraNetEggTimerPanelComponent } from './noranet-view/panels/noranet-eggtimer.component';
import { NoraNetCmLongPanelComponent } from './noranet-view/panels/noranet-cmlong.component';
import { NoraNetCmShortPanelComponent } from './noranet-view/panels/noranet-cmshort.component';

import { NoraNetEditComponent } from './noranet-edit/noranet-edit.component';
import { NoraNetEditFormComponent } from './noranet-edit/noranet-edit-form.component';
import { NoraNetDetectionEditPanelComponent } from './noranet-edit/panels/noranet-detection-edit.component';
import { NoraNetStandardEditPanelComponent } from './noranet-edit/panels/noranet-standard-edit.component';
import { NoraNetEggTimerEditPanelComponent } from './noranet-edit/panels/noranet-eggtimer-edit.component';
import { NoraNetCmLongEditPanelComponent } from './noranet-edit/panels/noranet-cmlong-edit.component';
import { NoraNetCmShortEditPanelComponent } from './noranet-edit/panels/noranet-cmshort-edit.component';

import { NoraNetSnarkSearchComponent } from './noranetsnark-search/noranetsnark-search.component';
import { NoraNetStationSearchComponent } from './noranetstation-search/noranetstation-search.component';

import { NoraNetBirdSelectMultiComponent } from './noranet-bird-select-multi/noranet-bird-select-multi.component';

import { NoraNetRoutingModule } from './noranet.routes';
import { SharedModule } from '../shared.module';

@NgModule({
  imports: [CommonModule, SharedModule, NoraNetRoutingModule],
  declarations: [
    NoraNetSearchComponent,
    NoraNetDetectedPanelComponent,
    NoraNetUndetectedPanelComponent,
    NoraNetViewComponent,
    NoraNetPanelComponent,
    NoraNetDetectionPanelComponent,
    NoraNetStandardPanelComponent,
    NoraNetEggTimerPanelComponent,
    NoraNetCmShortPanelComponent,
    NoraNetCmLongPanelComponent,
    NoraNetEditComponent,
    NoraNetEditFormComponent,
    NoraNetDetectionEditPanelComponent,
    NoraNetStandardEditPanelComponent,
    NoraNetEggTimerEditPanelComponent,
    NoraNetCmLongEditPanelComponent,
    NoraNetCmShortEditPanelComponent,
    NoraNetSnarkSearchComponent,
    NoraNetStationSearchComponent,
    NoraNetBirdSelectMultiComponent,
  ],
  exports: [CommonModule],
})
export class NoraNetModule {}
