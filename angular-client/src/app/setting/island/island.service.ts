import { Observable } from 'rxjs';

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { IslandBaseService } from './island-base.service';
import { IslandEntity } from '../../domain/island.entity';
import { IslandCriteria } from './island.criteria';

@Injectable()
export class IslandService extends IslandBaseService {
  public islandCriteria: IslandCriteria;

  constructor(http: HttpClient) {
    super(http);
  }

  findAll(): Observable<IslandEntity[]> {
    return this.http.get<IslandEntity[]>('/api/island/');
  }

  findByName(islandName: string): Observable<IslandEntity> {
    return this.http.get<IslandEntity>('/api/island/name/' + islandName);
  }
}
