import { Observable } from 'rxjs';

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Response } from '../../domain/response/response';
import { ReportNoraNetErrorDto } from './report-noranet-error-dto';

@Injectable()
export class ReportNoraNetErrorService {
  constructor(protected http: HttpClient) {}

  public search(criteria: any): Observable<Response<ReportNoraNetErrorDto[]>> {
    return this.http.post<Response<ReportNoraNetErrorDto[]>>(
      '/api/noraneterror/report/',
      criteria
    );
  }
}
