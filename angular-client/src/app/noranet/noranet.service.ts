import { Observable } from 'rxjs/internal/Observable';
import { map } from 'rxjs/operators';

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { NoraNetBaseService } from './noranet-base.service';
import { NoraNetDto } from './noranet-dto';
import { PagedResponse } from '../domain/response/paged-response';
import { NoraNetCriteria } from './noranet.criteria';
import { NoraNetEntity } from './entities/noranet.entity';
import { UndetectedBirdDTO } from './undetected-bird.dto';
import { NoraNetSnarkCriteria } from './noranetsnark.criteria';
import { NoraNetStationCriteria } from './noranetstation.criteria';

@Injectable()
export class NoraNetService extends NoraNetBaseService {
  public noranetCriteria: NoraNetCriteria;
  public noranetSnarkCriteria: NoraNetSnarkCriteria;
  public noranetStationCriteria: NoraNetStationCriteria;

  constructor(http: HttpClient) {
    super(http);
  }

  public findNoraNetDTOByNoraNetId(noraNetId: string): Observable<NoraNetDto> {
    return this.http.post<NoraNetDto>('/api/noranet/noranetDto', noraNetId);
  }

  public findSearchDTOByCriteria(
    criteria: NoraNetCriteria
  ): Observable<PagedResponse> {
    return this.http.post<PagedResponse>('/api/noranet/searchDTO', criteria);
  }

  public findUndetectedBirdsDTOByCriteria(
    criteria: NoraNetCriteria
  ): Observable<UndetectedBirdDTO[]> {
    // NB: Conscious decision to not use a paginated response
    return this.http.post<UndetectedBirdDTO[]>(
      '/api/noranet/searchUndetectedDTO',
      criteria
    );
  }

  public findAllNoraNets(): Observable<NoraNetEntity[]> {
    const tc = new NoraNetCriteria();
    tc.pageSize = 2147483647;
    return this.search(tc).pipe(map((response) => response.results));
  }

  public findSnarkSearchDTOByCriteria(
    criteria: NoraNetSnarkCriteria
  ): Observable<PagedResponse> {
    return this.http.post<PagedResponse>(
      '/api/noranet/searchSnarkDTO',
      criteria
    );
  }

  public findStationSearchDTOByCriteria(
    criteria: NoraNetStationCriteria
  ): Observable<PagedResponse> {
    return this.http.post<PagedResponse>(
      '/api/noranet/searchStationDTO',
      criteria
    );
  }
}
