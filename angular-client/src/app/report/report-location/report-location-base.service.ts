import { Observable } from 'rxjs';

import { HttpClient } from '@angular/common/http';

import { ReportLocationCriteria } from './report-location.criteria';

export abstract class ReportLocationBaseService {
  constructor(protected http: HttpClient) {}

  public search(criteria: ReportLocationCriteria): Observable<any> {
    return this.http.post<any>('/api/bird/locations', criteria);
  }
}
