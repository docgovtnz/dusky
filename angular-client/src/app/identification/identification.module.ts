import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { IdSearchComponent } from './identification-search/identification-search.component';

import { IdentificationRoutingModule } from './identification.routes';
import { SharedModule } from '../shared.module';

@NgModule({
  imports: [CommonModule, SharedModule, IdentificationRoutingModule],
  declarations: [IdSearchComponent],
  exports: [CommonModule],
})
export class IdentificationModule {}
