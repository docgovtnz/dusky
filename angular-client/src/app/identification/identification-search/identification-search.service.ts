import { Observable } from 'rxjs/index';

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { PagedResponse } from '../../domain/response/paged-response';
import { IdSearchCriteria } from './identification-search-criteria';

@Injectable({
  providedIn: 'root',
})
export class IdSearchService {
  public idSearchCriteria: IdSearchCriteria;

  constructor(private http: HttpClient) {}

  public search(criteria: any): Observable<PagedResponse> {
    return this.http.post<PagedResponse>('/api/id/search', criteria);
  }
}
