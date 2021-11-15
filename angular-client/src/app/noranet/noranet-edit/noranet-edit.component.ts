import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NoraNetService } from '../noranet.service';
import { NoraNetEntity } from '../entities/noranet.entity';
import { OptionsService } from '../../common/options.service';

import { combineLatest } from 'rxjs/index';
import { ValidationMessage } from '../../domain/response/validation-message';
import { FormGroup } from '@angular/forms';
import { FormService } from '../../form/form.service';
import { BaseEntityFormFactory } from '../../form/base-entity-form-factory';

@Component({
  selector: 'app-noranet-edit',
  templateUrl: 'noranet-edit.component.html',
})
export class NoraNetEditComponent implements OnInit {
  // New Form layout

  private _noranetEntity: NoraNetEntity;
  private formFactory: BaseEntityFormFactory;
  messages: ValidationMessage[];

  myFormGroup: FormGroup;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public optionsService: OptionsService,
    private formService: FormService,
    private service: NoraNetService
  ) {}

  ngOnInit(): void {
    // Take the latest value from each of the supplied observables and execute the subscriber
    // function. Note: the subscribe method isn't called until all observables have issued at
    // least one value. This subscription will keep being fired when there are more values,
    // however the createFormFactory() observable should only issue one value.
    combineLatest(
      this.route.params,
      this.route.queryParams,
      this.formService.createFormFactory('NoraNetEntity')
    ).subscribe(([params, queryParams, formFactory]) => {
      this.formFactory = formFactory;
      if (queryParams.newEntity) {
        this.newEntity();
      } else {
        this.loadEntity(params.id);
      }
    });
  }

  get noranetEntity(): NoraNetEntity {
    return this._noranetEntity;
  }

  set noranetEntity(value: NoraNetEntity) {
    this._noranetEntity = value;
    if (this.formFactory) {
      this.myFormGroup = this.formFactory.createForm(value);
      this.myFormGroup.patchValue(this._noranetEntity);
    }
  }

  newEntity() {
    const e = new NoraNetEntity();
    // set value after init so that form gets patched correctly
    this.noranetEntity = e;
  }

  loadEntity(id: string) {
    this.service.findById(id).subscribe((entity) => {
      this.noranetEntity = entity;
    });
  }

  onSave(): void {
    this.messages = null;
    Object.assign(this.noranetEntity, this.myFormGroup.getRawValue());
    this.service.save(this.noranetEntity).subscribe((response) => {
      this._noranetEntity = response.model;
      this.messages = response.messages;
      if (this.messages.length === 0) {
        this.onCancel();
      }
    });
  }

  onCancel(): void {
    if (this.noranetEntity.id) {
      this.router.navigate(['/noranet/' + this.noranetEntity.id]);
    } else {
      this.router.navigate(['/noranet']);
    }
  }
}
