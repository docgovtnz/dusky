import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { FertileEggService } from '../fertileegg.service';
import { FertileEggEntity } from '../../domain/fertileegg.entity';
import { OptionsService } from '../../common/options.service';

import { combineLatest } from 'rxjs/index';
import { ValidationMessage } from '../../domain/response/validation-message';
import { FormGroup } from '@angular/forms';
import { FormService } from '../../form/form.service';
import { BaseEntityFormFactory } from './../../form/base-entity-form-factory';

@Component({
  selector: 'app-fertileegg-edit',
  templateUrl: 'fertileegg-edit.component.html',
})
export class FertileEggEditComponent implements OnInit {
  // New Form layout

  private _fertileeggEntity: FertileEggEntity;
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
    private service: FertileEggService
  ) {}

  ngOnInit(): void {
    // Take the latest value from each of the supplied observables and execute the subscriber
    // function. Note: the subscribe method isn't called until all observables have issued at
    // least one value. This subscription will keep being fired when there are more values,
    // however the createFormFactory() observable should only issue one value.
    combineLatest(
      this.formService.createFormFactory('FertileEggEntity')
    ).subscribe(([formFactory]) => {
      this.formFactory = formFactory;
      this.newEntity();
    });
  }

  get fertileeggEntity(): FertileEggEntity {
    return this._fertileeggEntity;
  }

  set fertileeggEntity(value: FertileEggEntity) {
    this._fertileeggEntity = value;
    if (this.formFactory) {
      this.myFormGroup = this.formFactory.createForm(value);
      this.myFormGroup.patchValue(this._fertileeggEntity);
    }
  }

  newEntity() {
    const e: FertileEggEntity = new FertileEggEntity();
    // set value after init so that form gets patched correctly
    this.fertileeggEntity = e;
  }

  loadEntity(id: string) {
    this.service.findById(id).subscribe((entity) => {
      this.fertileeggEntity = entity;
    });
  }

  onSave(): void {
    this.messages = null;
    Object.assign(this.fertileeggEntity, this.myFormGroup.getRawValue());
    this.service.save(this.fertileeggEntity).subscribe((response) => {
      this._fertileeggEntity = response.model;
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
