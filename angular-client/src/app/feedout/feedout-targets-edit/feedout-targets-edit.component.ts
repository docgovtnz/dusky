import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

import { TargetBirdEntity } from '../../domain/targetbird.entity';

@Component({
  selector: 'app-feedout-targets-edit',
  templateUrl: './feedout-targets-edit.component.html',
})
export class FeedOutTargetsEditComponent implements OnInit {
  @Input()
  targets: TargetBirdEntity[];

  @Output()
  targetsChange = new EventEmitter<TargetBirdEntity[]>();

  constructor() {}

  ngOnInit() {}

  onAddClick() {
    this.targets.push(new TargetBirdEntity());
  }

  onDeleteClick(index) {
    this.targets.splice(index, 1);
  }
}
