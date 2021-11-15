import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { ReportChecksheetBaseService } from './report-checksheet-base.service';

@Injectable()
export class ReportChecksheetService extends ReportChecksheetBaseService {
  constructor(http: HttpClient) {
    super(http);
  }
}
