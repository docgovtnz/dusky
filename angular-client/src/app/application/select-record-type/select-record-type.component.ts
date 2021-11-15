import { Component, OnInit, ViewChild } from '@angular/core';
import { ModalDirective } from 'ngx-bootstrap/modal';
import { OptionsService } from '../../common/options.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-select-record-type',
  templateUrl: './select-record-type.component.html',
})
export class SelectRecordTypeComponent implements OnInit {
  @ViewChild('selectRecordTypeModal', { static: true })
  public selectRecordTypeModal: ModalDirective;

  recordType: string;

  constructor(private router: Router, public optionsService: OptionsService) {}

  ngOnInit() {}

  startSelect() {
    this.selectRecordTypeModal.show();
  }

  onConfirmSelectRecordType() {
    // TODO ensure this isn't hard coded some how
    if (this.recordType === 'Signal only') {
      this.router.navigate(['/record/bulksignalentry']);
    } else {
      this.router.navigate(['/record/edit/-1'], {
        queryParams: { newEntity: true, recordType: this.recordType },
      });
    }
    this.selectRecordTypeModal.hide();
  }

  onCancelSelectRecordType() {
    this.selectRecordTypeModal.hide();
  }
}
