import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { SnarkActivityBaseService } from './snarkactivity-base.service';
import { Observable } from 'rxjs';
import { PagedResponse } from '../domain/response/paged-response';
import { SnarkActivityCriteria } from './snarkactivity.criteria';

@Injectable()
export class SnarkActivityService extends SnarkActivityBaseService {
  public snarkactivityCriteria: SnarkActivityCriteria;

  constructor(http: HttpClient) {
    super(http);
  }

  public search(criteria: any): Observable<PagedResponse> {
    return this.http.post<PagedResponse>(
      '/api/snarkactivity/searchDTO',
      criteria
    );
  }
}
