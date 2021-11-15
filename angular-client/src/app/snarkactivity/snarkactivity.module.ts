import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SnarkRecordPanelComponent } from './snarkrecord-view/snarkrecord-panel.component';
import { SnarkRecordEditFormComponent } from './snarkrecord-edit/snarkrecord-edit-form.component';
import { SnarkActivityPanelComponent } from './snarkactivity-view/snarkactivity-panel.component';
import { SnarkActivityEditFormComponent } from './snarkactivity-edit/snarkactivity-edit-form.component';
import { SnarkActivityEditComponent } from './snarkactivity-edit/snarkactivity-edit.component';
import { TrackAndBowlActivityPanelComponent } from './trackandbowlactivity-view/trackandbowlactivity-panel.component';
import { TrackAndBowlActivityEditFormComponent } from './trackandbowlactivity-edit/trackandbowlactivity-edit-form.component';
import { SnarkActivitySearchComponent } from './snarkactivity-search/snarkactivity-search.component';
import { SnarkActivityViewComponent } from './snarkactivity-view/snarkactivity-view.component';
import { LinkListViewComponent } from './link-list-view/link-list-view.component';

import { SnarkActivityRoutingModule } from './snarkactivity.routes';
import { SharedModule } from '../shared.module';

@NgModule({
  imports: [CommonModule, SharedModule, SnarkActivityRoutingModule],
  declarations: [
    SnarkActivityEditComponent,
    SnarkActivityEditFormComponent,
    SnarkActivityPanelComponent,
    TrackAndBowlActivityEditFormComponent,
    TrackAndBowlActivityPanelComponent,
    SnarkRecordEditFormComponent,
    SnarkRecordPanelComponent,
    SnarkActivitySearchComponent,
    SnarkActivityViewComponent,
    LinkListViewComponent,
  ],
  exports: [CommonModule],
})
export class SnarkActivityModule {}
