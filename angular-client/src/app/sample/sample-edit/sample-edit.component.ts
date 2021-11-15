import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { SampleService } from '../sample.service';
import { SampleEntity } from '../../domain/sample.entity';
import { OptionsService } from '../../common/options.service';

import { combineLatest } from 'rxjs/index';
import { ValidationMessage } from '../../domain/response/validation-message';
import { FormGroup } from '@angular/forms';
import { FormService } from '../../form/form.service';
import { BaseEntityFormFactory } from './../../form/base-entity-form-factory';

@Component({
  selector: 'app-sample-edit',
  templateUrl: 'sample-edit.component.html',
})
export class SampleEditComponent implements OnInit {
  static partsMapping = [
    {
      parts: [
        'bloodDetail',
        'haematologyTestList',
        'chemistryAssayList',
        'microbiologyAndParasitologyTestList',
      ],
      parameters: { sampleCategory: 'Blood' },
    },
    {
      parts: ['swabDetail', 'microbiologyAndParasitologyTestList'],
      parameters: { sampleCategory: 'Swab' },
    },
    {
      parts: [
        'otherDetail',
        'haematologyTestList',
        'chemistryAssayList',
        'microbiologyAndParasitologyTestList',
      ],
      parameters: { sampleCategory: 'Other' },
    },
    {
      parts: ['spermDetail', 'spermMeasureList'],
      parameters: { sampleCategory: 'Sperm' },
    },
  ];

  // New Form layout

  private _sampleEntity: SampleEntity;
  private formFactory: BaseEntityFormFactory;
  messages: ValidationMessage[];

