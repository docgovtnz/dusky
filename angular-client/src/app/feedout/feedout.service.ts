import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { FeedOutBaseService } from './feedout-base.service';

@Injectable()
export class FeedOutService extends FeedOutBaseService {
  constructor(http: HttpClient) {
    super(http);
  }
}
