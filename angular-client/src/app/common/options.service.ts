/* eslint-disable @typescript-eslint/naming-convention */

import { AsyncSubject, Observable, of, Subject } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Cache } from './cache';
import { BirdSummaryDto } from './bird-summary-dto';
import { LocationSummaryDto } from './location-summary-dto';
import { PersonSummaryDto } from './person-summary-dto';

/**
 * Let's keep these methods in an alphabetical sort order.
 */
@Injectable()
export class OptionsService {
  static RECORD_TYPE_SUPPLEMENTARY_FEEDING = 'Supplementary feeding';
  static RECORD_TYPE_CAPTURE = 'Capture';
  static RECORD_TYPE_SIGN = 'Sign';
  static RECORD_TYPE_TRANSFER = 'Transfer';
  static RECORD_TYPE_TRIANGULATION = 'Triangulation';
  static RECORD_REASON_ABANDONED = 'Abandoned';
  static RECORD_REASON_MATING_SIGN = 'Mating sign';
  static RECORD_REASON_SUP_FOOD_FINISH = 'Sup. food finish';
  static TRANSMITTER_STATUS_DEPLOYED_OLD = 'Deployed old';
  static TRANSMITTER_STATUS_DEPLOYED_NEW = 'Deployed new';
  static OBSERVER_ROLE_INSPECTOR = 'Inspector';

  /*
   * This is created by executing this query:
SELECT [RecoveryType], [Reason], [SubReason]
FROM [Kakapo17].[dbo].[RecordMetadata]
ORDER BY [RecoveryType], [Reason], [SubReason]
   * and then modifying a text copy of with the following pairs of regex find and replace strings
RecoveryType\tReason\tSubReason\R

(.*)\t(.*)\t(.*)
      { 'recordType': '$1', 'reason': '$2', 'subReason': '$3' },
, '([^']*)': 'NULL'
, '$1': null
([^,:\{]) '
$1'
   */
  static recordOptions = [
    { recordType: 'Capture', reason: null, subReason: null },
    { recordType: 'Capture', reason: 'Abandoned', subReason: null },
    { recordType: 'Capture', reason: 'Banding', subReason: null },
    // { recordType: 'Capture', reason: 'Captivity', subReason: null },
    { recordType: 'Capture', reason: 'Dead', subReason: null },
    { recordType: 'Capture', reason: 'Discovery', subReason: null },
    {
      recordType: 'Capture',
      reason: 'Ex-handraise <150 days',
      subReason: null,
    },
    { recordType: 'Capture', reason: 'First tx fitting', subReason: null },
    { recordType: 'Capture', reason: 'Hand-feed', subReason: null },
    { recordType: 'Capture', reason: 'Harness check', subReason: null },
    { recordType: 'Capture', reason: 'Health', subReason: null },
    { recordType: 'Capture', reason: 'Health', subReason: 'Crusty bum' },
    { recordType: 'Capture', reason: 'Health', subReason: 'Vaccination' },
    { recordType: 'Capture', reason: 'Health', subReason: 'Aspergillosis' },
    { recordType: 'Capture', reason: 'Health', subReason: 'Parasite' },
    { recordType: 'Capture', reason: 'Health', subReason: 'Injury' },
    { recordType: 'Capture', reason: 'Health', subReason: 'Other' },
    { recordType: 'Capture', reason: 'In captivity', subReason: null },
    { recordType: 'Capture', reason: 'In captivity', subReason: 'Weaned' },
    { recordType: 'Capture', reason: 'Microchip', subReason: null },
    { recordType: 'Capture', reason: 'Nest check', subReason: null },
    { recordType: 'Capture', reason: 'Nest check', subReason: 'Hatching' },
    { recordType: 'Capture', reason: 'Sample', subReason: null },
    { recordType: 'Capture', reason: 'Sample', subReason: 'Pulp feather' },
    { recordType: 'Capture', reason: 'Sample', subReason: 'Semen' },
    { recordType: 'Capture', reason: 'Sample', subReason: 'Blood' },
    { recordType: 'Capture', reason: 'Sample', subReason: 'Faecal' },
    { recordType: 'Capture', reason: 'Sample', subReason: 'Swab' },
    { recordType: 'Capture', reason: 'Transfer', subReason: null },
    { recordType: 'Capture', reason: 'Tx change', subReason: null },
    { recordType: 'Encounter', reason: null, subReason: null },
    { recordType: 'Encounter', reason: 'Fledged', subReason: null },
    { recordType: 'Encounter', reason: 'Home range', subReason: null },
    { recordType: 'Encounter', reason: 'Independent', subReason: null },
    {
      recordType: 'Encounter',
      reason: 'Outside nest at night (1st night)',
      subReason: null,
    },
    { recordType: 'Encounter', reason: 'Radio Tracking', subReason: null },
    { recordType: 'Encounter', reason: 'Sighting', subReason: null },
    { recordType: 'Encounter', reason: 'Visit', subReason: null },
    { recordType: 'Nest found', reason: null, subReason: null },
    { recordType: 'Sign', reason: 'Feeding sign', subReason: null },
    { recordType: 'Sign', reason: 'Fighting sign', subReason: null },
    { recordType: 'Sign', reason: 'Mating sign', subReason: null },
    { recordType: 'Signal only', reason: null, subReason: null },
    { recordType: 'Signal only', reason: 'Sky Ranger', subReason: null },
    { recordType: 'Snark', reason: null, subReason: null },
    { recordType: 'Snark', reason: 'Autoweigh', subReason: null },
    { recordType: 'Snark', reason: 'Snark at hopper', subReason: null },
    { recordType: 'Snark', reason: 'Snark at nest', subReason: null },
    { recordType: 'Snark', reason: 'Snark at T&B', subReason: null },
    { recordType: 'Snark', reason: 'Snark at roost', subReason: null },
    {
      recordType: 'Supplementary feeding',
      reason: 'Sup. food change',
      subReason: null,
    },
    {
      recordType: 'Supplementary feeding',
      reason: 'Sup. food finish',
      subReason: null,
    },
    {
      recordType: 'Supplementary feeding',
      reason: 'Sup. food start',
      subReason: null,
    },
    { recordType: 'T&B activity', reason: null, subReason: null },
    { recordType: 'Transfer', reason: null, subReason: null },
    { recordType: 'Transfer', reason: 'Advocacy', subReason: null },
    { recordType: 'Transfer', reason: 'Hand-rearing', subReason: null },
    { recordType: 'Transfer', reason: 'Health', subReason: null },
    {
      recordType: 'Transfer',
      reason: 'Population management',
      subReason: null,
    },
    { recordType: 'Transfer', reason: 'Release', subReason: null },
    {
      recordType: 'Transfer',
      reason: 'Released from captivity',
      subReason: null,
    },
    { recordType: 'Transmitter', reason: 'Checkmate', subReason: null },
    { recordType: 'Transmitter', reason: 'Egg timer', subReason: null },
    { recordType: 'Transmitter', reason: 'Standard', subReason: null },
    { recordType: 'Transmitter', reason: 'Tx activity', subReason: null },
    { recordType: 'Transmitter', reason: 'Tx dropped', subReason: null },
    { recordType: 'Triangulation', reason: null, subReason: null },
    { recordType: 'tx add', reason: null, subReason: null },
  ];

