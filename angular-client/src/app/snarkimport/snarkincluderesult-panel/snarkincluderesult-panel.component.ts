import { Component, Input, OnInit } from '@angular/core';

import { OptionsService } from '../../common/options.service';
import { SnarkIncludeResultDTO } from '../snark-include-result-dto';

@Component({
  selector: 'app-snarkincluderesult-panel',
  templateUrl: './snarkincluderesult-panel.component.html',
})
export class SnarkIncludeResultPanelComponent implements OnInit {
  private _snarkIncludeResult: SnarkIncludeResultDTO;

  @Input()
  loading = false;

  @Input()
  get snarkIncludeResult(): SnarkIncludeResultDTO {
    return this._snarkIncludeResult;
  }

  set snarkIncludeResult(value: SnarkIncludeResultDTO) {
    this._snarkIncludeResult = value;
  }

  constructor(public optionsService: OptionsService) {}

  ngOnInit(): void {}
}
