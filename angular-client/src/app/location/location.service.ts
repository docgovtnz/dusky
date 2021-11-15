import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { LocationBaseService } from './location-base.service';
import { LocationCriteria } from './location.criteria';
import { Observable } from 'rxjs';
import { PagedResponse } from '../domain/response/paged-response';
import { map, tap } from 'rxjs/operators';
import { LocationEntity } from '../domain/location.entity';
import { Response } from '../domain/response/response';
import { OptionsService } from '../common/options.service';

@Injectable()
export class LocationService extends LocationBaseService {
  public locationCriteria: LocationCriteria;

  constructor(http: HttpClient, private optionsService: OptionsService) {
    super(http);
  }

  public setDefaults(criteria: LocationCriteria): LocationCriteria {
    criteria.activeOnly = true;
    return criteria;
  }

  public getCurrentEggs(locationID: string): Observable<string[]> {
    return this.http
      .get<PagedResponse>(`/api/location/${locationID}/currentEggs`)
      .pipe(map((pr) => pr.results));
  }

  public getCurrentChicks(locationID: string): Observable<string[]> {
    return this.http
      .get<PagedResponse>(`/api/location/${locationID}/currentChicks`)
      .pipe(map((pr) => pr.results));
  }

  public getNextClutchOrder(locationID: string): Observable<number> {
    return this.http.get<number>(`/api/location/${locationID}/nextClutchOrder`);
  }

  public getClutch(locationID: any): Observable<string> {
    return this.http.get(`/api/location/${locationID}/clutch`, {
      responseType: 'text',
    });
  }

  public save(entity: LocationEntity): Observable<Response<LocationEntity>> {
    return super
      .save(entity)
      .pipe(tap(() => this.optionsService.resetLocationCache()));
  }

  public delete(docId: string): Observable<any> {
    return super
      .delete(docId)
      .pipe(tap(() => this.optionsService.resetLocationCache()));
  }
}
