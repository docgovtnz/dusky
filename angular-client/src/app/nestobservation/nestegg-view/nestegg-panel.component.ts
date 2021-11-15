import { Component, Input, OnInit } from '@angular/core';

import { NestEggEntity } from '../../domain/nestegg.entity';
import { RecordEntity } from '../../domain/record.entity';
import { Router } from '@angular/router';

@Component({
  selector: 'app-nestegg-panel',
  templateUrl: 'nestegg-panel.component.html',
})
export class NestEggPanelComponent implements OnInit {
  @Input()
  nesteggList: NestEggEntity[];

  @Input()
  recordList: RecordEntity[];

  constructor(private router: Router) {}

  ngOnInit(): void {}

  navToRecord(recordID: string) {
    this.router.navigate(['record/' + recordID]);
  }
}
