import { Injectable } from '@angular/core';
import { ValidationService } from './validation.service';
import { FormBuilder } from '@angular/forms';
import { forkJoin, Observable } from 'rxjs';
import { MetaDataService } from '../meta/meta-data.service';
import { map } from 'rxjs/operators';
import { BaseEntityFormFactory } from './base-entity-form-factory';

@Injectable({
  providedIn: 'root',
})
export class FormService {
  constructor(
    private validationService: ValidationService,
    private metaDataService: MetaDataService,
    private fb: FormBuilder
  ) {}

  public createFormFactory(
    entityClassName: string
  ): Observable<BaseEntityFormFactory> {
    return forkJoin(
      this.metaDataService.getMetaClass(entityClassName),
      this.validationService.getDataValidationClass(entityClassName)
    ).pipe(
      map(
        ([metaClass, dataValidationClass]) =>
          new BaseEntityFormFactory(
            metaClass,
            dataValidationClass,
            this.fb,
            this.validationService
          )
      )
    );
  }
}
