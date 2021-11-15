import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { TxMortalityEntity } from '../../domain/txmortality.entity';
import { TxMortalityService } from '../../setting/txmortality/txmortality.service';

@Component({
  selector: 'app-transmitter-mortality-edit',
  templateUrl: './tx-mortality-edit.component.html',
})
export class TransmitterMortalityEditComponent implements OnInit {
  // New Form layout
  @Input()
  myFormGroup: FormGroup;

  _txMortalityID: string;

  mortalityList: TxMortalityEntity[] = [];

  beatsMin: number;
  hoursTillMort: number;

  constructor(private txMortalityService: TxMortalityService) {}

  ngOnInit() {}

  @Input()
  set txMortalityID(id: string) {
    this._txMortalityID = id;
    this.txMortalityService.getAllTxMortalityOptions().subscribe((results) => {
      this.mortalityList = results;
      this.onMortalitySelected(this._txMortalityID);
    });
  }

  get txMortaltiyID(): string {
    return this._txMortalityID;
  }

  onMortalitySelected(item: any) {
    const tx = this.getMortalityFromList(item);
    this.beatsMin = tx ? tx.normalBpm : null;
    this.hoursTillMort = tx ? tx.hoursTilMort : null;
  }

  getMortalityFromList(id: string): TxMortalityEntity {
    return this.mortalityList.find((t) => t.id === id);
  }
}
