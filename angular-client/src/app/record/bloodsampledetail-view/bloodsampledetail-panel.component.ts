import { Component, Input, OnInit } from '@angular/core';

import { BloodSampleDetailEntity } from '../../domain/bloodsampledetail.entity';
import { Router } from '@angular/router';

@Component({
  selector: 'app-bloodsampledetail-panel',
  templateUrl: 'bloodsampledetail-panel.component.html',
})
export class BloodSampleDetailPanelComponent implements OnInit {
  @Input()
  bloodSampleDetail: BloodSampleDetailEntity;

  constructor(private router: Router) {}

  ngOnInit(): void {}

  onClick(item, $event) {
    const url = 'sample/' + item.sampleID;
    if ($event.ctrlKey) {
      window.open('app/' + url, '_blank');
    } else {
      this.router.navigate([url]);
    }
  }
}
