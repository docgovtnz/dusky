import { Component, Input, OnInit } from '@angular/core';

import { NestDetailsEntity } from '../../domain/nestdetails.entity';

@Component({
  selector: 'app-nestdetails-panel',
  templateUrl: 'nestdetails-panel.component.html',
})
export class NestDetailsPanelComponent implements OnInit {
  @Input()
  nestdetailsRevision: NestDetailsEntity;

  constructor() {}

  ngOnInit(): void {}
}
