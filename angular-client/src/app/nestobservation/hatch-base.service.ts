import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { HatchEntity } from '../domain/hatch.entity';
import { PagedResponse } from '../domain/response/paged-response';
import { DeleteByIdCheckDto } from '../domain/response/delete-by-id-check-dto';
import { Response } from '../domain/response/response';

export abstract class HatchBaseService {
  constructor(protected http: HttpClient) {}

  public search(criteria: any): Observable<PagedResponse> {
    return this.http.post<PagedResponse>('/api/hatch/search', criteria);
  }

  public findById(docId: string): Observable<HatchEntity> {
    return this.http.get<HatchEntity>('/api/hatch/' + docId).pipe(
      map((response: HatchEntity) => {
        response.hatchDate =
          response.hatchDate === null ? null : new Date(response.hatchDate);
        response.modifiedTime =
          response.modifiedTime === null
            ? null
            : new Date(response.modifiedTime);
        return response;
      })
    );
  }

  public save(entity: HatchEntity): Observable<Response<HatchEntity>> {
    return this.http.post<Response<HatchEntity>>('/api/hatch/', entity);
  }

  public delete(docId: string): Observable<any> {
    return this.http.delete<any>('/api/hatch/' + docId);
  }

  public deleteByIdCheck(docId: string): Observable<DeleteByIdCheckDto> {
    return this.http.delete<any>('/api/hatch/' + docId + '/check');
  }
}
