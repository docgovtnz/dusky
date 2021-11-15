import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UpgradeComponent } from './upgrade.component';
import { UpgradeCheckDialogComponent } from './upgrade-check-dialog/upgrade-check-dialog.component';

import { SharedModule } from '../shared.module';

@NgModule({
  imports: [CommonModule, SharedModule],
  declarations: [UpgradeComponent, UpgradeCheckDialogComponent],
  exports: [CommonModule, UpgradeCheckDialogComponent],
})
export class UpgradeModule {}
