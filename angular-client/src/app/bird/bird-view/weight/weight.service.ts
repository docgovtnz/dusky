import { map, shareReplay } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { WeightSummaryDto } from './weight-summary-dto';
import { WeightRefData } from './weight-ref-data';
import { Moment } from 'moment';
import { EggWeightDTO } from './egg-weight-dto';
import { EggRefData } from './egg-ref-data';
import { Response } from '../../../domain/response/response';
import { BirdCriteria } from '../../bird.criteria';
import { LegendItem } from './legend-item';

@Injectable()
export class WeightService {
  constructor(private http: HttpClient) {}

  weightRefData: Observable<WeightRefData>;

  //todo new method for multibird ******
  findByBirdID(
    birdID: string,
    fromDate: Moment,
    toDate: Moment,
    strategy: string = 'raw'
  ): Observable<WeightSummaryDto[]> {
    let url = '/api/weight/byBirdID?birdID=' + birdID;
    if (fromDate) {
      console.log(fromDate.toISOString());
      url += '&fromDate=' + fromDate.toISOString();
    }
    if (toDate) {
      url += '&toDate=' + toDate.endOf('day').toISOString();
    }

    if (strategy) {
      url += '&strategy=' + strategy;
    }

    return this.http.get<WeightSummaryDto[]>(url).pipe(
      map((response: WeightSummaryDto[]) => {
        response.forEach(
          (dto) =>
            (dto.dateTime =
              dto.dateTime === null ? null : new Date(dto.dateTime))
        );
        return response;
      })
    );
  }

  getReferenceData(): Observable<WeightRefData> {
    return (this.weightRefData = this.http
      .get<WeightRefData>('/api/weight/referenceData')
      .pipe(shareReplay(1)));
  }

  getEggData(birdID: string): Observable<Response<EggWeightDTO[]>> {
    return this.http
      .get<Response<EggWeightDTO[]>>('/api/weight/eggWeights/' + birdID)
      .pipe(shareReplay(1));
  }

  getEggRefData(birdID: string): Observable<Response<EggRefData[]>> {
    return this.http
      .get<Response<EggRefData[]>>('/api/weight/eggReferenceData/' + birdID)
      .pipe(shareReplay(1));
  }

  public getMultiBirdData(
    birdCriteria: BirdCriteria,
    type: string
  ): Observable<LegendItem[]> {
    const url = birdCriteria.toUrl('/api/weight/multiWeightGraph');
    return this.http.get<LegendItem[]>(url + '&type=' + type);
  }

  public getMultiBirdDataWithDate(
    birdCriteria: BirdCriteria,
    type: string
  ): Observable<LegendItem[]> {
    const url = birdCriteria.toUrl('/api/weight/multiWeightGraphWithDate');
    return this.http.get<LegendItem[]>(url + '&type=' + type);
  }

  public getMultiBirdDeltaDataWithDate(
    birdCriteria: BirdCriteria,
    type: string
  ): Observable<LegendItem[]> {
    const url = birdCriteria.toUrl('/api/weight/multiWeightDeltaGraphWithDate');
    return this.http.get<LegendItem[]>(url + '&type=' + type);
  }

  public getMultiBirdEggData(
    birdCriteria: BirdCriteria
  ): Observable<LegendItem[]> {
    const url = birdCriteria.toUrl('/api/weight/multiEggWeightGraph');
    return this.http.get<LegendItem[]>(url);
  }

  public getMultiBirdWeightDeltaData(
    birdCriteria: BirdCriteria,
    type: string
  ): Observable<LegendItem[]> {
    const url = birdCriteria.toUrl('/api/weight/multiWeightDeltaGraph');
    return this.http.get<LegendItem[]>(url + '&type=' + type);
  }

  public formatWeightFromKg(weightInKg: number): string {
    return this.formatWeightInternal(weightInKg, 1000);
  }

  public formatWeightFromGrams(weightInGrams: number): string {
    return this.formatWeightInternal(weightInGrams, 1);
  }

  private formatWeightInternal(weight: number, scale: number) {
    if (weight) {
      weight = weight * scale;

      let labelWeight = weight;
      let units = 'g';
      if (weight > 1000) {
        labelWeight = weight / 1000;
        units = 'kg';
      }

      return labelWeight.toPrecision(4) + ' ' + units;
    } else {
      return '';
    }
  }
}
