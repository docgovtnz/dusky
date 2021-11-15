import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { NewEggBaseService } from './newegg-base.service';
import { NewEggEntity } from '../domain/newegg.entity';
import { Observable } from 'rxjs/index';
import { Response } from '../domain/response/response';
import { tap } from 'rxjs/operators';
import { OptionsService } from '../common/options.service';

@Injectable()
export class NewEggService extends NewEggBaseService {
  constructor(http: HttpClient, private optionsService: OptionsService) {
    super(http);
  }

  public save(entity: NewEggEntity): Observable<Response<NewEggEntity>> {
    return super
      .save(entity)
      .pipe(tap(() => this.optionsService.resetBirdCache()));
  }
}
