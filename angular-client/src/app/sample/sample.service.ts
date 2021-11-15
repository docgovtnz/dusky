import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { SampleBaseService } from './sample-base.service';
import { RecordEntity } from '../domain/record.entity';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { PagedResponse } from '../domain/response/paged-response';
import { TestStatsDto } from './test-stats-dto';
import { ResultRankDto } from './result-rank-dto';
import { ResultDto } from './result-dto';
import { SampleCriteria } from './sample.criteria';

@Injectable()
export class SampleService extends SampleBaseService {
  public sampleCriteria: SampleCriteria;
  bloodSampleContainer: string;

  constructor(http: HttpClient) {
    super(http);
  }

  public getRecord(sampleID: any): Observable<RecordEntity> {
    return this.http.get<RecordEntity>(`api/sample/${sampleID}/record`).pipe(
      map((response: RecordEntity) => {
        // this code is copied from record-base.service.ts
        response.dateTime =
          response.dateTime === null ? null : new Date(response.dateTime);
        response.entryDateTime =
          response.entryDateTime === null
            ? null
            : new Date(response.entryDateTime);
        response.firstArriveTime =
          response.firstArriveTime === null
            ? null
            : new Date(response.firstArriveTime);
        response.lastDepartTime =
          response.lastDepartTime === null
            ? null
            : new Date(response.lastDepartTime);
        response.modifiedTime =
          response.modifiedTime === null
            ? null
            : new Date(response.modifiedTime);
        return response;
      })
    );
  }

  public search(criteria: any): Observable<PagedResponse> {
    return this.http.post<PagedResponse>('/api/sample/searchDTO', criteria);
  }

  public nextSampleName(sampleNamePrefix, existing) {
    let nextLetter = null;
    if (sampleNamePrefix) {
      const letters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('');
      // make resilient to bad data
      if (!existing) {
        existing = [];
      }
      // determine which letters have already been used
      const used = letters.filter((l) =>
        existing.includes(sampleNamePrefix + l)
      );
      // determine the next letter based on the used letters
      if (used.length === 0) {
        nextLetter = letters[0];
      } else {
        const lastUsed = used[used.length - 1];
        // use the index of the last used letter to determine the next letter
        // unless the last is very last
        const nextIndex = letters.indexOf(lastUsed) + 1;
        if (nextIndex < letters.length) {
          nextLetter = letters[nextIndex];
        } else {
          // the last is very last so use the first unused instead
          const firstUnused = letters.find((l) => !used.includes(l));
          if (firstUnused) {
            nextLetter = firstUnused;
          }
        }
      }
    }
    if (nextLetter) {
      return sampleNamePrefix + nextLetter;
    } else {
      return null;
    }
  }

  public getHaematologyTestsStats(type): Observable<TestStatsDto[]> {
    return this.http.get<TestStatsDto[]>(
      '/api/sample/haematologyTests/stats?type=' + encodeURIComponent(type)
    );
  }

  public getHaematologyTestsRanks(type, results: ResultDto[]) {
    return this.http.post<ResultRankDto[]>(
      '/api/sample/haematologyTests/ranks',
      { sampleType: type, results }
    );
  }

  public getChemistryAssaysStats(type): Observable<TestStatsDto[]> {
    return this.http.get<TestStatsDto[]>(
      '/api/sample/chemistryAssays/stats?type=' + encodeURIComponent(type)
    );
  }

  public getChemistryAssaysRanks(type, results: ResultDto[]) {
    return this.http.post<ResultRankDto[]>(
      '/api/sample/chemistryAssays/ranks',
      { sampleType: type, results }
    );
  }
}
