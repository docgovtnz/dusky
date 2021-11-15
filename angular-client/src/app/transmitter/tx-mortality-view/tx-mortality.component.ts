import { isNullOrUndefined } from 'util';

import { Component, Input, OnInit } from '@angular/core';

import { TxMortalityEntity } from '../../domain/txmortality.entity';
import { TxMortalityService } from '../../setting/txmortality/txmortality.service';

@Component({
  selector: 'app-tx-mortality',
  templateUrl: './tx-mortality.component.html',
})
export class TxMortalityComponent implements OnInit {
  _txMort: string;

  txMortEntity: TxMortalityEntity = new TxMortalityEntity();

  constructor(private service: TxMortalityService) {}

  ngOnInit() {}

  @Input()
  set txMort(value: string) {
    this._txMort = value;
    this.txMortEntity = null;
    if (!isNullOrUndefined(value)) {
      this.service
        .findById(value)
        .subscribe((result: TxMortalityEntity) => (this.txMortEntity = result));
    }
  }

  get txMort() {
    return this._txMort;
  }
}
