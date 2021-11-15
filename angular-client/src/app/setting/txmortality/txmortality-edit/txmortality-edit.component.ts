import { combineLatest } from 'rxjs/index';

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormGroup } from '@angular/forms';

import { TxMortalityService } from '../txmortality.service';
import { TxMortalityEntity } from '../../../domain/txmortality.entity';
import { OptionsService } from '../../../common/options.service';
import { ValidationMessage } from '../../../domain/response/validation-message';
import { FormService } from '../../../form/form.service';
import { BaseEntityFormFactory } from '../../../form/base-entity-form-factory';

@Component({
  selector: 'app-txmortality-edit',
  templateUrl: 'txmortality-edit.component.html',
})
export class TxMortalityEditComponent implements OnInit {
  // New Form layout

  private _txmortalityEntity: TxMortalityEntity;
  private formFactory: BaseEntityFormFactory;
  messages: ValidationMessage[];

  myFormGroup: FormGroup;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public optionsService: OptionsService,
    private formService: FormService,
    private service: TxMortalityService
  ) {}

  ngOnInit(): void {
    // Take the latest value from each of the supplied observables and execute the subscriber
    // function. Note: the subscribe method isn't called until all observables have issued at
    // least one value. This subscription will keep being fired when there are more values,
    // however the createFormFactory() observable should only issue one value.
    combineLatest(
      this.route.params,
      this.route.queryParams,
      this.formService.createFormFactory('TxMortalityEntity')
    ).subscribe(([params, queryParams, formFactory]) => {
      this.formFactory = formFactory;
      if (queryParams.newEntity) {
        this.newEntity();
      } else {
        this.loadEntity(params.id);
      }
    });
  }

  get txmortalityEntity(): TxMortalityEntity {
    return this._txmortalityEntity;
  }

  set txmortalityEntity(value: TxMortalityEntity) {
    this._txmortalityEntity = value;
    if (this.formFactory) {
      this.myFormGroup = this.formFactory.createForm(value);
      this.myFormGroup.patchValue(this._txmortalityEntity);
    }
  }

  newEntity() {
    const e: TxMortalityEntity = new TxMortalityEntity();
    // set value after init so that form gets patched correctly
    this.txmortalityEntity = e;
  }

  loadEntity(id: string) {
    this.service.findById(id).subscribe((entity) => {
      this.txmortalityEntity = entity;
    });
  }

  onSave(): void {
    this.messages = null;
    Object.assign(this.txmortalityEntity, this.myFormGroup.getRawValue());
    this.service.save(this.txmortalityEntity).subscribe((response) => {
      this._txmortalityEntity = response.model;
      this.messages = response.messages;
      if (this.messages.length === 0) {
        this.onCancel();
      }
    });
  }

  onCancel(): void {
    if (this.txmortalityEntity.id) {
      this.router.navigate([
        '/settings/txmortality/' + this.txmortalityEntity.id,
      ]);
    } else {
      this.router.navigate(['/settings/txmortality']);
    }
  }
}
