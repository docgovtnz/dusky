import { combineLatest } from 'rxjs/index';

import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { IslandService } from '../island.service';
import { IslandEntity } from '../../../domain/island.entity';
import { OptionsService } from '../../../common/options.service';
import { ValidationMessage } from '../../../domain/response/validation-message';
import { FormService } from '../../../form/form.service';
import { BaseEntityFormFactory } from '../../../form/base-entity-form-factory';

@Component({
  selector: 'app-island-edit',
  templateUrl: 'island-edit.component.html',
})
export class IslandEditComponent implements OnInit {
  // New Form layout

  private _islandEntity: IslandEntity;
  private formFactory: BaseEntityFormFactory;
  messages: ValidationMessage[];

  myFormGroup: FormGroup;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public optionsService: OptionsService,
    private formService: FormService,
    private service: IslandService
  ) {}

  ngOnInit(): void {
    // Take the latest value from each of the supplied observables and execute the subscriber
    // function. Note: the subscribe method isn't called until all observables have issued at
    // least one value. This subscription will keep being fired when there are more values,
    // however the createFormFactory() observable should only issue one value.
    combineLatest(
      this.route.params,
      this.route.queryParams,
      this.formService.createFormFactory('IslandEntity')
    ).subscribe(([params, queryParams, formFactory]) => {
      this.formFactory = formFactory;
      if (queryParams.newEntity) {
        this.newEntity();
      } else {
        this.loadEntity(params.id);
      }
    });
  }

  get islandEntity(): IslandEntity {
    return this._islandEntity;
  }

  set islandEntity(value: IslandEntity) {
    this._islandEntity = value;
    if (this.formFactory) {
      this.myFormGroup = this.formFactory.createForm(value);
      this.myFormGroup.patchValue(this._islandEntity);
    }
  }

  newEntity() {
    const e: IslandEntity = new IslandEntity();
    // set value after init so that form gets patched correctly
    this.islandEntity = e;
  }

  loadEntity(id: string) {
    this.service.findById(id).subscribe((entity) => {
      this.islandEntity = entity;
    });
  }

  onSave(): void {
    this.messages = null;
    Object.assign(this.islandEntity, this.myFormGroup.getRawValue());
    this.service.save(this.islandEntity).subscribe((response) => {
      this._islandEntity = response.model;
      this.messages = response.messages;
      if (this.messages.length === 0) {
        this.onCancel();
      }
    });
  }

  onCancel(): void {
    if (this.islandEntity.id) {
      this.router.navigate(['/settings/island/' + this.islandEntity.id]);
    } else {
      this.router.navigate(['/settings/island']);
    }
  }
}
