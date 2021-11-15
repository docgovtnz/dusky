import { Component, Input, OnInit } from '@angular/core';

import { OtherSampleEntity } from '../../domain/othersample.entity';
import { Router } from '@angular/router';

@Component({
  selector: 'app-othersample-panel',
  templateUrl: 'othersample-panel.component.html',
})
export class OtherSamplePanelComponent implements OnInit {
  @Input()
  othersampleList: OtherSampleEntity[];

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
