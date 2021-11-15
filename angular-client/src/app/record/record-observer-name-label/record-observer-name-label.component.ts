import { ObserverEntity } from './../../domain/observer.entity';
import { Component, Input, OnInit } from '@angular/core';
import { PersonService } from '../../person/person.service';
import { RecordEntity } from '../../domain/record.entity';

@Component({
  selector: 'app-record-observer-name-label',
  templateUrl: './record-observer-name-label.component.html',
})
export class RecordObserverNameLabelComponent implements OnInit {
  _recordEntity: RecordEntity;
  observerName: string;

  constructor(private personService: PersonService) {}

  ngOnInit() {}

  @Input()
  set recordEntity(value: RecordEntity) {
    this._recordEntity = value;
    this.observerName = null;
    if (this._recordEntity && this._recordEntity.observerList) {
      const recorder: ObserverEntity = this._recordEntity.observerList.find(
        (o: ObserverEntity) => o.observationRoles.includes('Recorder')
      );
      if (recorder) {
        this.personService.findById(recorder.personID).subscribe((person) => {
          this.observerName = person.name;
        });
      }
    }
  }
}
