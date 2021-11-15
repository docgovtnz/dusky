import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { PersonEntity } from '../domain/person.entity';
import { PagedResponse } from '../domain/response/paged-response';
import { DeleteByIdCheckDto } from '../domain/response/delete-by-id-check-dto';
import { Response } from '../domain/response/response';

export abstract class PersonBaseService {
  constructor(protected http: HttpClient) {}

  public search(criteria: any): Observable<PagedResponse> {
    return this.http.post<PagedResponse>('/api/person/search', criteria);
  }

  public findById(docId: string): Observable<PersonEntity> {
    return this.http.get<PersonEntity>('/api/person/' + docId).pipe(
      map((response: PersonEntity) => {
        response.accountExpiry =
          response.accountExpiry === null
            ? null
            : new Date(response.accountExpiry);
        response.modifiedTime =
          response.modifiedTime === null
            ? null
            : new Date(response.modifiedTime);
        return response;
      })
    );
  }

  public save(entity: PersonEntity): Observable<Response<PersonEntity>> {
    return this.http.post<Response<PersonEntity>>('/api/person/', entity);
  }

  public delete(docId: string): Observable<any> {
    return this.http.delete<any>('/api/person/' + docId);
  }

  public deleteByIdCheck(docId: string): Observable<DeleteByIdCheckDto> {
    return this.http.delete<any>('/api/person/' + docId + '/check');
  }
}
