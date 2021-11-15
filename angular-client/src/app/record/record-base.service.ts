import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { RecordEntity } from '../domain/record.entity';
import { PagedResponse } from '../domain/response/paged-response';
import { DeleteByIdCheckDto } from '../domain/response/delete-by-id-check-dto';
import { Response } from '../domain/response/response';

export abstract class RecordBaseService {
  constructor(protected http: HttpClient) {}

  public search(criteria: any): Observable<PagedResponse> {
    return this.http.post<PagedResponse>('/api/record/search', criteria);
  }

  public findById(docId: string): Observable<RecordEntity> {
    return this.http.get<RecordEntity>('/api/record/' + docId).pipe(
      map((response: RecordEntity) => {
        response.dateTime =
          response.dateTime === null ? null : new Date(response.dateTime);
        response.entryDateTime =
          response.entryDateTime === null
            ? null
            : new Date(response.entryDateTime);
        response.firstArriveTime =
          response.firstArriveTime === null
            ? null
            : new Date(response.firstArriveTime);
        response.lastDepartTime =
          response.lastDepartTime === null
            ? null
            : new Date(response.lastDepartTime);
        response.modifiedTime =
          response.modifiedTime === null
            ? null
            : new Date(response.modifiedTime);
        return response;
      })
    );
  }

  public save(entity: RecordEntity): Observable<Response<RecordEntity>> {
    return this.http.post<Response<RecordEntity>>('/api/record/', entity);
  }

  public delete(docId: string): Observable<any> {
    return this.http.delete<any>('/api/record/' + docId);
  }

  public deleteByIdCheck(docId: string): Observable<DeleteByIdCheckDto> {
    return this.http.delete<any>('/api/record/' + docId + '/check');
  }
}
