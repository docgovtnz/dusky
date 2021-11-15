import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { DeadEmbryoBaseService } from './deadembryo-base.service';

@Injectable()
export class DeadEmbryoService extends DeadEmbryoBaseService {
  constructor(http: HttpClient) {
    super(http);
  }
}
