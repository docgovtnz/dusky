import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { NestObservationEntity } from '../domain/nestobservation.entity';
import { PagedResponse } from '../domain/response/paged-response';
import { DeleteByIdCheckDto } from '../domain/response/delete-by-id-check-dto';
import { Response } from '../domain/response/response';

export abstract class NestObservationBaseService {
  constructor(protected http: HttpClient) {}

  public search(criteria: any): Observable<PagedResponse> {
    return this.http.post<PagedResponse>(
      '/api/nestobservation/search',
      criteria
    );
  }

  public findById(docId: string): Observable<NestObservationEntity> {
    return this.http
      .get<NestObservationEntity>('/api/nestobservation/' + docId)
      .pipe(
        map((response: NestObservationEntity) => {
          response.chickfirstfeed1 =
            response.chickfirstfeed1 === null
              ? null
              : new Date(response.chickfirstfeed1);
          response.chickfirstfeed2 =
            response.chickfirstfeed2 === null
              ? null
              : new Date(response.chickfirstfeed2);
          response.chickfirstfeed3 =
            response.chickfirstfeed3 === null
              ? null
              : new Date(response.chickfirstfeed3);
          response.chickfirstfeed4 =
            response.chickfirstfeed4 === null
              ? null
              : new Date(response.chickfirstfeed4);
          response.date =
            response.date === null ? null : new Date(response.date);
          response.dateTime =
            response.dateTime === null ? null : new Date(response.dateTime);
          response.finishObservationTime =
            response.finishObservationTime === null
              ? null
              : new Date(response.finishObservationTime);
          response.firstTimeOff =
            response.firstTimeOff === null
              ? null
              : new Date(response.firstTimeOff);
          response.hatchDate =
            response.hatchDate === null ? null : new Date(response.hatchDate);
          response.layDate =
            response.layDate === null ? null : new Date(response.layDate);
          response.modifiedTime =
            response.modifiedTime === null
              ? null
              : new Date(response.modifiedTime);
          response.mumback1 =
            response.mumback1 === null ? null : new Date(response.mumback1);
          response.mumback2 =
            response.mumback2 === null ? null : new Date(response.mumback2);
          response.mumback3 =
            response.mumback3 === null ? null : new Date(response.mumback3);
          response.mumback4 =
            response.mumback4 === null ? null : new Date(response.mumback4);
          response.startObservationTime =
            response.startObservationTime === null
              ? null
              : new Date(response.startObservationTime);
          return response;
        })
      );
  }

  public save(
    entity: NestObservationEntity
  ): Observable<Response<NestObservationEntity>> {
    return this.http.post<Response<NestObservationEntity>>(
      '/api/nestobservation/',
      entity
    );
  }

  public delete(docId: string): Observable<any> {
    return this.http.delete<any>('/api/nestobservation/' + docId);
  }

  public deleteByIdCheck(docId: string): Observable<DeleteByIdCheckDto> {
    return this.http.delete<any>('/api/nestobservation/' + docId + '/check');
  }
}
