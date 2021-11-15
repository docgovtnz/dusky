import { Observable } from 'rxjs';

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Response } from '../../domain/response/response';
import { ReportMatingDTO } from './report-mating-dto';

@Injectable()
export class ReportMatingService {
  constructor(protected http: HttpClient) {}

  public search(criteria: any): Observable<Response<ReportMatingDTO[]>> {
    return this.http.post<Response<ReportMatingDTO[]>>(
      '/api/report/mating/report',
      criteria
    );
  }
}
