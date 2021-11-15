import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { FledgedChickEntity } from '../domain/fledgedchick.entity';
import { PagedResponse } from '../domain/response/paged-response';
import { DeleteByIdCheckDto } from '../domain/response/delete-by-id-check-dto';
import { Response } from '../domain/response/response';

export abstract class FledgedChickBaseService {
  constructor(protected http: HttpClient) {}

  public search(criteria: any): Observable<PagedResponse> {
    return this.http.post<PagedResponse>('/api/fledgedchick/search', criteria);
  }

  public findById(docId: string): Observable<FledgedChickEntity> {
    return this.http.get<FledgedChickEntity>('/api/fledgedchick/' + docId).pipe(
      map((response: FledgedChickEntity) => {
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
    entity: FledgedChickEntity
  ): Observable<Response<FledgedChickEntity>> {
    return this.http.post<Response<FledgedChickEntity>>(
      '/api/fledgedchick/',
      entity
    );
  }

  public delete(docId: string): Observable<any> {
    return this.http.delete<any>('/api/fledgedchick/' + docId);
  }

  public deleteByIdCheck(docId: string): Observable<DeleteByIdCheckDto> {
    return this.http.delete<any>('/api/fledgedchick/' + docId + '/check');
  }
}
