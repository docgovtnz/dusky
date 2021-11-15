import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { RecordSearchComponent } from './record-search/record-search.component';
import { RecordViewComponent } from './record-view/record-view.component';
import { RecordEditComponent } from './record-edit/record-edit.component';
import { BulkSignalEntryEditComponent } from './bulksignalentry-edit/bulksignalentry-edit.component';

const routes: Routes = [
  {
    path: '',
    component: RecordSearchComponent,
  },
  {
    path: 'bulksignalentry',
    component: BulkSignalEntryEditComponent,
  },
  {
    path: ':id',
    component: RecordViewComponent,
  },
  {
    path: 'edit/:id',
    component: RecordEditComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class RecordRoutingModule {}
