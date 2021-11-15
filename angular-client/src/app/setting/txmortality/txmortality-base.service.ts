import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { HttpClient } from '@angular/common/http';

import { TxMortalityEntity } from '../../domain/txmortality.entity';
import { PagedResponse } from '../../domain/response/paged-response';
import { DeleteByIdCheckDto } from '../../domain/response/delete-by-id-check-dto';
import { Response } from '../../domain/response/response';

export abstract class TxMortalityBaseService {
  constructor(protected http: HttpClient) {}

  public search(criteria: any): Observable<PagedResponse> {
    return this.http.post<PagedResponse>('/api/txmortality/search', criteria);
  }

  public findById(docId: string): Observable<TxMortalityEntity> {
    return this.http.get<TxMortalityEntity>('/api/txmortality/' + docId).pipe(
      map((response: TxMortalityEntity) => {
        response.modifiedTime =
          response.modifiedTime === null
            ? null
            : new Date(response.modifiedTime);
        return response;
      })
    );
  }

  public save(
    entity: TxMortalityEntity
  ): Observable<Response<TxMortalityEntity>> {
    return this.http.post<Response<TxMortalityEntity>>(
      '/api/txmortality/',
      entity
    );
  }

  public delete(docId: string): Observable<any> {
    return this.http.delete<any>('/api/txmortality/' + docId);
  }

  public deleteByIdCheck(docId: string): Observable<DeleteByIdCheckDto> {
    return this.http.delete<any>('/api/txmortality/' + docId + '/check');
  }
}
