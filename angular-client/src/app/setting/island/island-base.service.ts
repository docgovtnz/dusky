import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { HttpClient } from '@angular/common/http';

import { IslandEntity } from '../../domain/island.entity';
import { PagedResponse } from '../../domain/response/paged-response';
import { DeleteByIdCheckDto } from '../../domain/response/delete-by-id-check-dto';
import { Response } from '../../domain/response/response';

export abstract class IslandBaseService {
  constructor(protected http: HttpClient) {}

  public search(criteria: any): Observable<PagedResponse> {
    return this.http.post<PagedResponse>('/api/island/search', criteria);
  }

  public findById(docId: string): Observable<IslandEntity> {
    return this.http.get<IslandEntity>('/api/island/' + docId).pipe(
      map((response: IslandEntity) => {
        response.modifiedTime =
          response.modifiedTime === null
            ? null
            : new Date(response.modifiedTime);
        return response;
      })
    );
  }

  public save(entity: IslandEntity): Observable<Response<IslandEntity>> {
    return this.http.post<Response<IslandEntity>>('/api/island/', entity);
  }

  public delete(docId: string): Observable<any> {
    return this.http.delete<any>('/api/island/' + docId);
  }

  public deleteByIdCheck(docId: string): Observable<DeleteByIdCheckDto> {
    return this.http.delete<any>('/api/island/' + docId + '/check');
  }
}
