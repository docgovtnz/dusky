import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { FledgedChickService } from '../fledgedchick.service';
import { FledgedChickEntity } from '../../domain/fledgedchick.entity';
import { OptionsService } from '../../common/options.service';

import { combineLatest } from 'rxjs/index';
import { ValidationMessage } from '../../domain/response/validation-message';
import { FormGroup, FormArray } from '@angular/forms';
import { FormService } from '../../form/form.service';
import { BaseEntityFormFactory } from './../../form/base-entity-form-factory';
import { ObserverEntity } from 'src/app/domain/observer.entity';

@Component({
  selector: 'app-fledgedchick-edit',
  templateUrl: 'fledgedchick-edit.component.html',
})
export class FledgedChickEditComponent implements OnInit {
  // New Form layout

  private _fledgedchickEntity: FledgedChickEntity;
  private formFactory: BaseEntityFormFactory;
  messages: ValidationMessage[];

  myFormGroup: FormGroup;

  @Output()
  chickSaved = new EventEmitter();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public optionsService: OptionsService,
    private formService: FormService,
    private service: FledgedChickService
  ) {}

  ngOnInit(): void {
    // Take the latest value from each of the supplied observables and execute the subscriber
    // function. Note: the subscribe method isn't called until all observables have issued at
    // least one value. This subscription will keep being fired when there are more values,
    // however the createFormFactory() observable should only issue one value.
    combineLatest(
      this.formService.createFormFactory('FledgedChickEntity')
    ).subscribe(([formFactory]) => {
      this.formFactory = formFactory;
      this.newEntity();
    });
  }

  get fledgedchickEntity(): FledgedChickEntity {
    return this._fledgedchickEntity;
  }

  set fledgedchickEntity(value: FledgedChickEntity) {
    this._fledgedchickEntity = value;
    if (this.formFactory) {
      this.myFormGroup = this.formFactory.createForm(value);
      this.myFormGroup.patchValue(this._fledgedchickEntity);
    }
  }

  newEntity() {
    const e: FledgedChickEntity = new FledgedChickEntity();
    e.observerList = [];
    // set value after init so that form gets patched correctly
    this.fledgedchickEntity = e;
  }

  loadEntity(id: string) {
    this.service.findById(id).subscribe((entity) => {
      this.fledgedchickEntity = entity;
    });
  }

  onSave(): void {
    this.messages = null;
    Object.assign(this.fledgedchickEntity, this.myFormGroup.getRawValue());
    this.service.save(this.fledgedchickEntity).subscribe((response) => {
      this._fledgedchickEntity = response.model;
      this.messages = response.messages;
      if (this.messages.length === 0) {
        this.chickSaved.emit(response.model.birdID);
      }
    });
  }

  onCancel(): void {
    this.chickSaved.emit(null);
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
