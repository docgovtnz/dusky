import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TransmitterSearchComponent } from './transmitter-search/transmitter-search.component';
import { TransmitterViewComponent } from './transmitter-view/transmitter-view.component';
import { TransmitterPanelComponent } from './transmitter-view/transmitter-panel.component';
import { TransmitterEditComponent } from './transmitter-edit/transmitter-edit.component';
import { TransmitterEditFormComponent } from './transmitter-edit/transmitter-edit-form.component';
import { TxMortalityComponent } from './tx-mortality-view/tx-mortality.component';
import { TransmitterMortalityEditComponent } from './tx-mortality-edit/tx-mortality-edit.component';
import { TransmitterBirdHistoryComponent } from './transmitter-view/transmitter-bird-history/transmitter-bird-history.component';

import { TransmitterRoutingModule } from './transmitter.routes';
import { SharedModule } from '../shared.module';

@NgModule({
  imports: [CommonModule, SharedModule, TransmitterRoutingModule],
  declarations: [
    TransmitterSearchComponent,
    TransmitterViewComponent,
    TransmitterPanelComponent,
    TransmitterEditComponent,
    TransmitterEditFormComponent,
    TxMortalityComponent,
    TransmitterMortalityEditComponent,
    TransmitterBirdHistoryComponent,
  ],
  exports: [CommonModule],
})
export class TransmitterModule {}