  static observerRoleOptions = [
    { recordType: '*', includesInspector: '*', observerRole: 'Recorder' },
    {
      recordType: 'Capture',
      includesInspector: '*',
      observerRole: 'Bystander',
    },
    { recordType: 'Capture', includesInspector: '*', observerRole: 'Holder' },
    { recordType: 'Capture', includesInspector: '*', observerRole: 'Bleeder' },
    {
      recordType: 'Capture',
      includesInspector: '*',
      observerRole: OptionsService.OBSERVER_ROLE_INSPECTOR,
    },
    { recordType: 'Capture', includesInspector: true, observerRole: 'Feeder' },
    {
      recordType: 'Capture',
      includesInspector: true,
      observerRole: 'Inseminator',
    },
    {
      recordType: 'Capture',
      includesInspector: true,
      observerRole: 'Measurer',
    },
    { recordType: 'Capture', includesInspector: true, observerRole: 'Sampler' },
    {
      recordType: 'Capture',
      includesInspector: true,
      observerRole: 'Semen collector',
    },
    {
      recordType: 'Capture',
      includesInspector: true,
      observerRole: 'Transmitter fitter',
    },
    {
      recordType: 'Capture',
      includesInspector: true,
      observerRole: 'Vaccinator',
    },
    {
      recordType: 'Capture',
      includesInspector: true,
      observerRole: 'Microchipper',
    },
  ];

  //REFRESH_MILLIS = 60 * 1000 * 5;

