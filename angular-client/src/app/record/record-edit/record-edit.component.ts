import { combineLatest, EMPTY, empty, merge } from 'rxjs';
import {
  switchMap,
  map,
  distinctUntilChanged,
  filter,
  tap,
} from 'rxjs/operators';

import { Component, OnInit, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import * as moment from 'moment';

import { LocationEntity } from './../../domain/location.entity';
import { EsriMapComponent } from '../../map/esri-map.component';
import { RecordEntity } from '../../domain/record.entity';
import { RecordService } from '../record.service';
import { OptionsService } from '../../common/options.service';
import { BandsEntity } from '../../domain/bands.entity';
import { ChipsEntity } from '../../domain/chips.entity';
import { HealthCheckEntity } from '../../domain/healthcheck.entity';
import { WeightEntity } from '../../domain/weight.entity';
import { MeasureDetailEntity } from '../../domain/measuredetail.entity';
import { TransmitterChangeEntity } from '../../domain/transmitterchange.entity';
import { CaptureDetailEntity } from '../../domain/capturedetail.entity';
import { ValidationMessage } from '../../domain/response/validation-message';
import { FormService } from '../../form/form.service';
import { BaseEntityFormFactory } from './../../form/base-entity-form-factory';
import { BirdEntity } from '../../domain/bird.entity';
import { BirdService } from '../../bird/bird.service';
import { HandRaiseEntity } from '../../domain/handraise.entity';
import { TransferDetailEntity } from '../../domain/transferdetail.entity';
import { LocationService } from '../../location/location.service';
import { SupplementaryFeedingEntity } from '../../domain/supplementaryfeeding.entity';
import { CheckmateEntity } from '../../domain/checkmate.entity';
import { SkyRangerEntity } from '../../domain/skyranger.entity';
import { SnarkDataEntity } from '../../domain/snarkdata.entity';
import { HealthStatusEntity } from '../../domain/healthstatus.entity';
import { ObserverEntity } from '../../domain/observer.entity';
import { RecordSearchDTO } from '../record-search-dto';
import { StandardEntity } from 'src/app/domain/standard.entity';
import { BloodSampleDetailEntity } from 'src/app/domain/bloodsampledetail.entity';
import { IslandEntity } from '../../domain/island.entity';
import { IslandService } from '../../setting/island/island.service';

@Component({
  selector: 'app-record-edit',
  templateUrl: 'record-edit.component.html',
})
export class RecordEditComponent implements OnInit {
  /*
   * This is created by executing this query:
SELECT [RecoveryType], [Reason], [SubReason], [DefaultSignificantEvent]
FROM [Kakapo17].[dbo].[RecordMetadata]
ORDER BY [RecoveryType], [Reason], [SubReason]
   * and then modifying a text copy of with the following pairs of regex find and replace strings
RecoveryType\tReason\tSubReason\tDefaultSignificantEvent\R

\t0$
\tfalse
\t1$
\ttrue
(.*)\t(.*)\t(.*)\t(.*)
      { "default": $4, "parameters": { "recordType": "$1", "reason": "$2", "subReason": "$3" } },
, "[^"]*": "NULL"

([^,:\{]) "
$1"
   */
  static significantEventDefaults = [
    { default: false, parameters: { recordType: 'Capture' } },
    {
      default: true,
      parameters: { recordType: 'Capture', reason: 'Abandoned' },
    },
    { default: true, parameters: { recordType: 'Capture', reason: 'Banding' } },
    // {
    //   default: true,
    //   parameters: { recordType: 'Capture', reason: 'Captivity' },
    // },
    { default: true, parameters: { recordType: 'Capture', reason: 'Dead' } },
    {
      default: true,
      parameters: { recordType: 'Capture', reason: 'Discovery' },
    },
    {
      default: true,
      parameters: { recordType: 'Capture', reason: 'Ex-handraise <150 days' },
    },
    {
      default: true,
      parameters: { recordType: 'Capture', reason: 'First tx fitting' },
    },
    {
      default: false,
      parameters: { recordType: 'Capture', reason: 'Hand-feed' },
    },
    { default: true, parameters: { recordType: 'Capture', reason: 'Health' } },
    {
      default: true,
      parameters: {
        recordType: 'Capture',
        reason: 'Health',
        subReason: 'Crusty bum',
      },
    },
    {
      default: true,
      parameters: {
        recordType: 'Capture',
        reason: 'Health',
        subReason: 'Vaccination',
      },
    },
    {
      default: true,
      parameters: {
        recordType: 'Capture',
        reason: 'Health',
        subReason: 'Aspergillosis',
      },
    },
    {
      default: true,
      parameters: {
        recordType: 'Capture',
        reason: 'Health',
        subReason: 'Parasite',
      },
    },
    {
      default: true,
      parameters: {
        recordType: 'Capture',
        reason: 'Health',
        subReason: 'Injury',
      },
    },
    {
      default: true,
      parameters: {
        recordType: 'Capture',
        reason: 'Health',
        subReason: 'Other',
      },
    },
    {
      default: true,
      parameters: { recordType: 'Capture', reason: 'In captivity' },
    },
    {
      default: true,
      parameters: {
        recordType: 'Capture',
        reason: 'In captivity',
        subReason: 'Weaned',
      },
    },
    {
      default: true,
      parameters: { recordType: 'Capture', reason: 'Microchip' },
    },
    {
      default: false,
      parameters: { recordType: 'Capture', reason: 'Nest check' },
    },
    {
      default: true,
      parameters: {
        recordType: 'Capture',
        reason: 'Nest check',
        subReason: 'Hatching',
      },
    },
    { default: false, parameters: { recordType: 'Capture', reason: 'Sample' } },
    {
      default: true,
      parameters: {
        recordType: 'Capture',
        reason: 'Sample',
        subReason: 'Pulp feather',
      },
    },
    {
      default: true,
      parameters: {
        recordType: 'Capture',
        reason: 'Sample',
        subReason: 'Semen',
      },
    },
    {
      default: true,
      parameters: {
        recordType: 'Capture',
        reason: 'Sample',
        subReason: 'Blood',
      },
    },
    {
      default: true,
      parameters: {
        recordType: 'Capture',
        reason: 'Sample',
        subReason: 'Faecal',
      },
    },
    {
      default: true,
      parameters: {
        recordType: 'Capture',
        reason: 'Sample',
        subReason: 'Swab',
      },
    },
    {
      default: true,
      parameters: { recordType: 'Capture', reason: 'Transfer' },
    },
    {
      default: false,
      parameters: { recordType: 'Capture', reason: 'Tx change' },
    },
    {
      default: false,
      parameters: { recordType: 'Capture', reason: 'Harness check' },
    },
    { default: false, parameters: { recordType: 'Encounter' } },
    {
      default: true,
      parameters: { recordType: 'Encounter', reason: 'Fledged' },
    },
    {
      default: true,
      parameters: { recordType: 'Encounter', reason: 'Home range' },
    },
    {
      default: true,
      parameters: { recordType: 'Encounter', reason: 'Independent' },
    },
    {
      default: true,
      parameters: {
        recordType: 'Encounter',
        reason: 'Outside nest at night (1st night)',
      },
    },
    {
      default: false,
      parameters: { recordType: 'Encounter', reason: 'Radio Tracking' },
    },
    {
      default: false,
      parameters: { recordType: 'Encounter', reason: 'Sighting' },
    },
    {
      default: false,
      parameters: { recordType: 'Encounter', reason: 'Visit' },
    },
    { default: true, parameters: { recordType: 'Nest found' } },
    {
      default: false,
      parameters: { recordType: 'Sign', reason: 'Feeding sign' },
    },
    {
      default: false,
      parameters: { recordType: 'Sign', reason: 'Fighting sign' },
    },
    {
      default: true,
      parameters: { recordType: 'Sign', reason: 'Mating sign' },
    },
    { default: false, parameters: { recordType: 'Signal only' } },
    {
      default: false,
      parameters: { recordType: 'Signal only', reason: 'Sky Ranger' },
    },
    { default: false, parameters: { recordType: 'Snark' } },
    {
      default: false,
      parameters: { recordType: 'Snark', reason: 'Autoweigh' },
    },
    {
      default: false,
      parameters: { recordType: 'Snark', reason: 'Snark at hopper' },
    },
    {
      default: false,
      parameters: { recordType: 'Snark', reason: 'Snark at nest' },
    },
    {
      default: false,
      parameters: { recordType: 'Snark', reason: 'Snark at T&B' },
    },
    {
      default: false,
      parameters: { recordType: 'Snark', reason: 'Snark at roost' },
    },
    {
      default: true,
      parameters: {
        recordType: 'Supplementary feeding',
        reason: 'Sup. food change',
      },
    },
    {
      default: true,
      parameters: {
        recordType: 'Supplementary feeding',
        reason: 'Sup. food finish',
      },
    },
    {
      default: true,
      parameters: {
        recordType: 'Supplementary feeding',
        reason: 'Sup. food start',
      },
    },
    { default: false, parameters: { recordType: 'T&B activity' } },
    { default: true, parameters: { recordType: 'Transfer' } },
    {
      default: true,
      parameters: { recordType: 'Transfer', reason: 'Advocacy' },
    },
    {
      default: true,
      parameters: { recordType: 'Transfer', reason: 'Hand-rearing' },
    },
    { default: true, parameters: { recordType: 'Transfer', reason: 'Health' } },
    {
      default: true,
      parameters: { recordType: 'Transfer', reason: 'Population management' },
    },
    {
      default: true,
      parameters: { recordType: 'Transfer', reason: 'Release' },
    },
    {
      default: true,
      parameters: { recordType: 'Transfer', reason: 'Released from captivity' },
    },
    {
      default: false,
      parameters: { recordType: 'Transmitter', reason: 'Checkmate' },
    },
    {
      default: false,
      parameters: { recordType: 'Transmitter', reason: 'Egg timer' },
    },
    {
      default: false,
      parameters: { recordType: 'Transmitter', reason: 'Standard' },
    },
    {
      default: false,
      parameters: { recordType: 'Transmitter', reason: 'Tx activity' },
    },
    {
      default: true,
      parameters: { recordType: 'Transmitter', reason: 'Tx dropped' },
    },
    { default: false, parameters: { recordType: 'Triangulation' } },
    { default: false, parameters: { recordType: 'tx add' } },
  ];

  static partsMapping = [
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'handRaise',
        'medicationList',
        'spermSampleList',
      ],
      parameters: { recordType: 'Capture', reason: null, subReason: null },
    },
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'handRaise',
        'medicationList',
        'spermSampleList',
      ],
      parameters: {
        recordType: 'Capture',
        reason: 'Abandoned',
        subReason: null,
      },
    },
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'handRaise',
        'medicationList',
        'spermSampleList',
      ],
      parameters: { recordType: 'Capture', reason: 'Banding', subReason: null },
    },
    // {
    //   parts: [
    //     'weight',
    //     'healthCheck',
    //     'healthStatus',
    //     'captureDetail',
    //     'measureDetail',
    //     'transmitterChange',
    //     'harnessChange',
    //     'bands',
    //     'chips',
    //     'bloodSampleDetail',
    //     'swabSampleList',
    //     'otherSampleList',
    //     'handRaise',
    //     'medicationList',
    //     'spermSampleList',
    //   ],
    //   parameters: {
    //     recordType: 'Capture',
    //     reason: 'Captivity',
    //     subReason: null,
    //   },
    // },
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'handRaise',
        'medicationList',
        'spermSampleList',
      ],
      parameters: { recordType: 'Capture', reason: 'Dead', subReason: null },
    },
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'handRaise',
        'medicationList',
        'spermSampleList',
      ],
      parameters: {
        recordType: 'Capture',
        reason: 'Discovery',
        subReason: null,
      },
    },
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'handRaise',
        'medicationList',
        'spermSampleList',
      ],
      parameters: {
        recordType: 'Capture',
        reason: 'Ex-handraise <150 days',
        subReason: null,
      },
    },
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'handRaise',
        'medicationList',
        'spermSampleList',
      ],
      parameters: {
        recordType: 'Capture',
        reason: 'First tx fitting',
        subReason: null,
      },
    },
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'spermSampleList',
        'handRaise',
        'medicationList',
      ],
      parameters: {
        recordType: 'Capture',
        reason: 'Hand-feed',
        subReason: null,
      },
    },
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'handRaise',
        'medicationList',
        'spermSampleList',
      ],
      parameters: { recordType: 'Capture', reason: 'Health', subReason: null },
    },
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'handRaise',
        'medicationList',
        'spermSampleList',
      ],
      parameters: {
        recordType: 'Capture',
        reason: 'Health',
        subReason: 'Crusty bum',
      },
    },
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'handRaise',
        'medicationList',
        'spermSampleList',
      ],
      parameters: {
        recordType: 'Capture',
        reason: 'Health',
        subReason: 'Vaccination',
      },
    },
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'handRaise',
        'medicationList',
        'spermSampleList',
      ],
      parameters: {
        recordType: 'Capture',
        reason: 'Health',
        subReason: 'Aspergillosis',
      },
    },
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'handRaise',
        'medicationList',
        'spermSampleList',
      ],
      parameters: {
        recordType: 'Capture',
        reason: 'Health',
        subReason: 'Parasite',
      },
    },
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'handRaise',
        'medicationList',
        'spermSampleList',
      ],
      parameters: {
        recordType: 'Capture',
        reason: 'Health',
        subReason: 'Injury',
      },
    },
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'handRaise',
        'medicationList',
        'spermSampleList',
      ],
      parameters: {
        recordType: 'Capture',
        reason: 'Health',
        subReason: 'Other',
      },
    },
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'handRaise',
        'medicationList',
        'spermSampleList',
      ],
      parameters: {
        recordType: 'Capture',
        reason: 'In captivity',
        subReason: null,
      },
    },
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'handRaise',
        'medicationList',
        'spermSampleList',
      ],
      parameters: {
        recordType: 'Capture',
        reason: 'In captivity',
        subReason: 'Weaned',
      },
    },
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'spermSampleList',
        'handRaise',
        'medicationList',
      ],
      parameters: {
        recordType: 'Capture',
        reason: 'Microchip',
        subReason: null,
      },
    },
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'handRaise',
        'medicationList',
        'spermSampleList',
      ],
      parameters: {
        recordType: 'Capture',
        reason: 'Nest check',
        subReason: null,
      },
    },
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'handRaise',
        'medicationList',
        'spermSampleList',
      ],
      parameters: {
        recordType: 'Capture',
        reason: 'Nest check',
        subReason: 'Hatching',
      },
    },
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'handRaise',
        'medicationList',
        'spermSampleList',
      ],
      parameters: { recordType: 'Capture', reason: 'Sample', subReason: null },
    },
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'handRaise',
        'medicationList',
        'spermSampleList',
      ],
      parameters: {
        recordType: 'Capture',
        reason: 'Sample',
        subReason: 'Pulp feather',
      },
    },
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'handRaise',
        'medicationList',
        'spermSampleList',
      ],
      parameters: {
        recordType: 'Capture',
        reason: 'Sample',
        subReason: 'Semen',
      },
    },
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'handRaise',
        'medicationList',
        'spermSampleList',
      ],
      parameters: {
        recordType: 'Capture',
        reason: 'Sample',
        subReason: 'Blood',
      },
    },
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'handRaise',
        'medicationList',
        'spermSampleList',
      ],
      parameters: {
        recordType: 'Capture',
        reason: 'Sample',
        subReason: 'Faecal',
      },
    },
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'handRaise',
        'medicationList',
        'spermSampleList',
      ],
      parameters: {
        recordType: 'Capture',
        reason: 'Sample',
        subReason: 'Swab',
      },
    },
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'handRaise',
        'medicationList',
        'spermSampleList',
      ],
      parameters: {
        recordType: 'Capture',
        reason: 'Transfer',
        subReason: null,
      },
    },
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'handRaise',
        'medicationList',
        'spermSampleList',
      ],
      parameters: {
        recordType: 'Capture',
        reason: 'Tx change',
        subReason: null,
      },
    },
    {
      parts: [
        'weight',
        'healthCheck',
        'healthStatus',
        'captureDetail',
        'measureDetail',
        'transmitterChange',
        'harnessChange',
        'bands',
        'chips',
        'bloodSampleDetail',
        'swabSampleList',
        'otherSampleList',
        'handRaise',
        'medicationList',
        'spermSampleList',
      ],
      parameters: {
        recordType: 'Capture',
        reason: 'Harness check',
        subReason: null,
      },
    },
    {
      parts: ['batteryLife'],
      parameters: { recordType: 'Encounter', reason: null, subReason: null },
    },
    {
      parts: ['batteryLife'],
      parameters: {
        recordType: 'Encounter',
        reason: 'Fledged',
        subReason: null,
      },
    },
    {
      parts: ['batteryLife'],
      parameters: {
        recordType: 'Encounter',
        reason: 'Home range',
        subReason: null,
      },
    },
    {
      parts: ['batteryLife'],
      parameters: {
        recordType: 'Encounter',
        reason: 'Independent',
        subReason: null,
      },
    },
    {
      parts: ['batteryLife'],
      parameters: {
        recordType: 'Encounter',
        reason: 'Outside nest at night (1st night)',
        subReason: null,
      },
    },
    {
      parts: ['batteryLife'],
      parameters: {
        recordType: 'Encounter',
        reason: 'Radio Tracking',
        subReason: null,
      },
    },
    {
      parts: ['batteryLife'],
      parameters: {
        recordType: 'Encounter',
        reason: 'Sighting',
        subReason: null,
      },
    },
    {
      parts: ['batteryLife'],
      parameters: { recordType: 'Encounter', reason: 'Visit', subReason: null },
    },
    {
      parts: [],
      parameters: { recordType: 'Nest found', reason: null, subReason: null },
    },
    {
      parts: [],
      parameters: {
        recordType: 'Sign',
        reason: 'Feeding sign',
        subReason: null,
      },
    },
    {
      parts: [],
      parameters: {
        recordType: 'Sign',
        reason: 'Fighting sign',
        subReason: null,
      },
    },
    {
      parts: [],
      parameters: {
        recordType: 'Sign',
        reason: 'Mating sign',
        subReason: null,
      },
    },
    {
      parts: ['batteryLife'],
      parameters: { recordType: 'Signal only', reason: null, subReason: null },
    },
    {
      parts: ['batteryLife'],
      parameters: {
        recordType: 'Signal only',
        reason: 'Sky Ranger',
        subReason: null,
      },
    },
    {
      parts: ['weight', 'snarkData'],
      parameters: { recordType: 'Snark', reason: null, subReason: null },
    },
    {
      parts: ['weight', 'snarkData'],
      parameters: { recordType: 'Snark', reason: 'Autoweigh', subReason: null },
    },
    {
      parts: ['weight', 'snarkData'],
      parameters: {
        recordType: 'Snark',
        reason: 'Snark at hopper',
        subReason: null,
      },
    },
    {
      parts: ['weight', 'snarkData'],
      parameters: {
        recordType: 'Snark',
        reason: 'Snark at roost',
        subReason: null,
      },
    },
    {
      parts: ['weight', 'snarkData'],
      parameters: {
        recordType: 'Snark',
        reason: 'Snark at nest',
        subReason: null,
      },
    },
    {
      parts: ['weight', 'snarkData'],
      parameters: {
        recordType: 'Snark',
        reason: 'Snark at T&B',
        subReason: null,
      },
    },
    {
      parts: ['supplementaryFeeding'],
      parameters: {
        recordType: 'Supplementary feeding',
        reason: null,
        subReason: null,
      },
    },
    {
      parts: ['supplementaryFeeding'],
      parameters: {
        recordType: 'Supplementary feeding',
        reason: 'Sup. food change',
        subReason: null,
      },
    },
    {
      parts: [],
      parameters: {
        recordType: 'Supplementary feeding',
        reason: 'Sup. food finish',
        subReason: null,
      },
    },
    {
      parts: ['supplementaryFeeding'],
      parameters: {
        recordType: 'Supplementary feeding',
        reason: 'Sup. food start',
        subReason: null,
      },
    },
    {
      parts: [],
      parameters: { recordType: 'T&B activity', reason: null, subReason: null },
    },
    {
      parts: ['transferDetail'],
      parameters: { recordType: 'Transfer', reason: null, subReason: null },
    },
    {
      parts: ['transferDetail'],
      parameters: {
        recordType: 'Transfer',
        reason: 'Advocacy',
        subReason: null,
      },
    },
    {
      parts: ['transferDetail'],
      parameters: {
        recordType: 'Transfer',
        reason: 'Hand-rearing',
        subReason: null,
      },
    },
    {
      parts: ['transferDetail'],
      parameters: { recordType: 'Transfer', reason: 'Health', subReason: null },
    },
    {
      parts: ['transferDetail'],
      parameters: {
        recordType: 'Transfer',
        reason: 'Population management',
        subReason: null,
      },
    },
    {
      parts: ['transferDetail'],
      parameters: {
        recordType: 'Transfer',
        reason: 'Release',
        subReason: null,
      },
    },
    {
      parts: ['transferDetail'],
      parameters: {
        recordType: 'Transfer',
        reason: 'Released from captivity',
        subReason: null,
      },
    },
    {
      parts: ['checkmate'],
      parameters: {
        recordType: 'Transmitter',
        reason: 'Checkmate',
        subReason: null,
      },
    },
    {
      parts: ['eggTimer'],
      parameters: {
        recordType: 'Transmitter',
        reason: 'Egg timer',
        subReason: null,
      },
    },
    {
      parts: ['standard'],
      parameters: {
        recordType: 'Transmitter',
        reason: 'Standard',
        subReason: null,
      },
    },
    {
      parts: [],
      parameters: {
        recordType: 'Transmitter',
        reason: 'Tx activity',
        subReason: null,
      },
    },
    {
      parts: ['transmitterChange', 'bands', 'chips'],
      parameters: {
        recordType: 'Transmitter',
        reason: 'Tx dropped',
        subReason: null,
      },
    },
    {
      parts: ['locationBearingList'],
      parameters: {
        recordType: 'Triangulation',
        reason: null,
        subReason: null,
      },
    },
    {
      parts: [],
      parameters: { recordType: 'tx add', reason: null, subReason: null },
    },
  ];

  // New Form layout

  private _recordEntity: RecordEntity;
  private formFactory: BaseEntityFormFactory;
  messages: ValidationMessage[] = [];

  myFormGroup: FormGroup;

  isNew: boolean;
  dataLoaded = false;
  isMedMandatory = false;

  bandHistory: BandsEntity;
  chipHistory: ChipsEntity;
  birdEntity: BirdEntity;
  locationEntity: LocationEntity;
  islandEntity: IslandEntity;

  _mapComponent: EsriMapComponent;

  bloodSampleNamePrefix: string;
  swabSampleNamePrefix: string;
  otherSampleNamePrefix: string;
  spermSampleNamePrefix: string;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public optionsService: OptionsService,
    private formService: FormService,
    private service: RecordService,
    private birdService: BirdService,
    private locationService: LocationService,
    private islandService: IslandService
  ) {}

  ngOnInit(): void {
    // Take the latest value from each of the supplied observables and execute the subscriber
    // function. Note: the subscribe method isn't called until all observables have issued at
    // least one value. This subscription will keep being fired when there are more values,
    // however the createFormFactory() observable should only issue one value.
    combineLatest(
      this.route.params,
      this.route.queryParams,
      this.formService.createFormFactory('RecordEntity')
    ).subscribe(([params, queryParams, formFactory]) => {
      this.dataLoaded = false;
      this.formFactory = formFactory;
      if (queryParams.newEntity) {
        this.newEntity(queryParams.recordType);
      } else {
        this.loadEntity(params.id);
      }
      this.isNew = queryParams.newEntity === 'true';
    });
  }

  get recordEntity(): RecordEntity {
    return this._recordEntity;
  }

  set recordEntity(value: RecordEntity) {
    this._recordEntity = value;
    this.showInMap();
    this.syncForm();
  }

  syncForm(): void {
    if (this.formFactory) {
      this.myFormGroup = this.formFactory.createForm(this._recordEntity);

      // TODO this next bit need a proper refactor
      // have in effect before patching
      // load the bird for other components to use
      this.myFormGroup.controls.birdID.valueChanges
        .pipe(
          tap(() => {
            this.birdEntity = null;
          }),
          switchMap((birdID: string) =>
            birdID ? this.birdService.findById(birdID) : empty()
          )
        )
        .subscribe((response) => {
          this.birdEntity = response;
          this.showInMap();
        });
      // load the island entity if Triangulation type
      if (
        this._recordEntity.recordType ===
          OptionsService.RECORD_TYPE_TRIANGULATION &&
        !this._recordEntity.id
      ) {
        this.myFormGroup.controls.island.valueChanges
          .pipe(
            tap(() => {
              this.islandEntity = null;
            }),
            switchMap((island: string) =>
              island ? this.islandService.findByName(island) : EMPTY
            )
          )
          .subscribe((response) => {
            this.islandEntity = response;
            this.showInMap();
          });
      }
      // load the location for other components to use
      this.myFormGroup.controls.locationID.valueChanges
        .pipe(
          tap(() => {
            this.locationEntity = null;
          }),
          switchMap((locationID: string) =>
            locationID ? this.locationService.findById(locationID) : empty()
          )
        )
        .subscribe((response) => {
          this.locationEntity = response;
          this.showInMap();
        });

      // set the sample prefixes
      merge(
        this.myFormGroup.controls.birdID.valueChanges.pipe(
          map((birdID) => [birdID, this.myFormGroup.controls.dateTime.value])
        ),
        this.myFormGroup.controls.dateTime.valueChanges.pipe(
          map((dateTime) => [this.myFormGroup.controls.birdID.value, dateTime])
        )
      )
        .pipe(
          tap(() => {
            this.bloodSampleNamePrefix = null;
            this.swabSampleNamePrefix = null;
            this.otherSampleNamePrefix = null;
            this.spermSampleNamePrefix = null;
          }),
          filter(([birdID, dateTime]) => birdID && dateTime),
          switchMap(([birdID, dateTime]) =>
            this.birdService
              .findById(birdID)
              .pipe(map((b) => [b.houseID, dateTime]))
          ),
          filter(([houseID, dateTime]) => houseID && dateTime),
          map(
            ([houseID, dateTime]) =>
              houseID + '-' + moment(dateTime).format('DDMMYY')
          )
        )
        .subscribe((recordSampleNamePrefix) => {
          if (recordSampleNamePrefix) {
            this.bloodSampleNamePrefix = recordSampleNamePrefix + '-B-';
            this.swabSampleNamePrefix = recordSampleNamePrefix + '-SW-';
            this.otherSampleNamePrefix = recordSampleNamePrefix + '-O-';
            this.spermSampleNamePrefix = recordSampleNamePrefix + '-SP-';
          }
        });

      this.myFormGroup.patchValue(this._recordEntity);
      // special case for transmitter and chip change
      if (
        this._recordEntity.transmitterChange &&
        this._recordEntity.transmitterChange.txFrom &&
        !this._recordEntity.transmitterChange.txTo
      ) {
        (this.myFormGroup.controls
          .transmitterChange as FormGroup).controls.txTo.setValue('Removed');
      }
      if (this._recordEntity.chips && !this._recordEntity.chips.microchip) {
        (this.myFormGroup.controls
          .chips as FormGroup).controls.microchip.setValue('Removed');
      }

      // TODO this next bit need a proper refactor
      // have in effect after patching so values are not changed for existing records
      // populate the island from the selected bird and
      // populate the from island from if this is a transfer
      const islandControl = this.myFormGroup.controls['island'];
      this.myFormGroup.controls.birdID.valueChanges
        .pipe(
          tap(() => {
            if (
              this._recordEntity.recordType ===
              OptionsService.RECORD_TYPE_TRANSFER
            ) {
              (this.myFormGroup.controls
                .transferDetail as FormGroup).controls.transferFromIsland.setValue(
                null
              );
            }
            islandControl.setValue(null);
          }),
          switchMap((birdID: string) =>
            birdID ? this.birdService.findById(birdID) : empty()
          )
        )
        .subscribe((response) => {
          if (
            this._recordEntity.recordType ===
            OptionsService.RECORD_TYPE_TRANSFER
          ) {
            (this.myFormGroup.controls
              .transferDetail as FormGroup).controls.transferFromIsland.setValue(
              response.currentIsland
            );
          }
          if (
            this._recordEntity.recordType !==
            OptionsService.RECORD_TYPE_TRANSFER
          ) {
            islandControl.setValue(response.currentIsland);
          }
        });
      // populate the to island if this is a transfer
      islandControl.valueChanges.subscribe((island) => {
        if (
          this._recordEntity.recordType ===
            OptionsService.RECORD_TYPE_TRANSFER &&
          island
        ) {
          (this.myFormGroup.controls
            .transferDetail as FormGroup).controls.transferToIsland.setValue(
            island
          );
        }
      });
      // populate the from location from the selected bird if this is a transfer
      this.myFormGroup.controls.birdID.valueChanges
        .pipe(
          tap(() => {
            if (
              this._recordEntity.recordType ===
              OptionsService.RECORD_TYPE_TRANSFER
            ) {
              (this.myFormGroup.controls
                .transferDetail as FormGroup).controls.transferFromLocationID.setValue(
                null
              );
            }
          }),
          switchMap((birdID: string) =>
            birdID ? this.birdService.findById(birdID) : empty()
          )
        )
        .subscribe((response) => {
          if (
            this._recordEntity.recordType ===
            OptionsService.RECORD_TYPE_TRANSFER
          ) {
            (this.myFormGroup.controls
              .transferDetail as FormGroup).controls.transferFromLocationID.setValue(
              response.currentLocationID
            );
          }
        });
      // populate the to location if this is a transfer
      const locationIDControl = this.myFormGroup.controls.locationID;
      locationIDControl.valueChanges.subscribe((locationID) => {
        if (
          this._recordEntity.recordType === OptionsService.RECORD_TYPE_TRANSFER
        ) {
          (this.myFormGroup.controls
            .transferDetail as FormGroup).controls.transferToLocationID.setValue(
            locationID
          );
        }
      });

      // populate the northing and easting from the selected location
      const eastingControl = this.myFormGroup.controls['easting'];
      const northingControl = this.myFormGroup.controls['northing'];
      this.myFormGroup.controls['locationID'].valueChanges
        .pipe(
          tap(() => {
            eastingControl.setValue(null);
            northingControl.setValue(null);
          }),
          switchMap((locationID: string) =>
            locationID ? this.locationService.findById(locationID) : empty()
          )
        )
        .subscribe((response) => {
          eastingControl.setValue(response.easting);
          northingControl.setValue(response.northing);
        });

      // populate the to magnetic declination if this is a triangulation on new record
      const magneticDeclinationControl = this.myFormGroup.controls[
        'magneticDeclination'
      ];
      const magneticDeclinationAsOfYearControl = this.myFormGroup.controls[
        'magneticDeclinationAsOfYear'
      ];

      islandControl.valueChanges.subscribe((island) => {
        if (
          this._recordEntity.recordType ===
            OptionsService.RECORD_TYPE_TRIANGULATION &&
          !this._recordEntity.id &&
          island
        ) {
          magneticDeclinationControl.setValue(null);
          magneticDeclinationAsOfYearControl.setValue(null);

          this.islandService.findByName(island).subscribe((response) => {
            if (response) {
              magneticDeclinationControl.setValue(response.magneticDeclination);
              magneticDeclinationAsOfYearControl.setValue(
                response.magneticDeclinationAsOfYear
              );
            }
          });
        }
      });

      // KD-729 clear Sub Reason after changing the value in Reason
      this.myFormGroup.controls.reason.valueChanges.subscribe(() => {
        this.myFormGroup.controls.subReason.setValue(null);
      });

      // set and unset the significant event based on reason and sub reason
      merge(
        this.myFormGroup.controls.reason.valueChanges.pipe(
          map((reason) => [
            this._recordEntity.recordType,
            reason,
            this.myFormGroup.controls.subReason.value,
          ])
        ),
        this.myFormGroup.controls.subReason.valueChanges.pipe(
          map((subReason) => [
            this._recordEntity.recordType,
            this.myFormGroup.controls.reason.value,
            subReason,
          ])
        )
      )
        .pipe(
          map(([recordType, reason, subReason]) =>
            this.getSignificantEventDefault({
              recordType,
              reason,
              subReason,
            })
          )
        )
        .subscribe((defaultValue) => {
          if (defaultValue !== null) {
            this.myFormGroup.controls.significantEvent.setValue(defaultValue);
          }
        });

      // set the significant event default if this is a new record
      if (this._recordEntity.id === null) {
        this.myFormGroup.controls.significantEvent.setValue(
          this.getSignificantEventDefault({
            recordType: this._recordEntity.recordType,
          })
        );
      }

      // add and remove parts based on reason and sub reason
      merge(
        this.myFormGroup.controls.reason.valueChanges.pipe(
          map((reason) => [
            this._recordEntity.recordType,
            reason,
            this.myFormGroup.controls.subReason.value,
          ])
        ),
        this.myFormGroup.controls.subReason.valueChanges.pipe(
          map((subReason) => [
            this._recordEntity.recordType,
            this.myFormGroup.controls.reason.value,
            subReason,
          ])
        )
      )
        .pipe(
          distinctUntilChanged(),
          map(([recordType, reason, subReason]) => ({
            recordType,
            reason,
            subReason,
          }))
        )
        .subscribe((parameters) => {
          this.addRemoveParts(parameters, this.myFormGroup);
        });

      // sync recordEntity object with details needed for dynamically updating the map component
      const c = this.myFormGroup.controls;
      c.dateTime.valueChanges.subscribe((dateTime) => {
        this._recordEntity.dateTime = dateTime;
        this.showInMap();
      });
      c.easting.valueChanges.subscribe((easting) => {
        this._recordEntity.easting = easting;
        this.showInMap();
      });
      c.northing.valueChanges.subscribe((northing) => {
        this._recordEntity.northing = northing;
        this.showInMap();
      });
      c.island.valueChanges.subscribe((island) => {
        this._recordEntity.island = island;
        this.showInMap();
      });
      c.magneticDeclination.valueChanges.subscribe((magneticDeclination) => {
        this._recordEntity.magneticDeclination = magneticDeclination;
        this.showInMap();
      });
      c.magneticDeclinationAsOfYear.valueChanges.subscribe(
        (magneticDeclinationAsOfYear) => {
          this._recordEntity.magneticDeclinationAsOfYear = magneticDeclinationAsOfYear;
          this.showInMap();
        }
      );

      // add parts as per rules but don't remove in case this is old data
      this.addParts(
        {
          recordType: this._recordEntity.recordType,
          reason: this.myFormGroup.controls.reason.value,
          subReason: this.myFormGroup.controls.subReason.value,
        },
        this.myFormGroup
      );
    }
  }

  @ViewChild(EsriMapComponent)
  set mapComponent(mapComponent: EsriMapComponent) {
    this._mapComponent = mapComponent;
    this.showInMap();
  }

  showInMap() {
    if (this._mapComponent) {
      let islandName = null;
      if (this.birdEntity) {
        islandName = this.birdEntity.currentIsland;
      }

      if (this._recordEntity) {
        // If there's a record and the record has an island then upgrade the island to the record's island
        if (this.recordEntity.island) {
          islandName = this._recordEntity.island;
        }

        const dto = new RecordSearchDTO();
        dto.dateTime = this._recordEntity.dateTime;
        dto.easting = this._recordEntity.easting;
        dto.island = this._recordEntity.island;
        dto.northing = this._recordEntity.northing;
        dto.recordType = this._recordEntity.recordType;
        if (this.birdEntity) {
          dto.birdID = this.birdEntity.id;
          dto.birdName = this.birdEntity.birdName;
        }
        if (this.locationEntity) {
          dto.locationID = this.locationEntity.id;
          dto.locationName = this.locationEntity.locationName;
          dto.locationEasting = this.locationEntity.easting;
          dto.locationNorthing = this.locationEntity.northing;
        }

        if (dto.birdName) {
          dto.mapFeatureType = dto.birdName;
        } else {
          // I don't know what we set this to, so set to something
          dto.mapFeatureType = 'Unknown';
        }

        this._mapComponent.selectedRecord = dto;
      } else {
        this._mapComponent.selectedRecord = null;
      }

      // Now set the island
      this._mapComponent.selectedIslandName = islandName;
    }
  }

  newEntity(recordType: string) {
    const e = new RecordEntity();
    e.recordType = recordType;
    // add the first observer to prompt the use to enter it
    e.observerList = [new ObserverEntity()];
    // set value after init so that form gets patched correctly
    this.recordEntity = e;
    this.dataLoaded = true;
  }

  loadEntity(id: string) {
    this.service.findById(id).subscribe((entity) => {
      // set value after init so that form gets patched correctly
      this.recordEntity = entity;
      this.dataLoaded = true;
    });
  }

  onSave(): void {
    this.messages = [];
    this.syncRecordEntity();
    this.service.save(this.recordEntity).subscribe((response) => {
      this.recordEntity = response.model;
      this.messages = response.messages;
      if (this.messages.length === 0) {
        this.onCancel();
      }
    });
  }

  /**
   * Smoosh together an array from the entity object (record entity) with changes made to an array in the form (form group)
   *
   * @param arrayFromEntity Original array
   * @param arrayFromForm Form (modified) array
   * @returns Combined array
   */
  private syncArray(arrayFromEntity: any[], arrayFromForm: any[]): any[] {
    const newArray = [];

    arrayFromForm.forEach((item) => {
      if (item._originalIndex !== null) {
        const originalItem = arrayFromEntity[item._originalIndex];
        Object.assign(originalItem, item);
        delete originalItem['_originalIndex'];
        newArray.push(originalItem);
      } else {
        newArray.push(item);
      }
    });
    return newArray;
  }

  syncRecordEntity(): void {
    // TODO this code is very similar to SampleEditComponent.syncSampleEntity()
    const raw = this.myFormGroup.getRawValue();
    Object.keys(raw).forEach((part) => {
      // if the part exists on the record entity
      if (this.parts.includes(part) && this._recordEntity[part]) {
        if (Array.isArray(this._recordEntity[part])) {
          this._recordEntity[part] = this.syncArray(
            this._recordEntity[part],
            raw[part]
          );
        } else {
          // [KD-904] Handle special case whereby there is an array (bloodSampleList) nested in an object (bloodSampleDetail)
          const updatedBloodSampleList = Object.keys(
            this._recordEntity[part]
          ).includes('bloodSampleList')
            ? (this._recordEntity[part]['bloodSampleList'] = this.syncArray(
                this._recordEntity[part]['bloodSampleList'],
                raw[part]['bloodSampleList']
              ))
            : null;

          // save over the top of old value, perserving any existing values not updated by the form
          Object.assign(
            this._recordEntity[part],
            raw[part],
            updatedBloodSampleList && {
              bloodSampleList: updatedBloodSampleList,
            }
          );
        }
      } else {
        // use the form value as the new value for the part
        this._recordEntity[part] = raw[part];
      }
    });
    // remove any part for which there is no longer a control group
    this.parts.forEach((p) => {
      if (!this.myFormGroup.controls[p]) {
        this._recordEntity[p] = null;
      }
    });
    // remove any part for which all contained values are null
    this.parts.forEach((p) => {
      const partObject = this._recordEntity[p];
      if (partObject) {
        // start by changing any empty string values to null
        Object.keys(partObject).forEach((k) => {
          if (partObject[k] === '') {
            partObject[k] = null;
          }
        });
        // now if all contained values are null, remove the part
        if (Object.keys(partObject).every((k) => partObject[k] === null)) {
          this._recordEntity[p] = null;
        }
      }
    });
    // special case for transmitterChange
    const tc = this._recordEntity.transmitterChange;
    if (tc && !(tc.txTo || tc.newTxFineTune)) {
      this._recordEntity.transmitterChange = null;
    }
    // convert how user indicates a transmitter or chip has been removed into proper updates
    // we must do this after all empty parts have been removed (otherwise the transmitterChange and chips part would be removed)
    if (tc && tc.txTo === 'Removed') {
      tc.txTo = null;
    }
    const c = this._recordEntity.chips;
    if (c && c.microchip === 'Removed') {
      c.microchip = null;
    }
  }

  onCancel(): void {
    if (this.recordEntity.id) {
      this.router.navigate(['/record/' + this.recordEntity.id]);
    } else {
      this.router.navigate(['/record']);
    }
  }

  get bands(): boolean {
    return this._recordEntity && this._recordEntity.bands ? true : false;
  }

  set bands(value: boolean) {
    if (this.bands !== value) {
      if (value) {
        this._recordEntity.bands = new BandsEntity();
      } else {
        this._recordEntity.bands = null;
      }
    }
  }

  get captureDetail(): boolean {
    return this.myFormGroup && this.myFormGroup.controls.captureDetail
      ? true
      : false;
  }

  set captureDetail(value: boolean) {
    if (this.captureDetail !== value) {
      if (value) {
        (this.myFormGroup as any).captureDetailAdd(new CaptureDetailEntity());
      } else {
        (this.myFormGroup as any).captureDetailRemove();
      }
    }
  }

  get checkmate(): boolean {
    return this.myFormGroup && this.myFormGroup.controls.checkmate
      ? true
      : false;
  }

  set checkmate(value: boolean) {
    if (this.checkmate !== value) {
      if (value) {
        (this.myFormGroup as any).checkmateAdd(new CheckmateEntity());
      } else {
        (this.myFormGroup as any).checkmateRemove();
      }
    }
  }

  get bloodSampleDetail(): boolean {
    return this.myFormGroup && this.myFormGroup.controls.bloodSampleDetail
      ? true
      : false;
  }

  set bloodSampleDetail(value: boolean) {
    if (this.bloodSampleDetail !== value) {
      if (value) {
        (this.myFormGroup as any).bloodSampleDetailAdd(
          new BloodSampleDetailEntity()
        );
      } else {
        (this.myFormGroup as any).bloodSampleDetailRemove();
      }
    }
  }

  get chips(): boolean {
    return this._recordEntity && this._recordEntity.chips ? true : false;
  }

  set chips(value: boolean) {
    if (this.chips !== value) {
      if (value) {
        this._recordEntity.chips = new ChipsEntity();
      } else {
        this._recordEntity.chips = null;
      }
    }
  }

  get handRaise(): boolean {
    return this.myFormGroup && this.myFormGroup.controls.handRaise
      ? true
      : false;
  }

  set handRaise(value: boolean) {
    if (this.handRaise !== value) {
      if (value) {
        (this.myFormGroup as any).handRaiseAdd(new HandRaiseEntity());
      } else {
        (this.myFormGroup as any).handRaiseRemove();
      }
    }
  }

  get healthCheck(): boolean {
    return this._recordEntity && this._recordEntity.healthCheck ? true : false;
  }

  set healthCheck(value: boolean) {
    if (this.healthCheck !== value) {
      if (value) {
        this._recordEntity.healthCheck = new HealthCheckEntity();
      } else {
        this._recordEntity.healthCheck = null;
      }
    }
  }

  get healthStatus(): boolean {
    return this.myFormGroup && this.myFormGroup.controls.healthStatus
      ? true
      : false;
  }

  set healthStatus(value: boolean) {
    if (this.healthStatus !== value) {
      if (value) {
        (this.myFormGroup as any).healthStatusAdd(new HealthStatusEntity());
      } else {
        (this.myFormGroup as any).healthStatusRemove();
      }
    }
  }

  get measureDetail(): boolean {
    return this.myFormGroup && this.myFormGroup.controls.measureDetail
      ? true
      : false;
  }

  set measureDetail(value: boolean) {
    if (this.measureDetail !== value) {
      if (value) {
        (this.myFormGroup as any).measureDetailAdd(new MeasureDetailEntity());
      } else {
        (this.myFormGroup as any).measureDetailRemove();
      }
    }
  }

  get medicationList(): boolean {
    return this.myFormGroup && this.myFormGroup.controls.medicationList
      ? true
      : false;
  }

  set medicationList(value: boolean) {
    if (this.medicationList !== value) {
      if (value) {
        (this.myFormGroup as any).medicationListAdd([]);
      } else {
        (this.myFormGroup as any).medicationListRemove();
      }
    }
  }

  get batteryLife(): boolean {
    return this.myFormGroup && this.myFormGroup.controls.batteryLife
      ? true
      : false;
  }

  set batteryLife(value: boolean) {
    if (this.batteryLife !== value) {
      if (value) {
        (this.myFormGroup as any).batteryLifeAdd(new SkyRangerEntity());
      } else {
        (this.myFormGroup as any).batteryLifeRemove();
      }
    }
  }

  get supplementaryFeeding(): boolean {
    return this.myFormGroup && this.myFormGroup.controls.supplementaryFeeding
      ? true
      : false;
  }

  set supplementaryFeeding(value: boolean) {
    if (this.supplementaryFeeding !== value) {
      if (value) {
        (this.myFormGroup as any).supplementaryFeedingAdd(
          new SupplementaryFeedingEntity()
        );
      } else {
        (this.myFormGroup as any).supplementaryFeedingRemove();
      }
    }
  }

  get transferDetail(): boolean {
    return this.myFormGroup && this.myFormGroup.controls.transferDetail
      ? true
      : false;
  }

  set transferDetail(value: boolean) {
    if (this.transferDetail !== value) {
      if (value) {
        (this.myFormGroup as any).transferDetailAdd(new TransferDetailEntity());
      } else {
        (this.myFormGroup as any).transferDetailRemove();
      }
    }
  }

  get transmitterChange(): boolean {
    return this._recordEntity && this._recordEntity.transmitterChange
      ? true
      : false;
  }

  set transmitterChange(value: boolean) {
    if (this.transmitterChange !== value) {
      if (value) {
        this._recordEntity.transmitterChange = new TransmitterChangeEntity();
      } else {
        this._recordEntity.transmitterChange = null;
      }
    }
  }

  get weight(): boolean {
    return this.myFormGroup && this.myFormGroup.controls.weight ? true : false;
  }

  set weight(value: boolean) {
    if (this.weight !== value) {
      if (value) {
        (this.myFormGroup as any).weightAdd(new WeightEntity());
      } else {
        (this.myFormGroup as any).weightRemove();
      }
    }
  }

  get snarkData(): boolean {
    return this.myFormGroup && this.myFormGroup.controls.snarkData
      ? true
      : false;
  }

  set snarkData(value: boolean) {
    if (this.snarkData !== value) {
      if (value) {
        (this.myFormGroup as any).snarkDataAdd(new SnarkDataEntity());
      } else {
        (this.myFormGroup as any).snarkDataRemove();
      }
    }
  }

  get standard(): boolean {
    return this.myFormGroup && this.myFormGroup.controls.standard
      ? true
      : false;
  }

  set standard(value: boolean) {
    if (this.standard !== value) {
      if (value) {
        (this.myFormGroup as any).standardAdd(new StandardEntity());
      } else {
        (this.myFormGroup as any).standardRemove();
      }
    }
  }

  private getSignificantEventDefault(parameters: any) {
    const matches: any[] = RecordEditComponent.significantEventDefaults.filter(
      (entry) => {
        const entryKeys = Object.keys(entry.parameters);
        const parameterKeys = Object.keys(parameters);
        // this is a possible match if
        // there is a value for each parameter from the entry
        // and the value for the parameter matches the value for the entry
        return entryKeys.every(
          (i) =>
            parameterKeys.includes(i) && parameters[i] === entry.parameters[i]
        );
      }
    );
    // if there are no matches then return null (which means no default)
    if (matches.length === 0) {
      console.log(
        `No significant event default found for parameters ${JSON.stringify(
          parameters
        )}`
      );
      // return null to signify there is no default (and keep things error free for user)
      return null;
    }
    // now determine what is the most specific match based on the number of parameters matched
    // if there is a draw then throw an error because this is a conflict
    // first add a parameterCount field to each of the entries
    const matchesWithCounts = matches.map((entry) => ({
      parameterCount: Object.keys(entry.parameters).length,
      parameters: entry.parameters,
      default: entry.default,
    }));
    // use the added parameterCount information to get the highest count
    const highestCount = matchesWithCounts
      .map((entry) => entry.parameterCount)
      .sort()
      .reverse()[0];
    // find all the entries with the highest count
    const matchesWithHighestCount = matchesWithCounts.filter(
      (i) => i.parameterCount === highestCount
    );
    // if we have only one the we have a match, return the default value of the entry
    if (matchesWithHighestCount.length === 1) {
      return matchesWithHighestCount[0].default;
    } else {
      // we have more than one match so throw an error with details of the matching entries
      console.log(
        `Multiple significant event defaults found for parameters ${JSON.stringify(
          parameters
        )}`
      );
      // return null to signify there is no default (and keep things error free for user)
      return null;
    }
  }

  private addRemoveParts(
    parameters: { recordType: string; reason?: string; subReason?: string },
    fg: FormGroup
  ): void {
    this.addParts(parameters, fg);
    this.removeParts(parameters, fg);
  }

  private addParts(
    parameters: { recordType: string; reason?: string; subReason?: string },
    fg: FormGroup
  ): void {
    const partsToAdd = this.getMatchingParts(parameters);
    partsToAdd.forEach((p) => {
      if (!fg.controls[p] && fg[p + 'Add']) {
        fg[p + 'Add']();
      }
    });
  }

  private removeParts(
    parameters: { recordType: string; reason?: string; subReason?: string },
    fg: FormGroup
  ): void {
    const partsToAdd = this.getMatchingParts(parameters);
    const partsToRemove = this.parts.filter((i) => partsToAdd.indexOf(i) < 0);
    partsToRemove.forEach((p) => {
      if (fg.controls[p] && fg[p + 'Remove']) {
        fg[p + 'Remove']();
      }
    });
  }

  private get parts(): string[] {
    let parts = [];
    RecordEditComponent.partsMapping.forEach(
      (i) => (parts = parts.concat(i.parts))
    );
    // sort and filter out duplicates
    parts = parts.sort().filter((v, i, a) => a.indexOf(v) === i);
    return parts;
  }

  drugChange(hasDrug: boolean): void {
    this.isMedMandatory = hasDrug && !!this.myFormGroup.controls.handRaise;
  }

  private getMatchingParts(parameters: {
    recordType: string;
    reason?: string;
    subReason?: string;
  }): string[] {
    const matches: any[] = RecordEditComponent.partsMapping.filter((entry) => {
      const entryKeys = Object.keys(entry.parameters);
      const parameterKeys = Object.keys(parameters);
      // this is a possible match if
      // there is a value for each parameter from the entry
      // and the value for the parameter matches the value for the entry
      return entryKeys.every(
        (i) =>
          parameterKeys.includes(i) && parameters[i] === entry.parameters[i]
      );
    });
    // if there are no matches then return [] (which means no parts)
    if (matches.length === 0) {
      console.log(
        `No parts list found for parameters ${JSON.stringify(parameters)}`
      );
      // return [] to signify there is no parts (and keep things error free for user)
      return [];
    }
    // now determine what is the most specific match based on the number of parameters matched
    // if there is a draw then throw an error because this is a conflict
    // first add a parameterCount field to each of the entries
    const matchesWithCounts = matches.map((entry) => ({
      parameterCount: Object.keys(entry.parameters).length,
      parameters: entry.parameters,
      parts: entry.parts,
    }));
    // use the added parameterCount information to get the highest count
    const highestCount = matchesWithCounts
      .map((entry) => entry.parameterCount)
      .sort()
      .reverse()[0];
    // find all the entries with the highest count
    const matchesWithHighestCount = matchesWithCounts.filter(
      (i) => i.parameterCount === highestCount
    );
    // if we have only one the we have a match, return the parts of the entry
    if (matchesWithHighestCount.length === 1) {
      return matchesWithHighestCount[0].parts;
    } else {
      // we have more than one match so throw an error with details of the matching entries
      console.log(
        `Multiple parts list found for parameters ${JSON.stringify(parameters)}`
      );
      // return [] to signify there are no parts (and keep things error free for user)
      return [];
    }
  }
}
