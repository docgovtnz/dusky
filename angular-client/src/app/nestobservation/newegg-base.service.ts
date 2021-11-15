import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { NewEggEntity } from '../domain/newegg.entity';
import { PagedResponse } from '../domain/response/paged-response';
import { DeleteByIdCheckDto } from '../domain/response/delete-by-id-check-dto';
import { Response } from '../domain/response/response';

export abstract class NewEggBaseService {
  constructor(protected http: HttpClient) {}

  public search(criteria: any): Observable<PagedResponse> {
    return this.http.post<PagedResponse>('/api/newegg/search', criteria);
  }

  public findById(docId: string): Observable<NewEggEntity> {
    return this.http.get<NewEggEntity>('/api/newegg/' + docId).pipe(
      map((response: NewEggEntity) => {
        response.layDate =
          response.layDate === null ? null : new Date(response.layDate);
        response.modifiedTime =
          response.modifiedTime === null
            ? null
            : new Date(response.modifiedTime);
        return response;
      })
    );
  }

  public save(entity: NewEggEntity): Observable<Response<NewEggEntity>> {
    return this.http.post<Response<NewEggEntity>>('/api/newegg/', entity);
  }

  public delete(docId: string): Observable<any> {
    return this.http.delete<any>('/api/newegg/' + docId);
  }

  public deleteByIdCheck(docId: string): Observable<DeleteByIdCheckDto> {
    return this.http.delete<any>('/api/newegg/' + docId + '/check');
  }
}
