import { Observable } from 'rxjs/index';

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { PagedResponse } from '../../domain/response/paged-response';

@Injectable()
export class ReportLatestWeightService {
  constructor(protected http: HttpClient) {}

  public search(criteria: any): Observable<PagedResponse> {
    return this.http.post<PagedResponse>(
      '/api/report/latestweight/report',
      criteria
    );
  }
}
