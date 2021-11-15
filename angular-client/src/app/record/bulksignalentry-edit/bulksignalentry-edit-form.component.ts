import { PersonService } from '../../person/person.service';
import { forkJoin } from 'rxjs';
import { Component, OnInit, ViewChild } from '@angular/core';
import { OptionsService } from '../../common/options.service';
import { BirdService } from '../../bird/bird.service';
import { PagedResponse } from '../../domain/response/paged-response';
import { BirdCriteria } from '../../bird/bird.criteria';
import { map } from 'rxjs/operators';
import { RecordEntity } from '../../domain/record.entity';
import { ObserverEntity } from '../../domain/observer.entity';
import { RecordService } from '../record.service';
import { ModalDirective } from 'ngx-bootstrap/modal';

import * as moment from 'moment';
import { BatteryLifeEntity } from '../../domain/batterylife.entity';
import { ValidationMessage } from '../../domain/response/validation-message';
import { BirdSearchDTO } from '../../bird/bird-search-dto';

@Component({
  selector: 'app-bulksignalentry-edit-form',
  templateUrl: './bulksignalentry-edit-form.component.html',
})
export class BulkSignalEntryEditFormComponent implements OnInit {
  private manualMethod = 'Manual';
  private errolMethod = 'Errol';
  private skyRangerMethod = 'Sky Ranger';
  private recorderRole = 'Recorder';
  methods: string[] = [
    this.manualMethod,
    this.errolMethod,
    this.skyRangerMethod,
  ];
  method: string;
  island: string;
  personID: string;
  locationName: string;
  dateTime: Date;
  entries: any[];
  messages: ValidationMessage[];

  @ViewChild('createRecordsModal', { static: true })
  public createRecordsModal: ModalDirective;

  constructor(
    public optionsService: OptionsService,
    private birdService: BirdService,
    private recordService: RecordService,
    private personService: PersonService
  ) {}

  ngOnInit() {}

  onShowBirds() {
    this.entries = null;
    if (this.island !== null || this.island.trim() !== '') {
      const birdCriteria = new BirdCriteria();
      birdCriteria.currentIsland = this.island;
      birdCriteria.pageSize = 10000;
      birdCriteria.showAlive = true;
      birdCriteria.excludeEgg = true;
      this.birdService
        .search(birdCriteria)
        .pipe(
          map((response: PagedResponse) => {
            let entries = [];
            entries = response.results.map((bird: BirdSearchDTO) => {
              const entry = {};
              entry['birdID'] = bird.birdID;
              return entry;
            });
            return entries;
          })
        )
        .subscribe((response: any[]) => {
          this.entries = response;
        });
    }
  }

  onSignalChange(item: any) {
    item['loading'] = false;
    item['failed'] = false;
    item['created'] = false;
    if (
      item['signal'] &&
      this.locationName &&
      this.method === this.manualMethod
    ) {
      item['notes'] = `Signal received from ${this.locationName}`;
    }
  }

  onCreateRecords() {
    if (this.dateTime && moment(this.dateTime).isAfter(moment())) {
      const message = new ValidationMessage();
      message.messageText = 'Must be a historical date';
      this.messages = [message];
    } else {
      this.createRecordsModal.show();
    }
  }

  onConfirmCreateRecords() {
    this.createRecords();
  }

  createRecords() {
    this.entries
      .filter((i) => i['signal'])
      .forEach((i) => {
        i['messages'] = null;
        i['loading'] = true;
        i['failed'] = false;
        i['created'] = false;
        const record = new RecordEntity();
        record.birdID = i['birdID'];
        record.comments = i['notes'];
        record.entryDateTime = new Date();
        record.dateTime = this.dateTime;
        record.island = this.island;
        record.observerList = [];

        const observer = new ObserverEntity();
        observer.personID = this.personID;
        observer.observationRoles = [this.recorderRole];
        record.observerList.push(observer);

        record.recordType = 'Signal only';
        record.activity = 'Non-Specified Roosting';

        // always save battery life
        if (i['batteryLife1']) {
          record.batteryLife = new BatteryLifeEntity();
          record.batteryLife.batteryLife1 = i['batteryLife1'];
          record.batteryLife.batteryLife2 = i['batteryLife2'];
        }

        if (this.method === this.manualMethod) {
          record.errol = false;
        } else if (this.method === this.errolMethod) {
          record.errol = true;
        } else if (this.method === this.skyRangerMethod) {
          record.errol = false;
          record.reason = 'Sky Ranger';
        }
        // use a fork join in case there are other subscriptions needed to fully populate the person in future
        // TODO consider moving this logic into PersonService.save
        // eslint-disable-next-line @typescript-eslint/no-unused-expressions
        forkJoin([this.personService.findById(this.personID)]).subscribe(
          (results) => {
            // set the observer capacity before doing the save
            const person = results[0];
            observer.observerCapacity = person.currentCapacity;
            this.recordService.save(record).subscribe(
              (response) => {
                if (response.messages.length > 0) {
                  // just append the validation messages
                  i['messages'] = response.messages;
                  i['loading'] = false;
                  i['failed'] = true;
                } else {
                  i['loading'] = false;
                  i['created'] = true;
                  i['signal'] = false;
                  i['notes'] = '';
                  i['batteryLife1'] = '';
                  i['batteryLife2'] = '';
                }
              },
              () => {
                i['loading'] = false;
                i['failed'] = true;
              }
            );
          }
        ),
          () => {
            i['loading'] = false;
            i['failed'] = true;
          };
      });
    this.createRecordsModal.hide();
  }

  onCancelCreateRecords() {
    this.createRecordsModal.hide();
  }
}
