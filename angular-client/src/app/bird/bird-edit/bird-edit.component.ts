import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BirdService } from '../bird.service';
import { BirdEntity } from '../../domain/bird.entity';
import { OptionsService } from '../../common/options.service';

import { combineLatest } from 'rxjs/index';
import { ValidationMessage } from '../../domain/response/validation-message';
import { FormGroup } from '@angular/forms';
import { FormService } from '../../form/form.service';
import { BaseEntityFormFactory } from './../../form/base-entity-form-factory';
import { EggMeasurementsEntity } from '../../domain/eggmeasurements.entity';
import { EmbryoMeasurementsEntity } from '../../domain/embryomeasurements.entity';

@Component({
  selector: 'app-bird-edit',
  templateUrl: 'bird-edit.component.html',
})
export class BirdEditComponent implements OnInit {
  // New Form layout

  private _birdEntity: BirdEntity;
  private formFactory: BaseEntityFormFactory;
  messages: ValidationMessage[];

  myFormGroup: FormGroup;

  newEntity() {
    const e: BirdEntity = new BirdEntity();
    // default new birds to alive
    e.alive = true;
    // ensure we have a bird feature list to enter and save
    e.birdFeatureList = [];
    e.eggMeasurements = new EggMeasurementsEntity();
    e.embryoMeasurements = new EmbryoMeasurementsEntity();

    // set value after init so that form gets patched correctly
    this.birdEntity = e;
  }

  loadEntity(id: string) {
    this.service.findById(id).subscribe((entity) => {
      this.birdEntity = entity;
    });
  }

  get birdEntity(): BirdEntity {
    return this._birdEntity;
  }

  set birdEntity(value: BirdEntity) {
    this._birdEntity = value;
    if (this.formFactory) {
      this.myFormGroup = this.formFactory.createForm(value);
      this.myFormGroup.patchValue(this._birdEntity);
    }
  }

  onSave(): void {
    this.messages = null;
    Object.assign(this.birdEntity, this.myFormGroup.getRawValue());
    this.service.save(this.birdEntity).subscribe((response) => {
      this.birdEntity = response.model;
      this.messages = response.messages;
      if (this.messages.length === 0) {
        this.onCancel();
      }
    });
  }

  onCancel(): void {
    if (this.birdEntity.id) {
      this.router.navigate(['/bird/' + this.birdEntity.id]);
    } else {
      this.router.navigate(['/bird']);
    }
  }

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public optionsService: OptionsService,
    private formService: FormService,
    private service: BirdService
  ) {}

  ngOnInit(): void {
    // Take the latest value from each of the supplied observables and execute the subscriber
    // function. Note: the subscribe method isn't called until all observables have issued at
    // least one value. This subscription will keep being fired when there are more values,
    // however the createFormFactory() observable should only issue one value.
    combineLatest(
      this.route.params,
      this.route.queryParams,
      this.formService.createFormFactory('BirdEntity')
    ).subscribe(([params, queryParams, formFactory]) => {
      this.formFactory = formFactory;

      if (queryParams.newEntity) {
        this.newEntity();
      } else {
        this.loadEntity(params.id);
      }
    });
  }
}
