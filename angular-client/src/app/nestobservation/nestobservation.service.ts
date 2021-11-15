import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { NestObservationBaseService } from './nestobservation-base.service';
import { PagedResponse } from '../domain/response/paged-response';
import { Observable } from 'rxjs/index';
import { RecordEntity } from '../domain/record.entity';
import { NestObservationEntity } from '../domain/nestobservation.entity';
import { NestObservationCriteria } from './nestobservation.criteria';

@Injectable()
export class NestObservationService extends NestObservationBaseService {
  public nestobservationCriteria: NestObservationCriteria;

  constructor(http: HttpClient) {
    super(http);
  }

  public search(criteria: any): Observable<PagedResponse> {
    return this.http.post<PagedResponse>(
      '/api/nestobservation/searchDTO',
      criteria
    );
  }

  public getEggRecords(nestObservationID: string): Observable<RecordEntity[]> {
    return this.http.get<RecordEntity[]>(
      '/api/nestobservation/' + nestObservationID + '/eggRecords'
    );
  }

  public getChickRecords(
    nestObservationID: string
  ): Observable<RecordEntity[]> {
    return this.http.get<RecordEntity[]>(
      '/api/nestobservation/' + nestObservationID + '/chickRecords'
    );
  }

  public findByRecordID(recordID: string) {
    return this.http.get<NestObservationEntity>(
      '/api/nestobservation?recordID=' + recordID
    );
  }
}
