import { debounceTime } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { BirdSummaryDto } from '../../common/bird-summary-dto';
import { OptionsService } from '../../common/options.service';

@Component({
  selector: 'app-bird-id-select-multi',
  templateUrl: './bird-id-select-multi.component.html',
})
export class BirdIdSelectMultiComponent implements OnInit {
  summaryList: BirdSummaryDto[] = [];

  birdNameSelected: string = null;

  selectionSubject: Subject<string> = new Subject();

  _selectedBirds: string[] = [];

  @Input()
  autofocus = false;

  @Input()
  disabled = false;

  @Output()
  selectedBirdsChange: EventEmitter<string[]> = new EventEmitter<string[]>();

  constructor(private optionsService: OptionsService) {}

  ngOnInit() {
    this.optionsService
      .getBirdSummaries()
      .subscribe((summaryList: BirdSummaryDto[]) => {
        this.summaryList = summaryList;
      });

    this.selectionSubject.pipe(debounceTime(100)).subscribe((id) => {
      this._selectedBirds.push(id);
      this.selectedBirdsChange.emit(this._selectedBirds);
      this.birdNameSelected = null;
    });
  }

  @Input()
  get selectedBirds() {
    return this._selectedBirds;
  }

  set selectedBirds(selectedBirds: string[]) {
    if (selectedBirds) {
      this._selectedBirds = selectedBirds;
    } else {
      this._selectedBirds = [];
    }
  }

  onEnter(event) {
    event.preventDefault();
    event.stopPropagation();
  }

  onSelect(event) {
    if (event.item) {
      if (!this._selectedBirds.includes(event.item.id)) {
        this.selectionSubject.next(event.item.id);
      }
    }
  }

  onBlur() {
    const el = this.summaryList.find(
      (birdSummary) =>
        birdSummary.birdName.toLowerCase() === this.birdNameSelected
    );
    if (el && !this._selectedBirds.includes(el.id)) {
      this.selectionSubject.next(el.id);
    }
  }

  getBirdName(id: string): string {
    return this.summaryList.find((summary) => summary.id === id).birdName;
  }

  onRemoveOptionAction(index: number) {
    this._selectedBirds.splice(index, 1);
    this.selectedBirdsChange.emit(this._selectedBirds);
  }
}
