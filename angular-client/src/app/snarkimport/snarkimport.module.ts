import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SnarkCheckResultPanelComponent } from './snarkcheckresult-panel/snarkcheckresult-panel.component';
import { SnarkImportEditFormComponent } from './snarkimport-edit/snarkimport-edit-form.component';
import { SnarkImportEditComponent } from './snarkimport-edit/snarkimport-edit.component';
import { SnarkIncludeResultPanelComponent } from './snarkincluderesult-panel/snarkincluderesult-panel.component';
import { BirdSelectComponent } from './bird-select/bird-select.component';

import { SnarkImportRoutingModule } from './snarkimport.routes';
import { SharedModule } from '../shared.module';

@NgModule({
  imports: [CommonModule, SharedModule, SnarkImportRoutingModule],
  declarations: [
    SnarkImportEditComponent,
    SnarkImportEditFormComponent,
    SnarkCheckResultPanelComponent,
    SnarkIncludeResultPanelComponent,
    BirdSelectComponent,
  ],
  exports: [CommonModule],
})
export class SnarkImportModule {}
