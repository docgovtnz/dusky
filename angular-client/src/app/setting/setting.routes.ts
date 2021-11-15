import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { SettingsComponent } from './settings.component';
import { TxMortalityEditComponent } from './txmortality/txmortality-edit/txmortality-edit.component';
import { TxMortalityViewComponent } from './txmortality/txmortality-view/txmortality-view.component';
import { TxMortalitySearchComponent } from './txmortality/txmortality-search/txmortality-search.component';
import { IslandEditComponent } from './island/island-edit/island-edit.component';
import { IslandViewComponent } from './island/island-view/island-view.component';
import { IslandSearchComponent } from './island/island-search/island-search.component';
import { OptionListSearchComponent } from './optionlist/optionlist-search/optionlist-search.component';
import { OptionListNewComponent } from './optionlist/optionlist-new/option-list-new.component';

const routes: Routes = [
  {
    path: '',
    component: SettingsComponent,
  },
  {
    path: 'txmortality',
    component: TxMortalitySearchComponent,
  },
  {
    path: 'optionlist/:name/create',
    component: OptionListNewComponent,
  },
  {
    path: 'optionlist/:name',
    component: OptionListSearchComponent,
  },
  {
    path: 'txmortality/:id',
    component: TxMortalityViewComponent,
  },
  {
    path: 'txmortality/edit/:id',
    component: TxMortalityEditComponent,
  },
  {
    path: 'island',
    component: IslandSearchComponent,
  },
  {
    path: 'island/:id',
    component: IslandViewComponent,
  },
  {
    path: 'island/edit/:id',
    component: IslandEditComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SettingRoutingModule {}
