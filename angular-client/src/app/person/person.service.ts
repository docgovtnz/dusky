import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { PersonBaseService } from './person-base.service';
import { PersonEntity } from '../domain/person.entity';
import { Observable } from 'rxjs/index';
import { Response } from '../domain/response/response';
import { tap } from 'rxjs/operators';
import { OptionsService } from '../common/options.service';
import { PersonCriteria } from './person.criteria';

@Injectable()
export class PersonService extends PersonBaseService {
  public personCriteria: PersonCriteria;

  constructor(http: HttpClient, private optionsService: OptionsService) {
    super(http);
  }

  public save(entity: PersonEntity): Observable<Response<PersonEntity>> {
    return super
      .save(entity)
      .pipe(tap(() => this.optionsService.resetPersonCache()));
  }

  public delete(docId: string): Observable<any> {
    return super
      .delete(docId)
      .pipe(tap(() => this.optionsService.resetPersonCache()));
  }
}
