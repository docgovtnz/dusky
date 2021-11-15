import { Component, Input, OnInit } from '@angular/core';

import { SpermSampleEntity } from '../../domain/spermsample.entity';
import { Router } from '@angular/router';

@Component({
  selector: 'app-spermsample-panel',
  templateUrl: 'spermsample-panel.component.html',
})
export class SpermSamplePanelComponent implements OnInit {
  @Input()
  spermsampleList: SpermSampleEntity[];

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
