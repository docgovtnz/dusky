import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { RecordBaseService } from './record-base.service';
import { Observable } from 'rxjs/index';
import { BandsAndChipsDto } from '../common/bands-and-chips-dto';
import { CurrentTransmitterInfoDto } from '../common/current-transmitter-info-dto';
import { RecordCriteria } from './record.criteria';
import { PagedResponse } from '../domain/response/paged-response';
import * as moment from 'moment';
import { RecordSearchDTO } from './record-search-dto';
import { map } from 'rxjs/operators';

@Injectable()
export class RecordService extends RecordBaseService {
  public recordCriteria: RecordCriteria;

  constructor(http: HttpClient) {
    super(http);
  }

  public setDefaults(criteria: RecordCriteria): RecordCriteria {
    criteria.fromDate = moment().startOf('day').subtract(1, 'years').toDate();
    return criteria;
  }

  public findSearchDTOByCriteria(
    criteria: RecordCriteria
  ): Observable<PagedResponse> {
    return this.http.post<PagedResponse>('api/record/searchDTO/', criteria);
  }

  public findIdInfoByBirdId(birdId: string): Observable<BandsAndChipsDto> {
    return this.http.post<BandsAndChipsDto>(
      'api/record/identification/',
      birdId
    );
  }

  public findBandHistoryByBirdId(
    birdId: string
  ): Observable<BandsAndChipsDto[]> {
    return this.http.post<BandsAndChipsDto[]>(
      'api/record/bandhistory/',
      birdId
    );
  }

  public findChipHistoryByBirdId(
    birdId: string
  ): Observable<BandsAndChipsDto[]> {
    return this.http.post<BandsAndChipsDto[]>(
      'api/record/chiphistory/',
      birdId
    );
  }

  public findCurrentTransmitterInfoByBirdId(
    birdId: string
  ): Observable<CurrentTransmitterInfoDto> {
    return this.http.post<CurrentTransmitterInfoDto>(
      'api/record/currentTransmitter/',
      birdId
    );
  }

  public findByCriteriaAndMarkLatestLocation(
    criteria: RecordCriteria
  ): Observable<PagedResponse> {
    return this.findSearchDTOByCriteria(criteria).pipe(
      map((response) => {
        response.results = this.markLatestLocation(response.results);
        return response;
      })
    );
  }

  /**
   * Sweep through these search results and find the "latest" location
   *
   * @param recordSearchResults
   */
  markLatestLocation(
    recordSearchResults: RecordSearchDTO[]
  ): RecordSearchDTO[] {
    const latestRecordMap = new Map<string, RecordSearchDTO>();

    // first pass, find the latest record
    recordSearchResults.forEach((searchResult) => {
      if (this.hasMappableLocation(searchResult)) {
        let latestRecord = latestRecordMap.get(searchResult.birdID);
        if (!latestRecord) {
          latestRecordMap.set(searchResult.birdID, searchResult);
          latestRecord = searchResult;
        }

        if (searchResult.dateTime > latestRecord.dateTime) {
          latestRecordMap.set(searchResult.birdID, searchResult);
        }
      }
    });

    // second pass, set the mapFeatureType
    recordSearchResults.forEach((searchResult) => {
      if (this.hasMappableLocation(searchResult)) {
        const latestRecord = latestRecordMap.get(searchResult.birdID);
        searchResult.mapFeatureType =
          searchResult.recordID === latestRecord.recordID
            ? searchResult.birdName + '-Latest'
            : searchResult.birdName;

        //console.log('MarkLatest location: ' + searchResult.mapFeatureType);
      }
    });

    return recordSearchResults;
  }

  hasMappableLocation(recordSearchDTO: RecordSearchDTO): boolean {
    return (
      (recordSearchDTO.easting !== null && recordSearchDTO.northing !== null) ||
      (recordSearchDTO.locationEasting !== null &&
        recordSearchDTO.locationNorthing !== null)
    );
  }

  eventReason: any;
}
