import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { DeadEmbryoEntity } from '../domain/deadembryo.entity';
import { PagedResponse } from '../domain/response/paged-response';
import { DeleteByIdCheckDto } from '../domain/response/delete-by-id-check-dto';
import { Response } from '../domain/response/response';

export abstract class DeadEmbryoBaseService {
  constructor(protected http: HttpClient) {}

  public search(criteria: any): Observable<PagedResponse> {
    return this.http.post<PagedResponse>('/api/deadembryo/search', criteria);
  }

  public findById(docId: string): Observable<DeadEmbryoEntity> {
    return this.http.get<DeadEmbryoEntity>('/api/deadembryo/' + docId).pipe(
      map((response: DeadEmbryoEntity) => {
        response.date = response.date === null ? null : new Date(response.date);
        response.modifiedTime =
          response.modifiedTime === null
            ? null
            : new Date(response.modifiedTime);
        return response;
      })
    );
  }

  public save(
    entity: DeadEmbryoEntity
  ): Observable<Response<DeadEmbryoEntity>> {
    return this.http.post<Response<DeadEmbryoEntity>>(
      '/api/deadembryo/',
      entity
    );
  }

  public delete(docId: string): Observable<any> {
    return this.http.delete<any>('/api/deadembryo/' + docId);
  }

  public deleteByIdCheck(docId: string): Observable<DeleteByIdCheckDto> {
    return this.http.delete<any>('/api/deadembryo/' + docId + '/check');
  }
}
