import { Component, Input, OnInit } from '@angular/core';
import { TxActivityEntity } from '../../domain/txactivity.entity';
import { OptionsService } from '../../common/options.service';

@Component({
  selector: 'app-txactivity-edit-form',
  templateUrl: './txactivity-edit-form.component.html',
})
export class TxActivityEditFormComponent implements OnInit {
  @Input()
  txactivityEntity: TxActivityEntity;

  constructor(public optionsService: OptionsService) {}

  ngOnInit() {}
}