  birdNameCache: Cache<string[]>;
  islandNameCache: Cache<string[]>;
  locationNameCache: Cache<string[]>;
  recordTypesCache: Cache<string[]>;
  recordReasonsCache: Cache<string[]>;
  recordActivitiesCache: Cache<string[]>;
  birdSummariesCache: Cache<BirdSummaryDto[]>;
  locationSummariesCache: Cache<LocationSummaryDto[]>;
  personSummariesCache: Cache<PersonSummaryDto[]>;
  otherSampleTypesCache: Cache<string[]>;
  spermDiluentsCache: Cache<string[]>;
  sampleCategoriesCache: Cache<string[]>;
  sampleTypesCache: Cache<string[]>;
  txMortalityTypesCache: Cache<string[]>;
  optionListsCache: Cache<{ [key: string]: any }>;
  optionListTitleCache: Cache<{ [key: string]: string }>;

  constructor(private http: HttpClient) {
    // use an async subject so all subscribers get the last value returned before completion
    const refreshPeriodMills$: Subject<number> = new AsyncSubject();
    // populate the sync subject with data coming from an http get call
    this.http
      .get<number>('/api/options/clientCacheRefreshPeriodInMillis')
      .subscribe((refreshPeriodInMillis) => {
        refreshPeriodMills$.next(refreshPeriodInMillis);
        refreshPeriodMills$.complete();
      });
    this.birdNameCache = new Cache('Bird Names', refreshPeriodMills$, () =>
      this.getCacheValue<string[]>('/api/options/birdNames')
    );
    this.islandNameCache = new Cache('Island Names', refreshPeriodMills$, () =>
      this.getCacheValue<string[]>('/api/options/islandNames')
    );
    this.locationNameCache = new Cache(
      'Location Names',
      refreshPeriodMills$,
      () => this.getCacheValue<string[]>('/api/options/locationNames')
    );
    this.recordTypesCache = new Cache('Record Types', refreshPeriodMills$, () =>
      this.getCacheValue<string[]>('/api/options/recordTypes')
    );
    this.recordReasonsCache = new Cache(
      'Record Reasons',
      refreshPeriodMills$,
      () => this.getCacheValue<string[]>('/api/options/recordReasons')
    );
    this.recordActivitiesCache = new Cache(
      'Record Activities',
      refreshPeriodMills$,
      () => this.getCacheValue<string[]>('/api/options/recordActivities')
    );
    this.birdSummariesCache = new Cache(
      'Bird Summaries',
      refreshPeriodMills$,
      () => this.getCacheValue<BirdSummaryDto[]>('/api/options/birdSummaries')
    );
    this.locationSummariesCache = new Cache(
      'Location Summaries',
      refreshPeriodMills$,
      () =>
        this.getCacheValue<LocationSummaryDto[]>(
          '/api/options/locationSummaries'
        )
    );
    this.personSummariesCache = new Cache(
      'Person Summaries',
      refreshPeriodMills$,
      () =>
        this.getCacheValue<PersonSummaryDto[]>('/api/options/personSummaries')
    );
    this.otherSampleTypesCache = new Cache(
      'Other Sample Types',
      refreshPeriodMills$,
      () => this.getCacheValue<string[]>('/api/options/otherSampleTypes')
    );
    this.spermDiluentsCache = new Cache(
      'Sperm Diluents',
      refreshPeriodMills$,
      () => this.getCacheValue<string[]>('/api/options/spermDiluents')
    );
    this.sampleCategoriesCache = new Cache(
      'Sample Categories',
      refreshPeriodMills$,
      () => this.getCacheValue<string[]>('/api/options/sampleCategories')
    );
    this.sampleTypesCache = new Cache('Sample Types', refreshPeriodMills$, () =>
      this.getCacheValue<string[]>('/api/options/sampleTypes')
    );
    this.txMortalityTypesCache = new Cache(
      'Tx Mortality Types',
      refreshPeriodMills$,
      () => this.getCacheValue<string[]>('/api/options/txMortalityTypes')
    );
    this.optionListsCache = new Cache('Option Lists', refreshPeriodMills$, () =>
      this.getCacheValue<{ [key: string]: any }>('/api/options/optionLists')
    );
    this.optionListTitleCache = new Cache(
      'Option List Titles',
      refreshPeriodMills$,
      () =>
        this.getCacheValue<{ [key: string]: string }>(
          '/api/options/optionListTitles'
        )
    );
  }

