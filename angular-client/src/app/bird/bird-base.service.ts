import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BirdEntity } from '../domain/bird.entity';
import { PagedResponse } from '../domain/response/paged-response';
import { DeleteByIdCheckDto } from '../domain/response/delete-by-id-check-dto';
import { Response } from '../domain/response/response';

export abstract class BirdBaseService {
  constructor(protected http: HttpClient) {}

  public search(criteria: any): Observable<PagedResponse> {
    return this.http.post<PagedResponse>('/api/bird/search', criteria);
  }

  public findById(docId: string): Observable<BirdEntity> {
    return this.http.get<BirdEntity>('/api/bird/' + docId).pipe(
      map((response: BirdEntity) => {
        response.dateFirstFound =
          response.dateFirstFound === null
            ? null
            : new Date(response.dateFirstFound);
        response.dateFledged =
          response.dateFledged === null ? null : new Date(response.dateFledged);
        response.dateHatched =
          response.dateHatched === null ? null : new Date(response.dateHatched);
        response.dateIndependent =
          response.dateIndependent === null
            ? null
            : new Date(response.dateIndependent);
        response.dateLaid =
          response.dateLaid === null ? null : new Date(response.dateLaid);
        response.dateWeaned =
          response.dateWeaned === null ? null : new Date(response.dateWeaned);
        response.demise =
          response.demise === null ? null : new Date(response.demise);
        response.discoveryDate =
          response.discoveryDate === null
            ? null
            : new Date(response.discoveryDate);
        response.modifiedTime =
          response.modifiedTime === null
            ? null
            : new Date(response.modifiedTime);
        return response;
      })
    );
  }

  public save(entity: BirdEntity): Observable<Response<BirdEntity>> {
    return this.http.post<Response<BirdEntity>>('/api/bird/', entity);
  }

  public delete(docId: string): Observable<any> {
    return this.http.delete<any>('/api/bird/' + docId);
  }

  public deleteByIdCheck(docId: string): Observable<DeleteByIdCheckDto> {
    return this.http.delete<any>('/api/bird/' + docId + '/check');
  }
}
