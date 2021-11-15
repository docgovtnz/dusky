import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { SettingEntity } from '../domain/setting.entity';
import { PagedResponse } from '../domain/response/paged-response';
import { DeleteByIdCheckDto } from '../domain/response/delete-by-id-check-dto';
import { Response } from '../domain/response/response';

export abstract class SettingBaseService {
  constructor(protected http: HttpClient) {}

  public search(criteria: any): Observable<PagedResponse> {
    return this.http.post<PagedResponse>('/api/setting/search', criteria);
  }

  public findById(docId: string): Observable<SettingEntity> {
    return this.http.get<SettingEntity>('/api/setting/' + docId).pipe(
      map((response: SettingEntity) => {
        response.modifiedTime =
          response.modifiedTime === null
            ? null
            : new Date(response.modifiedTime);
        return response;
      })
    );
  }

  public save(entity: SettingEntity): Observable<Response<SettingEntity>> {
    return this.http.post<Response<SettingEntity>>('/api/setting/', entity);
  }

  public delete(docId: string): Observable<any> {
    return this.http.delete<any>('/api/setting/' + docId);
  }

  public deleteByIdCheck(docId: string): Observable<DeleteByIdCheckDto> {
    return this.http.delete<any>('/api/setting/' + docId + '/check');
  }
}
