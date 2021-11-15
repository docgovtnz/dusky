import { debounceTime } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { BirdSummaryDto } from '../../common/bird-summary-dto';
import { OptionsService } from '../../common/options.service';

@Component({
  selector: 'app-noranet-bird-select-multi',
  templateUrl: './noranet-bird-select-multi.component.html',
})
export class NoraNetBirdSelectMultiComponent implements OnInit {
  summaryList: BirdSummaryDto[] = [];

  birdNameSelected: string = null;

  selectionSubject: Subject<string> = new Subject();

  _selectedBirds: string[] = [];

  @Input()
  autofocus = false;

  @Input()
  disabled = false;

  @Output()
  selectedBirdsChange: EventEmitter<any[]> = new EventEmitter<any[]>();

  constructor(private optionsService: OptionsService) {}

  ngOnInit() {
    this.optionsService
      .getBirdSummaries()
      .subscribe((summaryList: BirdSummaryDto[]) => {
        this.summaryList = summaryList;
      });

    this.selectionSubject.pipe(debounceTime(100)).subscribe((id) => {
      this._selectedBirds.push(id);
      this.selectedBirdsChange.emit(this.convertSelectedBirds());
      this.birdNameSelected = null;
    });
  }

  @Input()
  get selectedBirds() {
    console.log('get');
    return this.convertSelectedBirds();
  }

  set selectedBirds(selectedBirds: any[]) {
    console.log('set');
    console.log(selectedBirds);
    if (selectedBirds) {
      this._selectedBirds = selectedBirds.map((sb) => sb.birdID);
    } else {
      this._selectedBirds = [];
    }
  }

  // Converts between the format stored internally, to a list of objects expected by a NoraNetEntity ({ birdID: <id> })
  convertSelectedBirds() {
    return this._selectedBirds.map((_sb) => Object.assign({}, { birdID: _sb }));
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
    if (!id) {
return null;
}

    const summary = this.summaryList.find((summary) => summary.id === id);
    if (!summary || !summary.birdName) {
return null;
}

    return summary.birdName;
  }

  onRemoveOptionAction(index: number) {
    this._selectedBirds.splice(index, 1);
    this.selectedBirdsChange.emit(this.convertSelectedBirds());
  }
}
