import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { SnarkImportEntity } from '../domain/snarkimport.entity';
import { PagedResponse } from '../domain/response/paged-response';
import { DeleteByIdCheckDto } from '../domain/response/delete-by-id-check-dto';
import { Response } from '../domain/response/response';

export abstract class SnarkImportBaseService {
  constructor(protected http: HttpClient) {}

  public search(criteria: any): Observable<PagedResponse> {
    return this.http.post<PagedResponse>('/api/snarkimport/search', criteria);
  }

  public findById(docId: string): Observable<SnarkImportEntity> {
    return this.http.get<SnarkImportEntity>('/api/snarkimport/' + docId).pipe(
      map((response: SnarkImportEntity) => {
        response.modifiedTime =
          response.modifiedTime === null
            ? null
            : new Date(response.modifiedTime);
        return response;
      })
    );
  }

  public save(
    entity: SnarkImportEntity
  ): Observable<Response<SnarkImportEntity>> {
    return this.http.post<Response<SnarkImportEntity>>(
      '/api/snarkimport/',
      entity
    );
  }

  public delete(docId: string): Observable<any> {
    return this.http.delete<any>('/api/snarkimport/' + docId);
  }

  public deleteByIdCheck(docId: string): Observable<DeleteByIdCheckDto> {
    return this.http.delete<any>('/api/snarkimport/' + docId + '/check');
  }
}
