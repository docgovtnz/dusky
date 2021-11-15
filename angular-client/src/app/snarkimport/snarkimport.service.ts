import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { SnarkImportBaseService } from './snarkimport-base.service';

import { SnarkImportEntity } from './../domain/snarkimport.entity';
import { SnarkCheckRequestDTO } from './snark-check-request-dto';
import { SnarkCheckResultDTO } from './snark-check-result-dto';
import { SnarkIncludeRequestDTO } from './snark-include-request-dto';
import { SnarkIncludeResultDTO } from './snark-include-result-dto';
import { SnarkImportRequest } from './snark-import-request';
import { Response } from '../domain/response/response';

@Injectable()
export class SnarkImportService extends SnarkImportBaseService {
  constructor(http: HttpClient) {
    super(http);
  }

  // Step 0. File is uploaded to /check, where some logic is run to pre-select/unselect rows for import
  public check(request: SnarkCheckRequestDTO): Observable<SnarkCheckResultDTO> {
    return this.http
      .post<SnarkCheckResultDTO>('/api/snarkimport/check', request)
      .pipe(
        map((result: SnarkCheckResultDTO) => {
          result.eveningList.forEach(
            (e) => (e.date = e.date === null ? null : new Date(e.date))
          );
          result.eveningList.forEach((e) =>
            e.snarkRecordList.forEach(
              (sr) =>
                (sr.arriveDateTime =
                  sr.arriveDateTime === null
                    ? null
                    : new Date(sr.arriveDateTime))
            )
          );
          result.eveningList.forEach((e) =>
            e.snarkRecordList.forEach(
              (sr) =>
                (sr.departDateTime =
                  sr.departDateTime === null
                    ? null
                    : new Date(sr.departDateTime))
            )
          );
          result.mysteryWeightList.forEach(
            (mw) => (mw.dateTime = mw.dateTime === null ? null : mw.dateTime)
          );
          return result;
        })
      );
  }

  // Step 1. After user has selected relevant rows and the correct bird(s) detected, submit a list of records with include true/false
  public include(
    request: SnarkIncludeRequestDTO
  ): Observable<SnarkIncludeResultDTO> {
    return this.http
      .post<SnarkIncludeResultDTO>('/api/snarkimport/include', request)
      .pipe(map((result: SnarkIncludeResultDTO) => result));
  }

  // Step 2. Final import
  public import(
    request: SnarkImportRequest
  ): Observable<Response<SnarkImportEntity>> {
    return this.http.post<Response<SnarkImportEntity>>(
      '/api/snarkimport/import',
      request
    );
  }
}
