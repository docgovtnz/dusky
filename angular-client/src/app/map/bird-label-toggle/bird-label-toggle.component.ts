import { Component, EventEmitter, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-bird-label-toggle',
  templateUrl: './bird-label-toggle.component.html',
  styleUrls: ['./bird-label-toggle.component.scss'],
})
export class BirdLabelToggleComponent implements OnInit {
  displayBirdNames = false;
  displayPaths = false;
  displayRecordDate = false;
  displayRecordType = false;

  @Output()
  changesMade = new EventEmitter();

  constructor() {}

  ngOnInit() {}

  updateBirdLabels() {
    console.log('Update Bird Labels: ');
    this.changesMade.next();
  }
}
