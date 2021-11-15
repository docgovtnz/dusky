import { Subscription } from 'rxjs';

import { Component, forwardRef, OnDestroy, OnInit, Input } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

import { TransmitterEntity } from '../../../domain/transmitter.entity';
import { OptionsService } from '../../../common/options.service';
import { InputService } from '../../../common/input.service';
import { TransmitterService } from '../../../transmitter/transmitter.service';

@Component({
  selector: 'app-transmitter-txid-id-select-control',
  templateUrl: './transmitter-txid-id-select-control.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => TransmitterTxidIdSelectControlComponent),
      multi: true,
    },
  ],
})
export class TransmitterTxidIdSelectControlComponent
  implements OnInit, OnDestroy, ControlValueAccessor {
  transmitterTxidSelected: string;
  transmitterIDSelected: string;
  // we must set to an empty array to avoid 'You provided 'undefined' where a stream was expected.' error
  optionList: TransmitterEntity[] = [];
  fullList: TransmitterEntity[];

  @Input()
  disabled = false;

  @Input()
  autofocus = false;

  @Input()
  removedOption = false;

  private clearInputListener: Subscription;

  // the method set in registerOnChange, it is just
  // a placeholder for a method that takes one parameter,
  // we use it to emit changes back to the form
  private propagateChange = (_: any) => {};

  constructor(
    private optionsService: OptionsService,
    private clearInputService: InputService,
    private transmitterService: TransmitterService
  ) {
    this.clearInputListener = clearInputService.clearInputRequest$.subscribe(
      () => {
        this.transmitterIDSelected = null;
        this.refreshTransmitterTxidSelected();
      }
    );
  }

  ngOnInit() {
    this.transmitterService.findSpareTransmitters().subscribe((result) => {
      this.optionList = result;
      if (this.removedOption) {
        this.optionList.push({
          txId: 'Removed',
          id: 'Removed',
        } as TransmitterEntity);
      }
      this.refreshTransmitterTxidSelected();
    });
    this.transmitterService.findAllTransmitters().subscribe((result) => {
      this.fullList = result;
      if (this.removedOption) {
        this.fullList.push({
          txId: 'Removed',
          id: 'Removed',
        } as TransmitterEntity);
      }
      this.refreshTransmitterTxidSelected();
    });
  }

  private refreshTransmitterTxidSelected() {
    if (!this.transmitterIDSelected) {
      this.transmitterTxidSelected = null;
    } else if (this.fullList) {
      // we need to use the full list as we could be looking at a past record
      const t = this.fullList.find((i) => i.id === this.transmitterIDSelected);
      if (t) {
        this.transmitterTxidSelected = t.txId;
      }
    }
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {}

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  writeValue(value: any): void {
    this.transmitterIDSelected = value;
    this.refreshTransmitterTxidSelected();
  }

  onValueChange(nextValue: any) {
    this.transmitterIDSelected = nextValue;
    this.propagateChange(nextValue);
  }

  ngOnDestroy() {
    this.clearInputListener.unsubscribe();
  }

  onInputBlur() {
    // if the full list has been loaded
    // (meaning refreshTransmitterTxidSelected() has hopefully been called for the initial value)
    if (this.fullList) {
      // if there is text in the input
      if (this.transmitterTxidSelected) {
        const t = this.fullList.find(
          (i) => i.txId === this.transmitterTxidSelected
        );
        if (t) {
          this.transmitterIDSelected = t.id;
          this.propagateChange(this.transmitterIDSelected);
        } else {
          // then set the text back to the name corresponding to the id
          this.refreshTransmitterTxidSelected();
        }
      } else {
        this.transmitterIDSelected = null;
        this.propagateChange(null);
      }
    }
  }
}
