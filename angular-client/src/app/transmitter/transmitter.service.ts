import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { TransmitterBaseService } from './transmitter-base.service';
import { Observable } from 'rxjs/internal/Observable';
import { TransmitterDto } from './transmitter-dto';
import { PagedResponse } from '../domain/response/paged-response';
import { TransmitterCriteria } from './transmitter.criteria';
import { map } from 'rxjs/operators';
import { TransmitterEntity } from '../domain/transmitter.entity';
import { TransmitterBirdHistoryDto } from './transmitter-bird-history-dto';

@Injectable()
export class TransmitterService extends TransmitterBaseService {
  public transmitterCriteria: TransmitterCriteria;

  constructor(http: HttpClient) {
    super(http);
  }

  public findTransmitterDTOByTxId(txid: string): Observable<TransmitterDto> {
    return this.http.post<TransmitterDto>(
      '/api/transmitter/transmitterDto',
      txid
    );
  }

  public findSearchDTOByCriteria(
    criteria: TransmitterCriteria
  ): Observable<PagedResponse> {
    return this.http.post<PagedResponse>('api/transmitter/searchDTO', criteria);
  }

  public getTransmitterDeployedDateTime(id: string): Observable<Date> {
    return this.http
      .get<string>(`api/transmitter/${id}/deployedDateTime`)
      .pipe(map((result) => (result === null ? null : new Date(result))));
  }

  public calculateTransmitterExpiry(
    id: string,
    lifeExpentancyWeeks?: number,
    deployedDateTime?: Date
  ): Observable<Date> {
    const params = {};
    if (lifeExpentancyWeeks) {
      params['lifeExpentancy'] = String(lifeExpentancyWeeks);
    }
    if (deployedDateTime) {
      params['deployedDateTime'] = deployedDateTime.toISOString();
    }
    return this.http
      .get<string>(`api/transmitter/${id}/expiryDate`, { params })
      .pipe(map((result) => (result === null ? null : new Date(result))));
  }

  public findSpareTransmitters(): Observable<TransmitterEntity[]> {
    const tc = new TransmitterCriteria();
    tc.spareOnly = true;
    tc.pageSize = 2147483647;
    return this.search(tc).pipe(map((response) => response.results));
  }

  public findAllTransmitters(): Observable<TransmitterEntity[]> {
    const tc = new TransmitterCriteria();
    tc.pageSize = 2147483647;
    return this.search(tc).pipe(map((response) => response.results));
  }

  public getBirdHistory(
    txDocId: string
  ): Observable<TransmitterBirdHistoryDto[]> {
    return this.http.get<TransmitterBirdHistoryDto[]>(
      `api/transmitter/${txDocId}/birdHistory`
    );
  }
}