  private getCacheValue<T>(url: string): Observable<T> {
    /* eslint-disable @typescript-eslint/naming-convention */
    return this.http.get<T>(url, {
      headers: new HttpHeaders({ x_service_level: 'optional' }),
    });
    /* eslint-enable */
  }

  getCaches(): Cache<any>[] {
    return [
      this.birdNameCache,
      this.islandNameCache,
      this.locationNameCache,
      this.recordTypesCache,
      this.recordReasonsCache,
      this.recordActivitiesCache,
      this.birdSummariesCache,
      this.locationSummariesCache,
      this.personSummariesCache,
      this.otherSampleTypesCache,
      this.sampleCategoriesCache,
      this.sampleTypesCache,
      this.txMortalityTypesCache,
      this.optionListsCache,
      this.optionListTitleCache,
    ];
  }

  stopCaches() {
    console.log('Stopping all caches');
    this.getCaches().forEach((cache) => cache.stop());
  }

  resetBirdCache() {
    this.birdSummariesCache.forceRefresh();
    this.birdNameCache.forceRefresh();
  }

  resetPersonCache() {
    this.personSummariesCache.forceRefresh();
  }

  resetLocationCache() {
    this.locationSummariesCache.forceRefresh();
    this.locationNameCache.forceRefresh();
  }

  getAlive(): string[] {
    return ['true', 'false'];
  }

  getBirdSummaries(): Observable<BirdSummaryDto[]> {
    //return this.http.get<BirdSummaryDto[]>('/api/options/birdSummaries');
    return this.birdSummariesCache.get();
  }

  getLocationSummaries(): Observable<LocationSummaryDto[]> {
    //return this.http.get<LocationSummaryDto[]>('/api/options/locationSummaries');
    return this.locationSummariesCache.get();
  }

  getPersonSummaries(): Observable<PersonSummaryDto[]> {
    //return this.http.get<PersonSummaryDto[]>('/api/options/personSummaries');
    return this.personSummariesCache.get();
  }

  getOptions(
    optionType: string,
    currentValue?: string,
    parameters?: any
  ): Observable<string[]> {
    // return both static and dynamic options as Observable<string[]> so that can be used interchangable
    // for static options, convert to Observable using the of rsjx operator

    // we first check if there is a option list that matches the optionType
    // this may require waiting for a response from the service but usually it won't because the option list is cached and preloaded

    return this.optionListsCache.get().pipe(
      switchMap((ol) => {
        if (ol[optionType]) {
          // there is a static list for this optionType
          let options = ol[optionType];
          // TransmitterStatusOptions is a special case were we don't include deployed as an available option
          if (optionType === 'TransmitterStatusOptions') {
            options = options.filter(
              (o) => o !== 'Deployed old' && o !== 'Deployed new'
            );
          }
          // if a currentValue was passed in then add it to the list
          if (currentValue) {
            // only add it if it doesn't already exist
            if (!options.includes(currentValue)) {
              options = [currentValue].concat(options);
            }
          }
          // always add null to the beginning of the list
          options = [null].concat(options);
          // TODO we use to remove duplicate but assume that isn't required anymore
          return of(options);
        } else {
          // there is no static list for this optionType
          // so it must be one of the special cases below
          switch (optionType) {
            case 'BirdName':
              return this.getBirdNames();
            case 'IslandName':
              return this.getIslandNames();
            case 'User':
              return this.getUser();
            case 'LocationName':
              return this.getLocationNames();
            case 'TxId':
              return this.getTxIds();
            case 'RecordActivities':
              return this.getRecordActivities();
            case 'RecordType':
              return this.getRecordTypes();
            case 'RecordReasons':
              return this.getRecordReasons();
            case 'RecordReasonSelect':
              return of(this.getRecordReasonSelect(currentValue, parameters));
            case 'RecordSubReasonSelect':
              return of(
                this.getRecordSubReasonSelect(currentValue, parameters)
              );
            case 'TxMortalityTypes':
              return this.getTxMortalityTypes();
            case 'ObserverRoleOptions':
              return of(this.getObserverRoleOptions(parameters));
            case 'OtherSampleTypes':
              return this.getOtherSampleTypes();
            case 'SpermDiluents':
              return this.getSpermDiluents();
            case 'SampleTypes':
              return this.getSampleTypes();
            case 'Stations':
              return this.getStations();
            default:
              return of(null);
          }
        }
      })
    );
  }

