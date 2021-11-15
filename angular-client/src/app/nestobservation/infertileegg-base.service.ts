import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { InfertileEggEntity } from '../domain/infertileegg.entity';
import { PagedResponse } from '../domain/response/paged-response';
import { DeleteByIdCheckDto } from '../domain/response/delete-by-id-check-dto';
import { Response } from '../domain/response/response';

export abstract class InfertileEggBaseService {
  constructor(protected http: HttpClient) {}

  public search(criteria: any): Observable<PagedResponse> {
    return this.http.post<PagedResponse>('/api/infertileegg/search', criteria);
  }

  public findById(docId: string): Observable<InfertileEggEntity> {
    return this.http.get<InfertileEggEntity>('/api/infertileegg/' + docId).pipe(
      map((response: InfertileEggEntity) => {
        response.modifiedTime =
          response.modifiedTime === null
            ? null
            : new Date(response.modifiedTime);
        return response;
      })
    );
  }

  public save(
    entity: InfertileEggEntity
  ): Observable<Response<InfertileEggEntity>> {
    return this.http.post<Response<InfertileEggEntity>>(
      '/api/infertileegg/',
      entity
    );
  }

  public delete(docId: string): Observable<any> {
    return this.http.delete<any>('/api/infertileegg/' + docId);
  }

  public deleteByIdCheck(docId: string): Observable<DeleteByIdCheckDto> {
    return this.http.delete<any>('/api/infertileegg/' + docId + '/check');
  }
}
