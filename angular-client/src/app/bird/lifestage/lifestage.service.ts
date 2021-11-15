import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { LifeStageBaseService } from './lifestage-base.service';
import { Observable } from 'rxjs';
import { LifeStageEntity } from '../../domain/lifestage.entity';
import { map } from 'rxjs/operators';

@Injectable()
export class LifeStageService extends LifeStageBaseService {
  constructor(http: HttpClient) {
    super(http);
  }

  findByBirdID(birdID: string): Observable<LifeStageEntity> {
    return this.http
      .get<LifeStageEntity>('/api/lifestage/birdID/' + birdID)
      .pipe(
        map((entity: LifeStageEntity) => {
          entity.dateTime =
            entity.dateTime === null ? null : new Date(entity.dateTime);
          return entity;
        })
      );
  }
}
