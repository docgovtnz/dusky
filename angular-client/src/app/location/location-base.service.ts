import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { LocationEntity } from '../domain/location.entity';
import { PagedResponse } from '../domain/response/paged-response';
import { DeleteByIdCheckDto } from '../domain/response/delete-by-id-check-dto';
import { Response } from '../domain/response/response';

export abstract class LocationBaseService {
  constructor(protected http: HttpClient) {}

  public search(criteria: any): Observable<PagedResponse> {
    return this.http.post<PagedResponse>('/api/location/search', criteria);
  }

  public findById(docId: string): Observable<LocationEntity> {
    return this.http.get<LocationEntity>('/api/location/' + docId).pipe(
      map((response: LocationEntity) => {
        response.firstDate =
          response.firstDate === null ? null : new Date(response.firstDate);
        response.modifiedTime =
          response.modifiedTime === null
            ? null
            : new Date(response.modifiedTime);
        return response;
      })
    );
  }

  public save(entity: LocationEntity): Observable<Response<LocationEntity>> {
    return this.http.post<Response<LocationEntity>>('/api/location/', entity);
  }

  public delete(docId: string): Observable<any> {
    return this.http.delete<any>('/api/location/' + docId);
  }

  public deleteByIdCheck(docId: string): Observable<DeleteByIdCheckDto> {
    return this.http.delete<any>('/api/location/' + docId + '/check');
  }
}
