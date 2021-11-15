import { ObserverEntity } from 'src/app/domain/observer.entity';
import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { DeadEmbryoService } from '../deadembryo.service';
import { DeadEmbryoEntity } from '../../domain/deadembryo.entity';
import { OptionsService } from '../../common/options.service';

import { combineLatest } from 'rxjs/index';
import { ValidationMessage } from '../../domain/response/validation-message';
import { FormGroup, FormArray } from '@angular/forms';
import { FormService } from '../../form/form.service';
import { BaseEntityFormFactory } from './../../form/base-entity-form-factory';
import { EggMeasurementsEntity } from 'src/app/domain/eggmeasurements.entity';
import { EmbryoMeasurementsEntity } from 'src/app/domain/embryomeasurements.entity';

@Component({
  selector: 'app-deadembryo-edit',
  templateUrl: 'deadembryo-edit.component.html',
})
export class DeadEmbryoEditComponent implements OnInit {
  // New Form layout

  private _deadembryoEntity: DeadEmbryoEntity;
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
    private service: DeadEmbryoService
  ) {}

  ngOnInit(): void {
    // Take the latest value from each of the supplied observables and execute the subscriber
    // function. Note: the subscribe method isn't called until all observables have issued at
    // least one value. This subscription will keep being fired when there are more values,
    // however the createFormFactory() observable should only issue one value.
    combineLatest(
      this.formService.createFormFactory('DeadEmbryoEntity')
    ).subscribe(([formFactory]) => {
      this.formFactory = formFactory;
      this.newEntity();
    });
  }

  get deadembryoEntity(): DeadEmbryoEntity {
    return this._deadembryoEntity;
  }

  set deadembryoEntity(value: DeadEmbryoEntity) {
    this._deadembryoEntity = value;
    if (this.formFactory) {
      this.myFormGroup = this.formFactory.createForm(value);
      this.myFormGroup.patchValue(this._deadembryoEntity);
    }
  }

  newEntity() {
    const e: DeadEmbryoEntity = new DeadEmbryoEntity();
    e.observerList = [];
    e.eggMeasurements = new EggMeasurementsEntity();
    e.embryoMeasurements = new EmbryoMeasurementsEntity();
    // set value after init so that form gets patched correctly
    this.deadembryoEntity = e;
  }

  loadEntity(id: string) {
    this.service.findById(id).subscribe((entity) => {
      this.deadembryoEntity = entity;
    });
  }

  onSave(): void {
    this.messages = null;
    Object.assign(this.deadembryoEntity, this.myFormGroup.getRawValue());
    this.service.save(this.deadembryoEntity).subscribe((response) => {
      this._deadembryoEntity = response.model;
      this.messages = response.messages;
      if (this.messages.length === 0) {
        this.eggSaved.emit(response.model.birdID);
      }
    });
  }

  onCancel(): void {
    this.eggSaved.emit(null);
  }

  // set bird id using direct method as the timing or triggering for alternatives using an @Input() isn't reliable in this case
  setValues(birdID: string, date: Date, observerList: ObserverEntity[]) {
    this.myFormGroup.controls.birdID.setValue(birdID);
    this.myFormGroup.controls.date.setValue(date);
    // remove the old observer list
    const olfa = this.myFormGroup.controls.observerList as FormArray;
    for (let i = 0; i < olfa.controls.length; i++) {
      olfa.removeAt(0);
    }
    // add the new one
    observerList.forEach((o) =>
      ((olfa as any).addItem() as FormGroup).patchValue(o)
    );
  }
}
