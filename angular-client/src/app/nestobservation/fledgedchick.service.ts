import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { FledgedChickBaseService } from './fledgedchick-base.service';

@Injectable()
export class FledgedChickService extends FledgedChickBaseService {
  constructor(http: HttpClient) {
    super(http);
  }
}
