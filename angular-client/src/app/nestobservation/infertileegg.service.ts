import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { InfertileEggBaseService } from './infertileegg-base.service';

@Injectable()
export class InfertileEggService extends InfertileEggBaseService {
  constructor(http: HttpClient) {
    super(http);
  }
}
