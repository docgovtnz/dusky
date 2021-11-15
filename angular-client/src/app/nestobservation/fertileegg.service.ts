import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { FertileEggBaseService } from './fertileegg-base.service';

@Injectable()
export class FertileEggService extends FertileEggBaseService {
  constructor(http: HttpClient) {
    super(http);
  }
}
