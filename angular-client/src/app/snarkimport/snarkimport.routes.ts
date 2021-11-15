import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { SnarkImportEditComponent } from './snarkimport-edit/snarkimport-edit.component';

const routes: Routes = [
  {
    path: 'edit/:id',
    component: SnarkImportEditComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SnarkImportRoutingModule {}
