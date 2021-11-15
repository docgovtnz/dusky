import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FeedOutTargetsEditComponent } from './feedout-targets-edit/feedout-targets-edit.component';
import { FeedOutTallysEditComponent } from './feedout-tallys-edit/feedout-tallys-edit.component';
import { FeedOutSearchComponent } from './feedout-search/feedout-search.component';

import { FeedOutRoutingModule } from './feedout.routes';
import { SharedModule } from '../shared.module';

@NgModule({
  imports: [CommonModule, SharedModule, FeedOutRoutingModule],
  declarations: [
    FeedOutSearchComponent,
    FeedOutTargetsEditComponent,
    FeedOutTallysEditComponent,
  ],
  exports: [CommonModule],
})
export class FeedOutModule {}
