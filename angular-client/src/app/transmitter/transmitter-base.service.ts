import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { TransmitterEntity } from '../domain/transmitter.entity';
import { PagedResponse } from '../domain/response/paged-response';
import { DeleteByIdCheckDto } from '../domain/response/delete-by-id-check-dto';
import { Response } from '../domain/response/response';

export abstract class TransmitterBaseService {
  constructor(protected http: HttpClient) {}

  public search(criteria: any): Observable<PagedResponse> {
    return this.http.post<PagedResponse>('/api/transmitter/search', criteria);
  }

  public findById(docId: string): Observable<TransmitterEntity> {
    return this.http.get<TransmitterEntity>('/api/transmitter/' + docId).pipe(
      map((response: TransmitterEntity) => {
        response.modifiedTime =
          response.modifiedTime === null
            ? null
            : new Date(response.modifiedTime);
        return response;
      })
    );
  }

  public save(
    entity: TransmitterEntity
  ): Observable<Response<TransmitterEntity>> {
    return this.http.post<Response<TransmitterEntity>>(
      '/api/transmitter/',
      entity
    );
  }

  public delete(docId: string): Observable<any> {
    return this.http.delete<any>('/api/transmitter/' + docId);
  }

  public deleteByIdCheck(docId: string): Observable<DeleteByIdCheckDto> {
    return this.http.delete<any>('/api/transmitter/' + docId + '/check');
  }
}
