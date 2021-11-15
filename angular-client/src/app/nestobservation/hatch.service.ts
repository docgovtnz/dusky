import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { HatchBaseService } from './hatch-base.service';

@Injectable()
export class HatchService extends HatchBaseService {
  constructor(http: HttpClient) {
    super(http);
  }
}
