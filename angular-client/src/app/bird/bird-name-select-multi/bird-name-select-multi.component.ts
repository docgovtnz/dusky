import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { OptionsService } from '../../common/options.service';

@Component({
  selector: 'app-bird-name-select-multi',
  templateUrl: './bird-name-select-multi.component.html',
})
export class BirdNameSelectMultiComponent implements OnInit {
  allBirdNames: string[] = [];

  _selectedBirds: string[] = [];

  @Output()
  selectedBirdsChange: EventEmitter<string[]> = new EventEmitter<string[]>();

  birdNameSelected: string = null;

  @Input()
  autofocus = false;

  @Input()
  disabled = false;

  @Input()
  set selectedBirds(selectedBirds: string[]) {
    if (selectedBirds) {
      if (typeof selectedBirds === 'string') {
        this._selectedBirds = [selectedBirds];
      } else {
        this._selectedBirds = selectedBirds;
      }
    } else {
      this._selectedBirds = [];
    }
  }

  constructor(private optionsService: OptionsService) {}

  ngOnInit() {
    // get the list from the options service function
    this.optionsService.getBirdNames().subscribe((birdNames: string[]) => {
      this.allBirdNames = birdNames;
    });
  }

  onEscape() {
    if (this.birdNameSelected) {
      if (!this._selectedBirds.includes(this.birdNameSelected)) {
        this._selectedBirds.push(this.birdNameSelected);
        this.selectedBirdsChange.emit(this._selectedBirds);
      }
      this.birdNameSelected = null;
    }
  }

  onEnter(event) {
    event.preventDefault();
    event.stopPropagation();
  }

  onSelect(event) {
    if (event.item) {
      if (!this._selectedBirds.includes(event.item)) {
        this._selectedBirds.push(event.item);
        this.selectedBirdsChange.emit(this._selectedBirds);
      }
      this.birdNameSelected = null;
    }
  }

  onRemoveOptionAction(index: number) {
    this._selectedBirds.splice(index, 1);
    this.selectedBirdsChange.emit(this._selectedBirds);
  }
}
