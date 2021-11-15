import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NewEggService } from '../newegg.service';
import { NewEggEntity } from '../../domain/newegg.entity';
import { OptionsService } from '../../common/options.service';

import { combineLatest, forkJoin } from 'rxjs/index';
import { ValidationMessage } from '../../domain/response/validation-message';
import { map, switchMap, tap, filter } from 'rxjs/operators';
import { merge } from 'rxjs';
import { FormGroup } from '@angular/forms';
import { FormService } from '../../form/form.service';
import { BaseEntityFormFactory } from './../../form/base-entity-form-factory';
import { LocationService } from 'src/app/location/location.service';
import { BirdService } from 'src/app/bird/bird.service';
import * as moment from 'moment';

@Component({
  selector: 'app-newegg-edit',
  templateUrl: 'newegg-edit.component.html',
})
export class NewEggEditComponent implements OnInit {
  // New Form layout

  private _neweggEntity: NewEggEntity;
  private formFactory: BaseEntityFormFactory;
  messages: ValidationMessage[];

  myFormGroup: FormGroup;

  @Output()
  eggSaved = new EventEmitter();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public optionsService: OptionsService,
    private formService: FormService,
    private service: NewEggService,
    private locationService: LocationService,
    private birdService: BirdService
  ) {}

  ngOnInit(): void {
    // Take the latest value from each of the supplied observables and execute the subscriber
    // function. Note: the subscribe method isn't called until all observables have issued at
    // least one value. This subscription will keep being fired when there are more values,
    // however the createFormFactory() observable should only issue one value.
    combineLatest(this.formService.createFormFactory('NewEggEntity')).subscribe(
      ([formFactory]) => {
        this.formFactory = formFactory;
        this.newEntity();
      }
    );
  }

  get neweggEntity(): NewEggEntity {
    return this._neweggEntity;
  }

  set neweggEntity(value: NewEggEntity) {
    this._neweggEntity = value;
    if (this.formFactory) {
      this.myFormGroup = this.formFactory.createForm(value);
      // setup so that clutch order id derived from the selected location
      const locationIDControl = this.myFormGroup.controls.locationID;
      locationIDControl.valueChanges
        .pipe(
          tap(() => this.myFormGroup.controls.clutchOrder.setValue(null)),
          filter((locationID) => locationID !== null),
          switchMap((locationID) =>
            this.locationService.getNextClutchOrder(locationID)
          )
        )
        .subscribe((nextClutchOrder) => {
          this.myFormGroup.controls.clutchOrder.setValue(nextClutchOrder);
        });
      // setup so that egg name is derived from the clutch order and bird
      // use ALice1-1-18
      const motherControl = this.myFormGroup.controls.mother;
      const clutchOrderControl = this.myFormGroup.controls.clutchOrder;
      const layDateControl = this.myFormGroup.controls.layDate;
      merge(
        motherControl.valueChanges.pipe(
          map((birdID) => [
            birdID,
            locationIDControl.value,
            clutchOrderControl.value,
            layDateControl.value,
          ])
        ),
        locationIDControl.valueChanges.pipe(
          map((locationID) => [
            motherControl.value,
            locationID,
            clutchOrderControl.value,
            layDateControl.value,
          ])
        ),
        clutchOrderControl.valueChanges.pipe(
          map((clutchOrder) => [
            motherControl.value,
            locationIDControl.value,
            clutchOrder,
            layDateControl.value,
          ])
        ),
        layDateControl.valueChanges.pipe(
          map((layDate) => [
            motherControl.value,
            locationIDControl.value,
            clutchOrderControl.value,
            layDate,
          ])
        )
      )
        .pipe(
          filter(
            ([birdID, locationID, clutchOrder, layDate]) =>
              birdID !== null &&
              locationID !== null &&
              clutchOrder !== null &&
              layDate !== null
          ),
          switchMap(([birdID, locationID, clutchOrder, layDate]) =>
            forkJoin(
              this.birdService.getName(birdID),
              this.locationService.getClutch(locationID)
            ).pipe(
              map(([birdName, clutch]) => [
                birdName,
                clutch,
                clutchOrder,
                layDate,
              ])
            )
          )
        )
        .subscribe(([birdName, clutch, clutchOrder, layDate]) => {
          const eggNameControl = this.myFormGroup.controls.eggName;
          // Only update if a value is not already set.
          if (!eggNameControl.value) {
            this.populateEggName(birdName, clutchOrder, clutch, layDate);
          }
        });
      this.myFormGroup.patchValue(this._neweggEntity);
    }
  }

  populateEggName(
    motherName: string,
    clutchOrder: number,
    clutch: string,
    layDate: Date
  ) {
    // Default the year to this year. Overwrite if a layDate is set.
    let year = moment().year().toString(10).substr(0, 4);
    if (layDate) {
      year = moment(layDate).year().toString(10).substr(0, 4);
    }

    // Set clutch to X if not found on the Nest Location.
    if (!clutch) {
      clutch = 'X';
    }
    this.myFormGroup.controls.eggName.setValue(
      `${motherName}-${clutch}${clutchOrder}-${year}`
    );
  }

  newEntity() {
    const e: NewEggEntity = new NewEggEntity();
    // set value after init so that form gets patched correctly
    this.neweggEntity = e;
  }

  onSave(): void {
    this.messages = null;
    Object.assign(this.neweggEntity, this.myFormGroup.getRawValue());
    this.service.save(this.neweggEntity).subscribe((response) => {
      this._neweggEntity = response.model;
      this.messages = response.messages;
      if (this.messages.length === 0) {
        this.eggSaved.emit(response.model.birdID);
      }
    });
  }

  onCancel(): void {
    this.eggSaved.emit(null);
  }

  setValues(mother: string, locationID: string, date: Date) {
    // Clear the form for the next entry.
    this.myFormGroup.controls.eggWidth.setValue(null);
    this.myFormGroup.controls.eggLength.setValue(null);
    this.myFormGroup.controls.fwCoefficientX10P4.setValue(null);
    this.myFormGroup.controls.clutchOrder.setValue(null);
    this.myFormGroup.controls.mother.setValue(null);
    this.myFormGroup.controls.locationID.setValue(null);
    this.myFormGroup.controls.layDate.setValue(null);

    this.myFormGroup.controls.eggName.setValue(null);

    this.myFormGroup.controls.mother.setValue(mother);
    this.myFormGroup.controls.locationID.setValue(locationID);
    this.myFormGroup.controls.layDate.setValue(date);
  }
}
