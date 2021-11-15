import { Component, Input, OnInit } from '@angular/core';

import { SwabSampleEntity } from '../../domain/swabsample.entity';
import { Router } from '@angular/router';

@Component({
  selector: 'app-swabsample-panel',
  templateUrl: 'swabsample-panel.component.html',
})
export class SwabSamplePanelComponent implements OnInit {
  @Input()
  swabsampleList: SwabSampleEntity[];

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
