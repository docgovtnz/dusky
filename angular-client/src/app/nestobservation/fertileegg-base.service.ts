import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { FertileEggEntity } from '../domain/fertileegg.entity';
import { PagedResponse } from '../domain/response/paged-response';
import { DeleteByIdCheckDto } from '../domain/response/delete-by-id-check-dto';
import { Response } from '../domain/response/response';

export abstract class FertileEggBaseService {
  constructor(protected http: HttpClient) {}

  public search(criteria: any): Observable<PagedResponse> {
    return this.http.post<PagedResponse>('/api/fertileegg/search', criteria);
  }

  public findById(docId: string): Observable<FertileEggEntity> {
    return this.http.get<FertileEggEntity>('/api/fertileegg/' + docId).pipe(
      map((response: FertileEggEntity) => {
        response.modifiedTime =
          response.modifiedTime === null
            ? null
            : new Date(response.modifiedTime);
        return response;
      })
    );
  }

  public save(
    entity: FertileEggEntity
  ): Observable<Response<FertileEggEntity>> {
    return this.http.post<Response<FertileEggEntity>>(
      '/api/fertileegg/',
      entity
    );
  }

  public delete(docId: string): Observable<any> {
    return this.http.delete<any>('/api/fertileegg/' + docId);
  }

  public deleteByIdCheck(docId: string): Observable<DeleteByIdCheckDto> {
    return this.http.delete<any>('/api/fertileegg/' + docId + '/check');
  }
}
