import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { NoraNetDetectionEntity } from '../../entities/noranetdetection.entity';

@Component({
  selector: 'app-noranet-detection-panel',
  templateUrl: './noranet-detection.component.html',
})
export class NoraNetDetectionPanelComponent implements OnInit {
  @Input()
  detectionList: NoraNetDetectionEntity[];

  constructor(private router: Router) {}

  ngOnInit(): void {}
}
