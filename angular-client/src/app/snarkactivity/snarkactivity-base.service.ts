import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { SnarkActivityEntity } from '../domain/snarkactivity.entity';
import { PagedResponse } from '../domain/response/paged-response';
import { DeleteByIdCheckDto } from '../domain/response/delete-by-id-check-dto';
import { Response } from '../domain/response/response';

export abstract class SnarkActivityBaseService {
  constructor(protected http: HttpClient) {}

  public search(criteria: any): Observable<PagedResponse> {
    return this.http.post<PagedResponse>('/api/snarkactivity/search', criteria);
  }

  public findById(docId: string): Observable<SnarkActivityEntity> {
    return this.http
      .get<SnarkActivityEntity>('/api/snarkactivity/' + docId)
      .pipe(
        map((response: SnarkActivityEntity) => {
          response.date =
            response.date === null ? null : new Date(response.date);
          response.modifiedTime =
            response.modifiedTime === null
              ? null
              : new Date(response.modifiedTime);
          return response;
        })
      );
  }

  public save(
    entity: SnarkActivityEntity
  ): Observable<Response<SnarkActivityEntity>> {
    return this.http.post<Response<SnarkActivityEntity>>(
      '/api/snarkactivity/',
      entity
    );
  }

  public delete(docId: string): Observable<any> {
    return this.http.delete<any>('/api/snarkactivity/' + docId);
  }

  public deleteByIdCheck(docId: string): Observable<DeleteByIdCheckDto> {
    return this.http.delete<any>('/api/snarkactivity/' + docId + '/check');
  }
}
