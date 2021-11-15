import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { interval, Observable, of, throwError } from 'rxjs';
import { FileMetaDataList } from './file-meta-data-list';
import { flatMap, retryWhen } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class UpgradeService {
  constructor(private http: HttpClient) {}

  downloadFileList(): Observable<FileMetaDataList> {
    return this.http.get<FileMetaDataList>('api/upgrade/local/download/list');
  }

  downloadFiles(): Observable<any> {
    // Should be a post instead of a get for security reasons, even though no body is sent
    return this.http.post('api/upgrade/local/download/files', {});
  }

  resetFiles(): Observable<any> {
    return this.http.delete('api/upgrade/local/reset/files');
  }

  upgradeRelease(): Observable<any> {
    return this.http.post('api/upgrade/local/upgrade', {});
  }

  restartCheck(): Observable<any> {
    /* eslint-disable @typescript-eslint/naming-convention */
    const headers = new HttpHeaders({
      x_service_level: 'optional',
    });
    /* eslint-enable */

    return this.http
      .get('api/upgrade/local/restart/check', { headers })
      .pipe(
        retryWhen((_) =>
          interval(3000).pipe(
            flatMap((count) =>
              count === 20 ? throwError('Giving up') : of(count)
            )
          )
        )
      );
  }
}
