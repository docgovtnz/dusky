import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { WeanedChickBaseService } from './weanedchick-base.service';

@Injectable()
export class WeanedChickService extends WeanedChickBaseService {
  constructor(http: HttpClient) {
    super(http);
  }
}
