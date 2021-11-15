import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { ReportLocationBaseService } from './report-location-base.service';

@Injectable()
export class ReportLocationService extends ReportLocationBaseService {
  constructor(http: HttpClient) {
    super(http);
  }
}
