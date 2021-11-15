import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { NoraNetEntity } from './entities/noranet.entity';
import { PagedResponse } from '../domain/response/paged-response';
import { DeleteByIdCheckDto } from '../domain/response/delete-by-id-check-dto';
import { Response } from '../domain/response/response';

export abstract class NoraNetBaseService {
  constructor(protected http: HttpClient) {}

  public search(criteria: any): Observable<PagedResponse> {
    return this.http.post<PagedResponse>('/api/noranet/search', criteria);
  }

  public findById(docId: string): Observable<NoraNetEntity> {
    return this.http.get<NoraNetEntity>('/api/noranet/' + docId).pipe(
      map((response: NoraNetEntity) => {
        response.modifiedTime =
          response.modifiedTime === null
            ? null
            : new Date(response.modifiedTime);
        return response;
      })
    );
  }

  public save(entity: NoraNetEntity): Observable<Response<NoraNetEntity>> {
    return this.http.post<Response<NoraNetEntity>>('/api/noranet/', entity);
  }

  public delete(docId: string): Observable<any> {
    return this.http.delete<any>('/api/noranet/' + docId);
  }

  public deleteByIdCheck(docId: string): Observable<DeleteByIdCheckDto> {
    return this.http.delete<any>('/api/noranet/' + docId + '/check');
  }
}
