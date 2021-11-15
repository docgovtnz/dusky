import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PersonSearchComponent } from './person-search/person-search.component';
import { PersonViewComponent } from './person-view/person-view.component';
import { PersonPanelComponent } from './person-view/person-panel.component';
import { PersonEditComponent } from './person-edit/person-edit.component';
import { ResetPasswordComponent } from './person-view/reset-password/reset-password.component';
import { PersonEditFormComponent } from './person-edit/person-edit-form.component';
import { ExpiredDateTickComponent } from './expired-date-tick/expired-date-tick.component';

import { PersonRoutingModule } from './person.routes';
import { SharedModule } from '../shared.module';

@NgModule({
  imports: [CommonModule, SharedModule, PersonRoutingModule],
  declarations: [
    PersonSearchComponent,
    PersonViewComponent,
    PersonPanelComponent,
    PersonEditComponent,
    PersonEditFormComponent,
    ExpiredDateTickComponent,
    ResetPasswordComponent,
  ],
  exports: [CommonModule],
})
export class PersonModule {}
