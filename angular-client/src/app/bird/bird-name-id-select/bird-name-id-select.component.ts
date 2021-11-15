import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { OptionsService } from '../../common/options.service';
import { BirdSummaryDto } from '../../common/bird-summary-dto';
import { InputService } from '../../common/input.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-bird-name-id-select',
  templateUrl: './bird-name-id-select.component.html',
  styleUrls: ['./bird-name-id-select.component.scss'],
})
export class BirdNameIdSelectComponent implements OnInit, OnDestroy {
  private _birdID;
  birdNameSelected: string;
  // we must set to an empty array to avoid 'You provided 'undefined' where a stream was expected.' error
  summaryList: BirdSummaryDto[] = [];

  @Input()
  disabled = false;

  @Input()
  autofocus = false;

  @Output()
  birdIDChange = new EventEmitter();

  clearInputListener: Subscription;

  constructor(
    private optionsService: OptionsService,
    private clearInputService: InputService
  ) {
    this.clearInputListener = clearInputService.clearInputRequest$.subscribe(
      () => {
        this.birdID = null;
      }
    );
  }

  ngOnInit() {
    this.optionsService
      .getBirdSummaries()
      .subscribe((summaryList: BirdSummaryDto[]) => {
        this.summaryList = summaryList;
        this.refreshBirdNameSelected();
      });
  }

  private refreshBirdNameSelected() {
    if (!this._birdID) {
      this.birdNameSelected = null;
    } else if (this.summaryList) {
      const bs = this.summaryList.find((i) => i.id === this._birdID);
      if (bs) {
        this.birdNameSelected = bs.birdName;
      }
    }
  }

  get birdID() {
    return this._birdID;
  }

  @Input()
  set birdID(value) {
    this._birdID = value;
    this.refreshBirdNameSelected();
  }

  onTypeaheadOnSelect($event) {
    this._birdID = $event.item.id;
    // we've got the user's new selection so fire an event with the next value
    this.birdIDChange.next($event.item.id);
  }

  ngOnDestroy() {
    this.clearInputListener.unsubscribe();
  }

  onInputBlur() {
    // if the summary list has been loaded
    // (meaning refreshBirdNameSelected() has hopefully been called for the initial value)
    if (this.summaryList.length > 0) {
      // if there is text in the input
      if (this.birdNameSelected) {
        const bs = this.summaryList.find(
          (i) => i.birdName === this.birdNameSelected
        );
        if (bs) {
          if (this._birdID !== bs.id) {
            this._birdID = bs.id;
            this.birdIDChange.next(this._birdID);
          }
        } else {
          // then set the text back to the name corresponding to the id
          this.refreshBirdNameSelected();
        }
      } else {
        this._birdID = null;
        this.birdIDChange.next(null);
      }
    }
  }
}
