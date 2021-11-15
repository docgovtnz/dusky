import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-bird-name-label-multi',
  templateUrl: './bird-name-label-multi.component.html',
})
export class BirdNameLabelMultiComponent {
  @Input()
  birdList: any[];
}
