import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { SampleEntity } from '../domain/sample.entity';
import { PagedResponse } from '../domain/response/paged-response';
import { DeleteByIdCheckDto } from '../domain/response/delete-by-id-check-dto';
import { Response } from '../domain/response/response';

export abstract class SampleBaseService {
  constructor(protected http: HttpClient) {}

  public search(criteria: any): Observable<PagedResponse> {
    return this.http.post<PagedResponse>('/api/sample/search', criteria);
  }

  public findById(docId: string): Observable<SampleEntity> {
    return this.http.get<SampleEntity>('/api/sample/' + docId).pipe(
      map((response: SampleEntity) => {
        response.collectionDate =
          response.collectionDate === null
            ? null
            : new Date(response.collectionDate);
        response.modifiedTime =
          response.modifiedTime === null
            ? null
            : new Date(response.modifiedTime);
        return response;
      })
    );
  }

  public save(entity: SampleEntity): Observable<Response<SampleEntity>> {
    return this.http.post<Response<SampleEntity>>('/api/sample/', entity);
  }

  public delete(docId: string): Observable<any> {
    return this.http.delete<any>('/api/sample/' + docId);
  }

  public deleteByIdCheck(docId: string): Observable<DeleteByIdCheckDto> {
    return this.http.delete<any>('/api/sample/' + docId + '/check');
  }
}
