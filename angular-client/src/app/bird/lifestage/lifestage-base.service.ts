import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { LifeStageEntity } from '../../domain/lifestage.entity';
import { PagedResponse } from '../../domain/response/paged-response';
import { DeleteByIdCheckDto } from '../../domain/response/delete-by-id-check-dto';
import { Response } from '../../domain/response/response';

export abstract class LifeStageBaseService {
  constructor(protected http: HttpClient) {}

  public search(criteria: any): Observable<PagedResponse> {
    return this.http.post<PagedResponse>('/api/lifestage/search', criteria);
  }

  public findById(docId: string): Observable<LifeStageEntity> {
    return this.http.get<LifeStageEntity>('/api/lifestage/' + docId).pipe(
      map((response: LifeStageEntity) => {
        response.dateTime =
          response.dateTime === null ? null : new Date(response.dateTime);
        response.modifiedTime =
          response.modifiedTime === null
            ? null
            : new Date(response.modifiedTime);
        return response;
      })
    );
  }

  public save(entity: LifeStageEntity): Observable<Response<LifeStageEntity>> {
    return this.http.post<Response<LifeStageEntity>>('/api/lifestage/', entity);
  }

  public delete(docId: string): Observable<any> {
    return this.http.delete<any>('/api/lifestage/' + docId);
  }

  public deleteByIdCheck(docId: string): Observable<DeleteByIdCheckDto> {
    return this.http.delete<any>('/api/lifestage/' + docId + '/check');
  }
}
