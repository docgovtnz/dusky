import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/index';
import { IndexRebuildStatus } from './index-rebuild-status';

@Injectable()
export class IndexRebuildService {
  constructor(protected http: HttpClient) {}

  public resetIndexes(): Observable<IndexRebuildStatus> {
    return this.http.post<IndexRebuildStatus>(
      '/api/application/reset-indexes',
      null
    );
  }

  public getResetStatus(): Observable<IndexRebuildStatus> {
    return this.http.get<IndexRebuildStatus>(
      '/api/application/reset-indexes-status'
    );
  }
}
