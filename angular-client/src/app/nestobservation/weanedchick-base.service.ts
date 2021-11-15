import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { WeanedChickEntity } from '../domain/weanedchick.entity';
import { PagedResponse } from '../domain/response/paged-response';
import { DeleteByIdCheckDto } from '../domain/response/delete-by-id-check-dto';
import { Response } from '../domain/response/response';

export abstract class WeanedChickBaseService {
  constructor(protected http: HttpClient) {}

  public search(criteria: any): Observable<PagedResponse> {
    return this.http.post<PagedResponse>('/api/weanedchick/search', criteria);
  }

  public findById(docId: string): Observable<WeanedChickEntity> {
    return this.http.get<WeanedChickEntity>('/api/weanedchick/' + docId).pipe(
      map((response: WeanedChickEntity) => {
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
    entity: WeanedChickEntity
  ): Observable<Response<WeanedChickEntity>> {
    return this.http.post<Response<WeanedChickEntity>>(
      '/api/weanedchick/',
      entity
    );
  }

  public delete(docId: string): Observable<any> {
    return this.http.delete<any>('/api/weanedchick/' + docId);
  }

  public deleteByIdCheck(docId: string): Observable<DeleteByIdCheckDto> {
    return this.http.delete<any>('/api/weanedchick/' + docId + '/check');
  }
}
