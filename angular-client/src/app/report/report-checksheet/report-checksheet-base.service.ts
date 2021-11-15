import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReportChecksheet } from './report-checksheet';
import { Response } from '../../domain/response/response';

export abstract class ReportChecksheetBaseService {
  constructor(protected http: HttpClient) {}

  public search(criteria: any): Observable<Response<ReportChecksheet>> {
    return this.http.post<Response<ReportChecksheet>>(
      '/api/report/checksheet/report',
      criteria
    );
  }
}
