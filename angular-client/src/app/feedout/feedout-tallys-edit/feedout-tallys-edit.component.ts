import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

import { OptionsService } from '../../common/options.service';
import { FoodTallyEntity } from '../../domain/foodtally.entity';

@Component({
  selector: 'app-feedout-tallys-edit',
  templateUrl: './feedout-tallys-edit.component.html',
})
export class FeedOutTallysEditComponent implements OnInit {
  @Input()
  tallys: FoodTallyEntity[];

  @Output()
  targetsChange = new EventEmitter<FoodTallyEntity[]>();

  constructor(public optionsService: OptionsService) {}

  ngOnInit() {}

  onAddClick() {
    this.tallys.push(new FoodTallyEntity());
  }

  onDeleteClick(index) {
    this.tallys.splice(index, 1);
  }
}
