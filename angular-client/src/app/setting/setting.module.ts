import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { IslandEditComponent } from './island/island-edit/island-edit.component';
import { IslandEditFormComponent } from './island/island-edit/island-edit-form.component';
import { IslandSearchComponent } from './island/island-search/island-search.component';
import { IslandPanelComponent } from './island/island-view/island-panel.component';
import { IslandViewComponent } from './island/island-view/island-view.component';
import { TxMortalityPanelComponent } from './txmortality/txmortality-view/txmortality-panel.component';
import { TxMortalitySearchComponent } from './txmortality/txmortality-search/txmortality-search.component';
import { TxMortalityEditComponent } from './txmortality/txmortality-edit/txmortality-edit.component';
import { TxMortalityViewComponent } from './txmortality/txmortality-view/txmortality-view.component';
import { TxMortalityEditFormComponent } from './txmortality/txmortality-edit/txmortality-edit-form.component';
import { OptionListSearchComponent } from './optionlist/optionlist-search/optionlist-search.component';
import { OptionListNewComponent } from './optionlist/optionlist-new/option-list-new.component';
import { OptionListNewFormComponent } from './optionlist/optionlist-new/option-list-new-form.component';

import { SettingsComponent } from './settings.component';
import { PasswordToggleComponent } from './password-toggle.component';

import { SettingRoutingModule } from './setting.routes';
import { SharedModule } from '../shared.module';

@NgModule({
  imports: [CommonModule, SharedModule, SettingRoutingModule],
  declarations: [
    IslandEditComponent,
    IslandEditFormComponent,
    IslandSearchComponent,
    IslandPanelComponent,
    IslandViewComponent,
    TxMortalityEditComponent,
    TxMortalityEditFormComponent,
    TxMortalityPanelComponent,
    TxMortalitySearchComponent,
    TxMortalityViewComponent,
    OptionListSearchComponent,
    OptionListNewComponent,
    OptionListNewFormComponent,
    SettingsComponent,
    PasswordToggleComponent,
  ],
  exports: [CommonModule],
})
export class SettingModule {}
