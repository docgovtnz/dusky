import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { HatchService } from '../hatch.service';
import { HatchEntity } from '../../domain/hatch.entity';
import { OptionsService } from '../../common/options.service';

import { combineLatest } from 'rxjs/index';
import { ValidationMessage } from '../../domain/response/validation-message';
import { FormGroup } from '@angular/forms';
import { FormService } from '../../form/form.service';
import { BaseEntityFormFactory } from './../../form/base-entity-form-factory';

@Component({
  selector: 'app-hatch-edit',
  templateUrl: 'hatch-edit.component.html',
})
export class HatchEditComponent implements OnInit {
  // New Form layout

  private _hatchEntity: HatchEntity;
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
    private service: HatchService
  ) {}

  ngOnInit(): void {
    // Take the latest value from each of the supplied observables and execute the subscriber
    // function. Note: the subscribe method isn't called until all observables have issued at
    // least one value. This subscription will keep being fired when there are more values,
    // however the createFormFactory() observable should only issue one value.
    combineLatest(this.formService.createFormFactory('HatchEntity')).subscribe(
      ([formFactory]) => {
        this.formFactory = formFactory;
        this.newEntity();
      }
    );
  }

  get hatchEntity(): HatchEntity {
    return this._hatchEntity;
  }

  set hatchEntity(value: HatchEntity) {
    this._hatchEntity = value;
    if (this.formFactory) {
      this.myFormGroup = this.formFactory.createForm(value);
      this.myFormGroup.patchValue(this._hatchEntity);
    }
  }

  newEntity() {
    const e: HatchEntity = new HatchEntity();
    // set value after init so that form gets patched correctly
    this.hatchEntity = e;
  }

  loadEntity(id: string) {
    this.service.findById(id).subscribe((entity) => {
      this.hatchEntity = entity;
    });
  }

  onSave(): void {
    this.messages = null;
    Object.assign(this.hatchEntity, this.myFormGroup.getRawValue());
    this.service.save(this.hatchEntity).subscribe((response) => {
      this._hatchEntity = response.model;
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
  setValues(birdID: string) {
    this.myFormGroup.controls.birdID.setValue(birdID);
  }
}
