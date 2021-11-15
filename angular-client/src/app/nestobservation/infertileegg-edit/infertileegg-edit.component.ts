import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { InfertileEggService } from '../infertileegg.service';
import { InfertileEggEntity } from '../../domain/infertileegg.entity';
import { OptionsService } from '../../common/options.service';

import { combineLatest } from 'rxjs/index';
import { ValidationMessage } from '../../domain/response/validation-message';
import { FormGroup } from '@angular/forms';
import { FormService } from '../../form/form.service';
import { BaseEntityFormFactory } from './../../form/base-entity-form-factory';
import { EggMeasurementsEntity } from 'src/app/domain/eggmeasurements.entity';

@Component({
  selector: 'app-infertileegg-edit',
  templateUrl: 'infertileegg-edit.component.html',
})
export class InfertileEggEditComponent implements OnInit {
  // New Form layout

  private _infertileeggEntity: InfertileEggEntity;
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
    private service: InfertileEggService
  ) {}

  ngOnInit(): void {
    // Take the latest value from each of the supplied observables and execute the subscriber
    // function. Note: the subscribe method isn't called until all observables have issued at
    // least one value. This subscription will keep being fired when there are more values,
    // however the createFormFactory() observable should only issue one value.
    combineLatest(
      this.formService.createFormFactory('InfertileEggEntity')
    ).subscribe(([formFactory]) => {
      this.formFactory = formFactory;
      this.newEntity();
    });
  }

  get infertileeggEntity(): InfertileEggEntity {
    return this._infertileeggEntity;
  }

  set infertileeggEntity(value: InfertileEggEntity) {
    this._infertileeggEntity = value;
    if (this.formFactory) {
      this.myFormGroup = this.formFactory.createForm(value);
      this.myFormGroup.patchValue(this._infertileeggEntity);
    }
  }

  newEntity() {
    const e: InfertileEggEntity = new InfertileEggEntity();
    e.eggMeasurements = new EggMeasurementsEntity();
    // set value after init so that form gets patched correctly
    this.infertileeggEntity = e;
  }

  loadEntity(id: string) {
    this.service.findById(id).subscribe((entity) => {
      this.infertileeggEntity = entity;
    });
  }

  onSave(): void {
    this.messages = null;
    Object.assign(this.infertileeggEntity, this.myFormGroup.getRawValue());
    this.service.save(this.infertileeggEntity).subscribe((response) => {
      this._infertileeggEntity = response.model;
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
