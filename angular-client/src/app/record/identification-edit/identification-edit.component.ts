import { startWith, switchMap, catchError, filter, tap } from 'rxjs/operators';
import { Observable } from 'rxjs/internal/Observable';
import { CurrentBandDTO } from './../../bird/current-band-dto';
import { empty, of } from 'rxjs';
import { Component, Input, OnInit } from '@angular/core';
import { OptionsService } from '../../common/options.service';
import { RecordService } from '../../record/record.service';
import { TransmitterService } from '../../transmitter/transmitter.service';
import { TransmitterEntity } from '../../domain/transmitter.entity';
import { FormControl, FormGroup } from '@angular/forms';
import { BirdService } from 'src/app/bird/bird.service';
import { CurrentChipDTO } from 'src/app/bird/current-chip-dto';

@Component({
  selector: 'app-identification-edit',
  templateUrl: './identification-edit.component.html',
})
export class IdentificationEditComponent implements OnInit {
  // New Form layout
  @Input()
  myFormGroup: FormGroup;
  checkGroup: FormGroup;
  currentTransmitter: TransmitterEntity;
  currentDeployedDate: Date;
  currentExpiryDate: Date;
  currentBand: CurrentBandDTO;
  currentChip: CurrentChipDTO;
  selectedTransmitter: TransmitterEntity;
  selectedExpiryDate: Date;

  private _birdID: string;
  private _recordDateTime: Date;

  constructor(
    public optionsService: OptionsService,
    private service: RecordService,
    private birdService: BirdService,
    private transmitterService: TransmitterService
  ) {}

  ngOnInit() {
    this.checkGroup = new FormGroup({
      txRemovedCheck: new FormControl(),
    });
    const txToControl = (this.myFormGroup.controls
      .transmitterChange as FormGroup).controls.txTo;
    txToControl.valueChanges
      .pipe(
        startWith(txToControl.value),
        tap(() => (this.selectedTransmitter = null)),
        filter((id) => id !== null && id !== 'Removed'),
        switchMap((id) => this.transmitterService.findById(id)),
        catchError(() => empty())
      )
      .subscribe((t) => {
        this.selectedTransmitter = t;
      });
    txToControl.valueChanges
      .pipe(
        tap(() => (this.selectedExpiryDate = null)),
        filter((id) => id !== null && id !== 'Removed'),
        switchMap((id) =>
          this.calculateSelectedExpiryDate(id, this.recordDateTime)
        ),
        catchError(() => empty())
      )
      .subscribe((expiryDate) => {
        this.selectedExpiryDate = expiryDate;
      });
    txToControl.valueChanges.subscribe((id) => {
      if (id === 'Removed') {
        this.myFormGroup
          .get('transmitterChange')
          .get('newTxFineTune')
          .setValue('');
        this.myFormGroup
          .get('transmitterChange')
          .get('newTxFineTune')
          .disable();
        if (this.myFormGroup.get('harnessChange')) {
          this.myFormGroup
            .get('harnessChange')
            .get('newHarnessLength')
            .disable();
          this.myFormGroup
            .get('harnessChange')
            .get('newHarnessLength')
            .setValue('');
        }
      } else {
        this.myFormGroup.get('transmitterChange').get('newTxFineTune').enable();
        if (this.myFormGroup.get('harnessChange')) {
          this.myFormGroup
            .get('harnessChange')
            .get('newHarnessLength')
            .enable();
        }
      }
    });

    if (this.service.eventReason === 'Tx dropped') {
      this.checkGroup.controls.txRemovedCheck.setValue(true);
      this.myFormGroup.get('transmitterChange').get('txTo').setValue('Removed');
      this.myFormGroup
        .get('transmitterChange')
        .get('newTxFineTune')
        .setValue('');
      //this.myFormGroup.get('harnessChange').get('newHarnessLength').setValue(null);
      this.myFormGroup.get('transmitterChange').get('txTo').disable();
      this.myFormGroup.get('transmitterChange').get('newTxFineTune').disable();
      //this.myFormGroup.get('harnessChange').get('newHarnessLength').disable();
    }

    this.checkGroup.controls.txRemovedCheck.valueChanges.subscribe((e) => {
      if (e) {
        //todo force the remove
        this.myFormGroup
          .get('transmitterChange')
          .get('txTo')
          .setValue('Removed');
        this.myFormGroup
          .get('transmitterChange')
          .get('newTxFineTune')
          .setValue(null);
        this.myFormGroup.get('transmitterChange').get('txTo').disable();
        this.myFormGroup
          .get('transmitterChange')
          .get('newTxFineTune')
          .disable();
        if (this.myFormGroup.get('harnessChange')) {
          this.myFormGroup
            .get('harnessChange')
            .get('newHarnessLength')
            .setValue(null);
          this.myFormGroup
            .get('harnessChange')
            .get('newHarnessLength')
            .disable();
        }
      } else {
        //todo reset remove
        this.myFormGroup.get('transmitterChange').get('txTo').setValue(null);
        this.myFormGroup.get('transmitterChange').get('txTo').enable();
        this.myFormGroup.get('transmitterChange').get('newTxFineTune').enable();
        if (this.myFormGroup.get('harnessChange')) {
          this.myFormGroup
            .get('harnessChange')
            .get('newHarnessLength')
            .enable();
        }
      }
    });
  }

  calculateSelectedExpiryDate(
    id: string,
    recordDateTime: Date
  ): Observable<Date> {
    return this.transmitterService
      .calculateTransmitterExpiry(id, null, null)
      .pipe(
        switchMap((expiryDate) => {
          if (expiryDate) {
            return of(expiryDate);
          } else if (recordDateTime) {
            return this.transmitterService.calculateTransmitterExpiry(
              id,
              null,
              recordDateTime
            );
          } else {
            return empty();
          }
        })
      );
  }

  @Input()
  set recordDateTime(value: Date) {
    this.selectedExpiryDate = null;
    this._recordDateTime = value;
    const id = (this.myFormGroup.controls.transmitterChange as FormGroup)
      .controls.txTo.value;
    if (id && id !== 'Removed') {
      this.calculateSelectedExpiryDate(id, value).subscribe(
        (selectedExpiryDate) => {
          this.selectedExpiryDate = selectedExpiryDate;
        }
      );
    }
  }

  get recordDateTime() {
    return this._recordDateTime;
  }

  @Input()
  set birdID(value: string) {
    this.currentBand = null;
    this.currentChip = null;
    this.currentTransmitter = null;
    this.currentDeployedDate = null;
    this.currentExpiryDate = null;
    this._birdID = value;
    if (value) {
      this.birdService.getCurrentBand(value).subscribe((b) => {
        this.currentBand = b;
      });
      this.birdService.getCurrentChip(value).subscribe((c) => {
        this.currentChip = c;
      });
      this.birdService.getCurrentTransmitter(value).subscribe((t) => {
        this.currentTransmitter = t;
      });
      this.birdService
        .getCurrentTransmitterDeployedDate(value)
        .subscribe((d) => {
          this.currentDeployedDate = d;
        });
      this.birdService.getCurrentTransmitterExpiryDate(value).subscribe((d) => {
        this.currentExpiryDate = d;
      });
    }
  }

  get birdID() {
    return this._birdID;
  }
}
