import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PersonService } from '../person.service';
import { PersonEntity } from '../../domain/person.entity';
import { OptionsService } from '../../common/options.service';

import { combineLatest } from 'rxjs/index';
import { ValidationMessage } from '../../domain/response/validation-message';
import { FormGroup } from '@angular/forms';
import { FormService } from '../../form/form.service';
import { BaseEntityFormFactory } from './../../form/base-entity-form-factory';

@Component({
  selector: 'app-person-edit',
  templateUrl: 'person-edit.component.html',
})
export class PersonEditComponent implements OnInit {
  // New Form layout

  private _personEntity: PersonEntity;
  private formFactory: BaseEntityFormFactory;
  messages: ValidationMessage[];

  myFormGroup: FormGroup;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public optionsService: OptionsService,
    private formService: FormService,
    private service: PersonService
  ) {}

  ngOnInit(): void {
    // Take the latest value from each of the supplied observables and execute the subscriber
    // function. Note: the subscribe method isn't called until all observables have issued at
    // least one value. This subscription will keep being fired when there are more values,
    // however the createFormFactory() observable should only issue one value.
    combineLatest(
      this.route.params,
      this.route.queryParams,
      this.formService.createFormFactory('PersonEntity')
    ).subscribe(([params, queryParams, formFactory]) => {
      this.formFactory = formFactory;
      if (queryParams.newEntity) {
        this.newEntity();
      } else {
        this.loadEntity(params.id);
      }
    });
  }

  get personEntity(): PersonEntity {
    return this._personEntity;
  }

  set personEntity(value: PersonEntity) {
    this._personEntity = value;
    if (this.formFactory) {
      this.myFormGroup = this.formFactory.createForm(value);
      this.myFormGroup.patchValue(this._personEntity);
    }
  }

  newEntity() {
    const e: PersonEntity = new PersonEntity();
    // set value after init so that form gets patched correctly
    this.personEntity = e;
  }

  loadEntity(id: string) {
    this.service.findById(id).subscribe((entity) => {
      this.personEntity = entity;
    });
  }

  onSave(): void {
    this.messages = null;
    Object.assign(this.personEntity, this.myFormGroup.getRawValue());
    this.service.save(this.personEntity).subscribe((response) => {
      this._personEntity = response.model;
      this.messages = response.messages;
      if (this.messages.length === 0) {
        this.onCancel();
      }
    });
  }

  onCancel(): void {
    if (this.personEntity.id) {
      this.router.navigate(['/person/' + this.personEntity.id]);
    } else {
      this.router.navigate(['/person']);
    }
  }
}
