import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { SnarkActivityService } from '../snarkactivity.service';
import { SnarkActivityEntity } from '../../domain/snarkactivity.entity';
import { OptionsService } from '../../common/options.service';

import { combineLatest } from 'rxjs/index';
import { ValidationMessage } from '../../domain/response/validation-message';
import { FormGroup } from '@angular/forms';
import { FormService } from '../../form/form.service';
import { BaseEntityFormFactory } from './../../form/base-entity-form-factory';
import { TrackAndBowlActivityEntity } from '../../domain/trackandbowlactivity.entity';

@Component({
  selector: 'app-snarkactivity-edit',
  templateUrl: 'snarkactivity-edit.component.html',
})
export class SnarkActivityEditComponent implements OnInit {
  // New Form layout

  private _snarkactivityEntity: SnarkActivityEntity;
  private formFactory: BaseEntityFormFactory;
  messages: ValidationMessage[];

  myFormGroup: FormGroup;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public optionsService: OptionsService,
    private formService: FormService,
    private service: SnarkActivityService
  ) {}

  ngOnInit(): void {
    // Take the latest value from each of the supplied observables and execute the subscriber
    // function. Note: the subscribe method isn't called until all observables have issued at
    // least one value. This subscription will keep being fired when there are more values,
    // however the createFormFactory() observable should only issue one value.
    combineLatest(
      this.route.params,
      this.route.queryParams,
      this.formService.createFormFactory('SnarkActivityEntity')
    ).subscribe(([params, queryParams, formFactory]) => {
      this.formFactory = formFactory;
      if (queryParams.newEntity) {
        this.newEntity();
      } else {
        this.loadEntity(params.id);
      }
    });
  }

  get snarkactivityEntity(): SnarkActivityEntity {
    return this._snarkactivityEntity;
  }

  set snarkactivityEntity(value: SnarkActivityEntity) {
    this._snarkactivityEntity = value;
    if (this.formFactory) {
      this.myFormGroup = this.formFactory.createForm(value);
      this.myFormGroup.patchValue(this._snarkactivityEntity);
    }
  }

  newEntity() {
    const e: SnarkActivityEntity = new SnarkActivityEntity();
    // set value after init so that form gets patched correctly
    this.snarkactivityEntity = e;
  }

  loadEntity(id: string) {
    this.service.findById(id).subscribe((entity) => {
      this.snarkactivityEntity = entity;
    });
  }

  onSave(): void {
    this.messages = null;
    Object.assign(this.snarkactivityEntity, this.myFormGroup.getRawValue());
    if (!this.myFormGroup.controls.trackAndBowlActivity) {
      this.snarkactivityEntity.trackAndBowlActivity = null;
    }
    this.service.save(this.snarkactivityEntity).subscribe((response) => {
      this._snarkactivityEntity = response.model;
      this.messages = response.messages;
      if (this.messages.length === 0) {
        this.onCancel();
      }
    });
  }

  onCancel(): void {
    if (this.snarkactivityEntity.id) {
      this.router.navigate(['/snarkactivity/' + this.snarkactivityEntity.id]);
    } else {
      this.router.navigate(['/snarkactivity']);
    }
  }

  get trackAndBowlActivity(): boolean {
    return this.myFormGroup && this.myFormGroup.controls.trackAndBowlActivity
      ? true
      : false;
  }

  set trackAndBowlActivity(value: boolean) {
    if (this.trackAndBowlActivity !== value) {
      if (value) {
        (this.myFormGroup as any).trackAndBowlActivityAdd(
          new TrackAndBowlActivityEntity()
        );
      } else {
        (this.myFormGroup as any).trackAndBowlActivityRemove();
      }
    }
  }

  get snarkRecordList(): boolean {
    return this.myFormGroup && this.myFormGroup.controls.snarkRecordList
      ? true
      : false;
  }

  set snarkRecordList(value: boolean) {
    if (this.snarkRecordList !== value) {
      if (value) {
        (this.myFormGroup as any).snarkRecordListAdd([]);
      } else {
        (this.myFormGroup as any).snarkRecordListRemove();
      }
    }
  }
}
