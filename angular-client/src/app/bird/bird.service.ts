import { BirdTransmitterHistoryDto } from './bird-transmitter-history-dto';
import { TransmitterEntity } from './../domain/transmitter.entity';
import { CurrentChipDTO } from './current-chip-dto';
import { CurrentBandDTO } from './current-band-dto';
import { Lifestage } from './lifestage';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/index';
import { map, tap } from 'rxjs/operators';

import { BirdBaseService } from './bird-base.service';
import { BirdEntity } from '../domain/bird.entity';
import { DatedMeasureDetailDto } from '../common/dated-measure-detail-dto';
import { PagedResponse } from '../domain/response/paged-response';
import { OptionsService } from '../common/options.service';
import { Response } from '../domain/response/response';
import { BirdSummaryDto } from '../common/bird-summary-dto';
import { BirdSummaryCriteria } from '../common/bird-summary-criteria';
import { BirdCriteria } from './bird.criteria';

@Injectable()
export class BirdService extends BirdBaseService {
  public birdCriteria: BirdCriteria;

  constructor(http: HttpClient, private optionsService: OptionsService) {
    super(http);
  }

  public findCurrentMeasureDetailByBirdID(
    birdID: string
  ): Observable<DatedMeasureDetailDto> {
    return this.http
      .get<DatedMeasureDetailDto>(`api/bird/${birdID}/currentMeasureDetail`)
      .pipe(
        map((response: DatedMeasureDetailDto) => {
          response.dateTime =
            response.dateTime === null ? null : new Date(response.dateTime);
          return response;
        })
      );
  }

  public search(criteria: any): Observable<PagedResponse> {
    return this.http.post<PagedResponse>('/api/bird/searchDTO', criteria);
  }

  public findBirdByTransmitterID(
    island: string,
    channel: number,
    sex: string
  ): Observable<BirdEntity> {
    return this.http.get<BirdEntity>(
      `api/bird/birdByTransmitter?island=${island}&channel=${channel}&sex=${sex}`
    );
  }

  public getCurrentBand(birdID: string): Observable<CurrentBandDTO> {
    return this.http.get<CurrentBandDTO>(`api/bird/${birdID}/currentBand`).pipe(
      map((response) => {
        if (response) {
          response.dateTime =
            response.dateTime === null ? null : new Date(response.dateTime);
        }
        return response;
      })
    );
  }

  public getCurrentChip(birdID: string): Observable<CurrentChipDTO> {
    return this.http.get<CurrentChipDTO>(`api/bird/${birdID}/currentChip`).pipe(
      map((response) => {
        if (response) {
          response.dateTime =
            response.dateTime === null ? null : new Date(response.dateTime);
        }
        return response;
      })
    );
  }

  public getCurrentTransmitter(birdID: string): Observable<TransmitterEntity> {
    return this.http.get<TransmitterEntity>(
      `api/bird/${birdID}/currentTransmitter`
    );
  }

  public getCurrentTransmitterDeployedDate(birdID: string): Observable<Date> {
    return this.http
      .get<Date>(`api/bird/${birdID}/currentTransmitter/deployedDate`)
      .pipe(map((d) => (d ? new Date(d) : null)));
  }

  public getCurrentTransmitterExpiryDate(birdID: string): Observable<Date> {
    return this.http
      .get<Date>(`api/bird/${birdID}/currentTransmitter/expiryDate`)
      .pipe(map((d) => (d ? new Date(d) : null)));
  }

  public getTransmitterHistory(
    birdID: string
  ): Observable<BirdTransmitterHistoryDto[]> {
    return this.http.get<BirdTransmitterHistoryDto[]>(
      `api/bird/${birdID}/transmitterHistory`
    );
  }

  public getAgeClass(birdID: string): Observable<string> {
    return this.http.get(`api/bird/${birdID}/ageClass`, {
      responseType: 'text',
    });
  }

  public getMortality(birdID: any): any {
    return this.http.get(`api/bird/${birdID}/mortality`, {
      responseType: 'text',
    });
  }

  public getMilestone(birdID: any): any {
    return this.http.get(`api/bird/${birdID}/milestone`, {
      responseType: 'text',
    });
  }

  public getAgeInDays(birdID: string): Observable<number> {
    return this.http
      .get(`api/bird/${birdID}/ageInDays`, { responseType: 'text' })
      .pipe(
        map((daysString) => (daysString ? parseInt(daysString, 10) : null))
      );
  }

  public getName(birdID: string): Observable<string> {
    return this.http.get(`api/bird/${birdID}/name`, { responseType: 'text' });
  }

  public save(entity: BirdEntity): Observable<Response<BirdEntity>> {
    return super
      .save(entity)
      .pipe(tap(() => this.optionsService.resetBirdCache()));
  }

  public delete(docId: string): Observable<any> {
    return super
      .delete(docId)
      .pipe(tap(() => this.optionsService.resetBirdCache()));
  }

  public getLifeStage(birdID: string): Observable<Lifestage> {
    return this.http.get<Lifestage>(`api/bird/${birdID}/lifestage`);
  }

  public getLifeStages(birdIDs: string[]): Observable<Lifestage[]> {
    return this.http.post<Lifestage[]>(`api/bird/lifestages`, birdIDs);
  }

  public findSummaryDTOByCriteria(
    criteria: BirdSummaryCriteria
  ): Observable<BirdSummaryDto[]> {
    return this.http.post<BirdSummaryDto[]>('api/bird/summaryDTO', criteria);
  }
}
