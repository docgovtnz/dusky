import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { RecordService } from '../record.service';
import { OptionsService } from '../../common/options.service';

@Component({
  selector: 'app-bulksignalentry-edit',
  templateUrl: 'bulksignalentry-edit.component.html',
})
export class BulkSignalEntryEditComponent implements OnInit {
  constructor(
    public optionsService: OptionsService,
    private service: RecordService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // TODO
  }

  onClose(): void {
    this.router.navigate(['/']);
  }
}
