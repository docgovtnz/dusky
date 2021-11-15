import { Component, Input, OnInit } from '@angular/core';

import { NestChickEntity } from '../../domain/nestchick.entity';
import { RecordEntity } from '../../domain/record.entity';
import { Router } from '@angular/router';

@Component({
  selector: 'app-nestchick-panel',
  templateUrl: 'nestchick-panel.component.html',
})
export class NestChickPanelComponent implements OnInit {
  @Input()
  nestchickList: NestChickEntity[];

  @Input()
  recordList: RecordEntity[];

  constructor(private router: Router) {}

  ngOnInit(): void {}

  navToRecord(recordID: string) {
    this.router.navigate(['record/' + recordID]);
  }
}