  myFormGroup: FormGroup;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public optionsService: OptionsService,
    private formService: FormService,
    private service: SampleService
  ) {}

  ngOnInit(): void {
    // Take the latest value from each of the supplied observables and execute the subscriber
    // function. Note: the subscribe method isn't called until all observables have issued at
    // least one value. This subscription will keep being fired when there are more values,
    // however the createFormFactory() observable should only issue one value.
    combineLatest(
      this.route.params,
      this.formService.createFormFactory('SampleEntity')
    ).subscribe(([params, formFactory]) => {
      this.formFactory = formFactory;
      this.loadEntity(params.id);
    });
  }

  get sampleEntity(): SampleEntity {
    return this._sampleEntity;
  }

  set sampleEntity(value: SampleEntity) {
    this._sampleEntity = value;
    this.syncForm();
  }

  syncForm(): void {
    if (this.formFactory) {
      this.myFormGroup = this.formFactory.createForm(this._sampleEntity);
      this.myFormGroup.patchValue(this._sampleEntity);
    }
  }

  loadEntity(id: string) {
    this.service.findById(id).subscribe((entity) => {
      this.sampleEntity = entity;
      this.addRemoveParts(
        { sampleCategory: entity.sampleCategory },
        this.myFormGroup
      );
    });
  }

  onSave(): void {
    this.messages = [];
    this.syncSampleEntity();
    this.service.save(this.sampleEntity).subscribe((response) => {
      this.sampleEntity = response.model;
      this.messages = response.messages;
      if (this.messages.length === 0) {
        this.onCancel();
      }
    });
  }

  syncSampleEntity(): void {
    // TODO this code is very similar to RecordEditComponent.syncRecordEntity()
    const raw = this.myFormGroup.getRawValue();
    Object.keys(raw).forEach((k) => {
      // if the part exists on the record entity and is not an array
      if (this.parts.includes(k) && this._sampleEntity[k]) {
        if (!Array.isArray(this._sampleEntity[k])) {
          // save over the top of old value, preserving any existing values not updated by the form
          Object.assign(this._sampleEntity[k], raw[k]);
        } else {
          const originalArray = this._sampleEntity[k];
          // create a new array combining new items with existing items with new context saved over top
          const newArray = [];
          const rawArray = raw[k];
          rawArray.forEach((item) => {
            if (item._originalIndex !== null) {
              const originalItem = originalArray[item._originalIndex];
              Object.assign(originalItem, item);
              delete originalItem['_originalIndex'];
              newArray.push(originalItem);
            } else {
              newArray.push(item);
            }
          });
          this._sampleEntity[k] = newArray;
        }
      } else {
        // use the form value as the new value for the part
        this._sampleEntity[k] = raw[k];
      }
    });
    // remove any part for which there is no longer a control group
    this.parts.forEach((p) => {
      if (!this.myFormGroup.controls[p]) {
        this._sampleEntity[p] = null;
      }
    });
    // remove any part for which all contained values are null
    this.parts.forEach((p) => {
      const partObject = this._sampleEntity[p];
      if (partObject) {
        // TODO decide whether to move this logic into record edit also
        if (Array.isArray(partObject)) {
          if (partObject.length === 0) {
            this._sampleEntity[p] = null;
          }
        } else {
          // start by changing any empty string values to null
          Object.keys(partObject).forEach((k) => {
            if (partObject[k] === '') {
              partObject[k] = null;
            }
          });
          // now if all contained values are null, remove the part
          if (Object.keys(partObject).every((k) => partObject[k] === null)) {
            this._sampleEntity[p] = null;
          }
        }
      }
    });
  }

  onCancel(): void {
    if (this.sampleEntity.id) {
      this.router.navigate(['/sample/' + this.sampleEntity.id]);
    } else {
      this.router.navigate(['/sample']);
    }
  }

  private addRemoveParts(
    parameters: { sampleCategory: string },
    fg: FormGroup
  ): void {
    this.addParts(parameters, fg);
    this.removeParts(parameters, fg);
  }

  private addParts(
    parameters: { sampleCategory: string },
    fg: FormGroup
  ): void {
    const partsToAdd = this.getMatchingParts(parameters);
    partsToAdd.forEach((p) => {
      if (!fg.controls[p] && fg[p + 'Add']) {
        fg[p + 'Add']();
      }
    });
  }

  private removeParts(
    parameters: { sampleCategory: string },
    fg: FormGroup
  ): void {
    const partsToAdd = this.getMatchingParts(parameters);
    const partsToRemove = this.parts.filter((i) => partsToAdd.indexOf(i) < 0);
    partsToRemove.forEach((p) => {
      if (fg.controls[p] && fg[p + 'Remove']) {
        fg[p + 'Remove']();
      }
    });
  }

  private get parts(): string[] {
    let parts = [];
    SampleEditComponent.partsMapping.forEach(
      (i) => (parts = parts.concat(i.parts))
    );
    // sort and filter out duplicates
    parts = parts.sort().filter((v, i, a) => a.indexOf(v) === i);
    return parts;
  }

  private getMatchingParts(parameters: { sampleCategory: string }): string[] {
    const matches: any[] = SampleEditComponent.partsMapping.filter((entry) => {
      const entryKeys = Object.keys(entry.parameters);
      const parameterKeys = Object.keys(parameters);
      // this is a possible match if
      // there is a value for each parameter from the entry
      // and the value for the parameter matches the value for the entry
      return entryKeys.every(
        (i) =>
          parameterKeys.includes(i) && parameters[i] === entry.parameters[i]
      );
    });
    // if there are no matches then return [] (which means no parts)
    if (matches.length === 0) {
      console.log(
        `No parts list found for parameters ${JSON.stringify(parameters)}`
      );
      // return [] to signify there is no parts (and keep things error free for user)
      return [];
    }
    // now determine what is the most specific match based on the number of parameters matched
    // if there is a draw then throw an error because this is a conflict
    // first add a parameterCount field to each of the entries
    const matchesWithCounts = matches.map((entry) => ({
      parameterCount: Object.keys(entry.parameters).length,
      parameters: entry.parameters,
      parts: entry.parts,
    }));
    // use the added parameterCount information to get the highest count
    const highestCount = matchesWithCounts
      .map((entry) => entry.parameterCount)
      .sort()
      .reverse()[0];
    // find all the entries with the highest count
    const matchesWithHighestCount = matchesWithCounts.filter(
      (i) => i.parameterCount === highestCount
    );
    // if we have only one the we have a match, return the parts of the entry
    if (matchesWithHighestCount.length === 1) {
      return matchesWithHighestCount[0].parts;
    } else {
      // we have more than one match so throw an error with details of the matching entries
      console.log(
        `Multiple parts list found for parameters ${JSON.stringify(parameters)}`
      );
      // return [] to signify there are no parts (and keep things error free for user)
      return [];
    }
  }
}
