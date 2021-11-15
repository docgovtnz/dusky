import { Component, Input, Output, OnInit, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';

import { PagedResponse } from 'src/app/domain/response/paged-response';
import { NoraNetCriteria } from '../../noranet.criteria';

@Component({
  selector: 'app-noranet-detected-panel',
  templateUrl: './noranet-detected.component.html',
})
export class NoraNetDetectedPanelComponent implements OnInit {
  @Input()
  pagedResponse: PagedResponse;

  @Input()
  noranetCriteria: NoraNetCriteria;

  @Output()
  pageChanged = new EventEmitter();

  constructor(private router: Router) {}

  ngOnInit(): void {}

  onPageChanged(event: any) {
    this.pageChanged.emit(event);
  }

  onClick(item, $event) {
    const url = 'noranet/' + item.noraNetId;
    if ($event.ctrlKey) {
      window.open('app/' + url, '_blank');
    } else {
      this.router.navigate([url]);
    }
  }
}
