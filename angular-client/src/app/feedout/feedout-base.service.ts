import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { FeedOutEntity } from '../domain/feedout.entity';
import { PagedResponse } from '../domain/response/paged-response';
import { DeleteByIdCheckDto } from '../domain/response/delete-by-id-check-dto';
import { Response } from '../domain/response/response';

export abstract class FeedOutBaseService {
  constructor(protected http: HttpClient) {}

  public search(criteria: any): Observable<PagedResponse> {
    return this.http.post<PagedResponse>('/api/feedout/search', criteria);
  }

  public findById(docId: string): Observable<FeedOutEntity> {
    return this.http.get<FeedOutEntity>('/api/feedout/' + docId).pipe(
      map((response: FeedOutEntity) => {
        response.dateIn =
          response.dateIn === null ? null : new Date(response.dateIn);
        response.dateOut =
          response.dateOut === null ? null : new Date(response.dateOut);
        response.modifiedTime =
          response.modifiedTime === null
            ? null
            : new Date(response.modifiedTime);
        return response;
      })
    );
  }

  public save(entity: FeedOutEntity): Observable<Response<FeedOutEntity>> {
    return this.http.post<Response<FeedOutEntity>>('/api/feedout/', entity);
  }

  public delete(docId: string): Observable<any> {
    return this.http.delete<any>('/api/feedout/' + docId);
  }

  public deleteByIdCheck(docId: string): Observable<DeleteByIdCheckDto> {
    return this.http.delete<any>('/api/feedout/' + docId + '/check');
  }
}
