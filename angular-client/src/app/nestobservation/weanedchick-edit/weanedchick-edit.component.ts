import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { WeanedChickService } from '../weanedchick.service';
import { WeanedChickEntity } from '../../domain/weanedchick.entity';
import { OptionsService } from '../../common/options.service';

import { combineLatest } from 'rxjs/index';
import { ValidationMessage } from '../../domain/response/validation-message';
import { FormGroup, FormArray } from '@angular/forms';
import { FormService } from '../../form/form.service';
import { BaseEntityFormFactory } from './../../form/base-entity-form-factory';
import { ObserverEntity } from 'src/app/domain/observer.entity';

@Component({
  selector: 'app-weanedchick-edit',
  templateUrl: 'weanedchick-edit.component.html',
})
export class WeanedChickEditComponent implements OnInit {
  // New Form layout

  private _weanedchickEntity: WeanedChickEntity;
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
    private service: WeanedChickService
  ) {}

  ngOnInit(): void {
    // Take the latest value from each of the supplied observables and execute the subscriber
    // function. Note: the subscribe method isn't called until all observables have issued at
    // least one value. This subscription will keep being fired when there are more values,
    // however the createFormFactory() observable should only issue one value.
    combineLatest(
      this.formService.createFormFactory('WeanedChickEntity')
    ).subscribe(([formFactory]) => {
      this.formFactory = formFactory;
      this.newEntity();
    });
  }

  get weanedchickEntity(): WeanedChickEntity {
    return this._weanedchickEntity;
  }

  set weanedchickEntity(value: WeanedChickEntity) {
    this._weanedchickEntity = value;
    if (this.formFactory) {
      this.myFormGroup = this.formFactory.createForm(value);
      this.myFormGroup.patchValue(this._weanedchickEntity);
    }
  }

  newEntity() {
    const e: WeanedChickEntity = new WeanedChickEntity();
    e.observerList = [];
    // set value after init so that form gets patched correctly
    this.weanedchickEntity = e;
  }

  loadEntity(id: string) {
    this.service.findById(id).subscribe((entity) => {
      this.weanedchickEntity = entity;
    });
  }

  onSave(): void {
    this.messages = null;
    Object.assign(this.weanedchickEntity, this.myFormGroup.getRawValue());
    this.service.save(this.weanedchickEntity).subscribe((response) => {
      this._weanedchickEntity = response.model;
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