  getOptionList(
    optionListName: string,
    optionListText: string
  ): Observable<string[]> {
    const criteria = {
      optionListName,
      optionListText,
    };

    return this.http.post<string[]>('/api/optionList/findDisplay', criteria);
  }

  getBirdNames(): Observable<string[]> {
    //return this.http.get<string[]>('/api/options/birdNames');
    return this.birdNameCache.get();
  }

  getIslandNames(): Observable<string[]> {
    //return this.http.get<string[]>('/api/options/islandNames');
    return this.islandNameCache.get();
  }

  getLocationNames(): Observable<string[]> {
    //return this.http.get<string[]>('/api/options/locationNames');
    return this.locationNameCache.get();
  }

  getTxIds(): Observable<string[]> {
    return this.http.get<string[]>('/api/options/txIds');
  }

  getObserverRoleOptions(parameters?: any): string[] {
    let parameterKeys = [];
    if (parameters) {
      parameterKeys = Object.keys(parameters);
    }
    const availableOptions = OptionsService.observerRoleOptions
      .filter((i) =>
        parameterKeys.every((k) => parameters[k] === i[k] || i[k] === '*')
      )
      .map((i) => i.observerRole)
      .filter((i) => i !== null);
    let options = availableOptions;
    options = availableOptions.filter((v, i, a) => a.indexOf(v) === i);
    return options;
  }

  getSpecialRoles(): string[] {
    return [
      'Bleeder',
      'Feeder',
      'Inseminator',
      'Measurer',
      'Sampler',
      'Semen collector',
      'Transmitter fitter',
    ];
  }

  getRecordActivities(): Observable<string[]> {
    //return this.http.get<string[]>('/api/options/recordActivities');
    return this.recordActivitiesCache.get();
  }

  getRecordTypes(): Observable<string[]> {
    //return this.http.get<string[]>('/api/options/recordTypes');
    return this.recordTypesCache.get();
  }

  getRecordReasons(): Observable<string[]> {
    //return this.http.get<string[]>('/api/options/recordReasons');
    return this.recordReasonsCache.get();
  }

  getRecordReasonSelect(currentValue?: string, parameters?: any): string[] {
    let parameterKeys = [];
    if (parameters) {
      parameterKeys = Object.keys(parameters);
    }
    const availableOptions = OptionsService.recordOptions
      .filter((i) => parameterKeys.every((k) => parameters[k] === i[k]))
      .map((i) => i.reason)
      .filter((i) => i !== null);
    let options = availableOptions;
    if (currentValue) {
      options = [currentValue].concat(options).sort();
    }
    options = [null].concat(options).filter((v, i, a) => a.indexOf(v) === i);
    return options;
  }

  getRecordSubReasonSelect(currentValue?: string, parameters?: any): string[] {
    let parameterKeys = [];
    if (parameters) {
      parameterKeys = Object.keys(parameters);
    }
    const availableOptions = OptionsService.recordOptions
      .filter((i) => parameterKeys.every((k) => parameters[k] === i[k]))
      .map((i) => i.subReason)
      .filter((i) => i !== null);
    let options = availableOptions;
    if (currentValue) {
      options = [currentValue].concat(options).sort();
    }
    options = [null].concat(options).filter((v, i, a) => a.indexOf(v) === i);
    return options;
  }

  getUser(): Observable<string[]> {
    return this.http.get<string[]>('/api/options/users');
  }

  getAllTransmitters(): Observable<string[]> {
    return this.http.get<string[]>('/api/options/allTransmitters');
  }

  getTxMortalityTypes(): Observable<string[]> {
    return this.txMortalityTypesCache.get().pipe(
      // prepend null to the list as this is used to populate a select and not a typeahead
      map((types) => [null].concat(types))
    );
  }

  getOtherSampleTypes(): Observable<string[]> {
    //return this.http.get<string[]>('/api/options/otherSampleType');
    return this.otherSampleTypesCache.get();
  }

  getSpermDiluents(): Observable<string[]> {
    //return this.http.get<string[]>('/api/options/spermDiluents');
    return this.spermDiluentsCache.get();
  }

  getSampleTypes(): Observable<string[]> {
    //return this.http.get<string[]>('/api/options/sampleType');
    return this.sampleTypesCache.get();
  }

  getOptionListTitles(): Observable<any> {
    return this.optionListTitleCache.get();
  }

  getStations(): Observable<string[]> {
    return this.http.get<string[]>('/api/options/stations');
  }
}
