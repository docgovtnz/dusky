import { Observable, empty } from 'rxjs';
import {
  map,
  switchMap,
  tap,
  catchError,
  filter,
  distinctUntilChanged,
} from 'rxjs/operators';
import { combineLatest } from 'rxjs/index';

import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormGroup } from '@angular/forms';

import { ValidationMessage } from '../../domain/response/validation-message';
import { BaseEntityFormFactory } from './../../form/base-entity-form-factory';

import { InputService } from '../../common/input.service';
import { FormService } from '../../form/form.service';
import { SnarkImportService } from '../snarkimport.service';
import { OptionsService } from '../../common/options.service';

import { SnarkCheckResultPanelComponent } from '../snarkcheckresult-panel/snarkcheckresult-panel.component';
import { SnarkIncludeResultPanelComponent } from '../snarkincluderesult-panel/snarkincluderesult-panel.component';

import { SnarkCheckRequestDTO } from '../snark-check-request-dto';
import { SnarkCheckResultDTO } from '../snark-check-result-dto';
import { SnarkIncludeRequestDTO } from '../snark-include-request-dto';
import { SnarkIncludeResultDTO } from '../snark-include-result-dto';
import { SnarkImportRequest } from '../snark-import-request';
import { SnarkImportEntity } from '../../domain/snarkimport.entity';

@Component({
  selector: 'app-snarkimport-edit',
  templateUrl: 'snarkimport-edit.component.html',
})
export class SnarkImportEditComponent implements OnInit {
  // New Form layout

  private _snarkimportEntity: SnarkImportEntity;
  private formFactory: BaseEntityFormFactory;
  messages: ValidationMessage[];

  myFormGroup: FormGroup;

  snarkCheckResult: SnarkCheckResultDTO;
  snarkIncludeResult: SnarkIncludeResultDTO;

  @ViewChild(SnarkCheckResultPanelComponent, { static: true })
  snarkCheckResultPanel: SnarkCheckResultPanelComponent;

  @ViewChild(SnarkIncludeResultPanelComponent, { static: true })
  snarkIncludeResultPanel: SnarkIncludeResultPanelComponent;

  checkLoading = false;
  includeLoading = false;
  importLoading = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public optionsService: OptionsService,
    private formService: FormService,
    private service: SnarkImportService,
    private clearInputService: InputService
  ) {}

  ngOnInit(): void {
    // Take the latest value from each of the supplied observables and execute the subscriber
    // function. Note: the subscribe method isn't called until all observables have issued at
    // least one value. This subscription will keep being fired when there are more values,
    // however the createFormFactory() observable should only issue one value.
    combineLatest(
      this.route.params,
      this.route.queryParams,
      this.formService.createFormFactory('SnarkImportEntity')
    ).subscribe(([params, queryParams, formFactory]) => {
      this.formFactory = formFactory;
      if (queryParams.newEntity) {
        this.newEntity();
      } else {
        this.loadEntity(params.id);
      }
    });
  }

  get snarkimportEntity(): SnarkImportEntity {
    return this._snarkimportEntity;
  }

  set snarkimportEntity(value: SnarkImportEntity) {
    this._snarkimportEntity = value;
    if (this.formFactory) {
      this.myFormGroup = this.formFactory.createForm(value);
      this.myFormGroup.patchValue(this._snarkimportEntity);
    }
  }

  parseSnarkFile(event) {
    const { snarkFileName, snarkFileContent } = this.myFormGroup.controls;

    if (event) {
      // Derives file name and encodes content on successful addition of file upload
      snarkFileName.setValue(event.name);

      this.readAsBase64(event).subscribe((base64EncodedContent) => {
        snarkFileContent.setValue(base64EncodedContent);
      });
    } else {
      // The file upload widget sends null if files are zero, hence set values of snark related variable to null as well
      snarkFileName.setValue(null);
      snarkFileContent.setValue(null);
    }
  }

  newEntity() {
    const e: SnarkImportEntity = new SnarkImportEntity();
    // Set defaults
    e.activityType = 'Hopper';
    e.qualityOverride = 128;
    e.showLockRecords = false;
    // set value after init so that form gets patched correctly
    this.snarkimportEntity = e;
  }

  loadEntity(id: string) {
    this.service.findById(id).subscribe((entity) => {
      this.snarkimportEntity = entity;
    });
  }

  runCheck(): void {
    this.checkLoading = true;
    this.snarkCheckResult = null;
    this.snarkIncludeResult = null;

    const request = new SnarkCheckRequestDTO();
    Object.assign(request, this.myFormGroup.getRawValue());

    this.doCheck(request);
  }

  runInclude(): void {
    this.includeLoading = true;

    const request = new SnarkIncludeRequestDTO();
    request.eveningList = Object.assign(this.snarkCheckResult.eveningList);

    this.doInclude(request);
  }

  runImport(): void {
    this.messages = null;
    this.importLoading = true;

    const request = new SnarkImportRequest();
    request.eveningList = Object.assign(this.snarkIncludeResult);

    Object.assign(this.snarkimportEntity, this.myFormGroup.getRawValue());
    const copy: SnarkImportEntity = Object.assign(
      {},
      this.myFormGroup.getRawValue()
    );

    request.entity = copy;
    this.doImport(request);
  }

  clear() {
    this.newEntity();
    this.snarkCheckResult = null;
    this.snarkIncludeResult = null;
    this.clearInputService.clearInput(null);
  }

  private doCheck(request: SnarkCheckRequestDTO) {
    this.checkLoading = true;

    this.service.check(request).subscribe((result) => {
      this.snarkCheckResult = result;
      this.checkLoading = false;
    });
  }

  private doInclude(request: SnarkIncludeRequestDTO) {
    this.includeLoading = true;

    this.service.include(request).subscribe((result) => {
      this.snarkIncludeResult = result.filter(
        (evening) => evening.snarkRecordList.length > 0
      ); // Exclude evenings with zero records
      this.includeLoading = false;
    });
  }

  private doImport(request: SnarkImportRequest) {
    this.importLoading = false;

    this.service.import(request).subscribe((result) => {
      this._snarkimportEntity = result.model;
      this.messages = result.messages;
      if (this.messages.length === 0) {
        this.clear();
      }
    });
  }

  private readAsBase64(file: Blob): Observable<string> {
    return Observable.create((obs) => {
      const reader = new FileReader();

      reader.onerror = (err) => obs.error(err);
      reader.onabort = (err) => obs.error(err);
      reader.onload = () => {
        // as per https://stackoverflow.com/questions/36280818/how-to-convert-file-to-base64-in-javascript
        // and https://developer.mozilla.org/en-US/docs/Web/API/FileReader/readAsDataURL
        // removing redundant start of string
        const base64EncodedContent = (reader.result as string).replace(
          /^.*;base64,/,
          ''
        );
        obs.next(base64EncodedContent);
      };
      reader.onloadend = () => obs.complete();

      // reader result will have the base 64 encoded content of the file
      return reader.readAsDataURL(file);
    });
  }
}
