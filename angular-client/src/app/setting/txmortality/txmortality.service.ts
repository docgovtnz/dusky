import { Observable } from 'rxjs/internal/Observable';

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { TxMortalityBaseService } from './txmortality-base.service';
import { TxMortalityEntity } from '../../domain/txmortality.entity';
import { TxMortalityCriteria } from './txmortality.criteria';

@Injectable()
export class TxMortalityService extends TxMortalityBaseService {
  public txmortalityCriteria: TxMortalityCriteria;

  constructor(http: HttpClient) {
    super(http);
  }

  public getAllTxMortalityOptions(): Observable<TxMortalityEntity[]> {
    return this.http.get<TxMortalityEntity[]>('/api/txmortality/all');
  }
}
