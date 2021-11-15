import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { TransmitterService } from '../transmitter.service';
import { TransmitterEntity } from '../../domain/transmitter.entity';
import { OptionsService } from '../../common/options.service';

import { combineLatest } from 'rxjs/index';
import { ValidationMessage } from '../../domain/response/validation-message';
import { switchMap } from 'rxjs/operators';
import { FormGroup } from '@angular/forms';
import { FormService } from '../../form/form.service';
import { BaseEntityFormFactory } from './../../form/base-entity-form-factory';

@Component({
  selector: 'app-transmitter-edit',
  templateUrl: 'transmitter-edit.component.html',
})
export class TransmitterEditComponent implements OnInit {
  // New Form layout

  private _transmitterEntity: TransmitterEntity;
  private formFactory: BaseEntityFormFactory;
  messages: ValidationMessage[];

  myFormGroup: FormGroup;

  expiry: Date;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public optionsService: OptionsService,
    private formService: FormService,
    private service: TransmitterService
  ) {}

  ngOnInit(): void {
    // Take the latest value from each of the supplied observables and execute the subscriber
    // function. Note: the subscribe method isn't called until all observables have issued at
    // least one value. This subscription will keep being fired when there are more values,
    // however the createFormFactory() observable should only issue one value.
    combineLatest(
      this.route.params,
      this.route.queryParams,
      this.formService.createFormFactory('TransmitterEntity')
    ).subscribe(([params, queryParams, formFactory]) => {
      this.formFactory = formFactory;
      if (queryParams.newEntity) {
        this.newEntity();
      } else {
        this.loadEntity(params.id);
      }
    });
  }

  get transmitterEntity(): TransmitterEntity {
    return this._transmitterEntity;
  }

  set transmitterEntity(value: TransmitterEntity) {
    this._transmitterEntity = value;
    if (this.formFactory) {
      this.myFormGroup = this.formFactory.createForm(value);
      // disable enable fields based on the status
      this.myFormGroup.controls.status.valueChanges.subscribe((status) => {
        if (
          [
            OptionsService.TRANSMITTER_STATUS_DEPLOYED_OLD,
            OptionsService.TRANSMITTER_STATUS_DEPLOYED_NEW,
          ].includes(status)
        ) {
          this.myFormGroup.controls.status.disable({ emitEvent: false });
          this.myFormGroup.controls.lifeExpectancy.disable();
        } else {
          this.myFormGroup.controls.status.enable({ emitEvent: false });
          this.myFormGroup.controls.lifeExpectancy.enable();
        }
      });
      // calculate expiry based on the life expectancy
      if (this._transmitterEntity.id) {
        this.myFormGroup.controls.lifeExpectancy.valueChanges
          .pipe(
            switchMap((lifeExpectancy) =>
              this.service.calculateTransmitterExpiry(
                this._transmitterEntity.id,
                lifeExpectancy,
                null
              )
            )
          )
          .subscribe((expiry) => {
            this.expiry = expiry;
          });
      }
      this.myFormGroup.patchValue(this._transmitterEntity);
    }
  }

  newEntity() {
    const e: TransmitterEntity = new TransmitterEntity();
    e.status = 'New'; // TODO reference constant
    // set value after init so that form gets patched correctly
    this.transmitterEntity = e;
  }

  loadEntity(id: string) {
    this.service.findById(id).subscribe((entity) => {
      this.transmitterEntity = entity;
    });
  }

  onSave(): void {
    this.messages = null;
    Object.assign(this.transmitterEntity, this.myFormGroup.getRawValue());
    this.service.save(this.transmitterEntity).subscribe((response) => {
      this._transmitterEntity = response.model;
      this.messages = response.messages;
      if (this.messages.length === 0) {
        this.onCancel();
      }
    });
  }

  onCancel(): void {
    if (this.transmitterEntity.id) {
      this.router.navigate(['/transmitter/' + this.transmitterEntity.id]);
    } else {
      this.router.navigate(['/transmitter']);
    }
  }
}
