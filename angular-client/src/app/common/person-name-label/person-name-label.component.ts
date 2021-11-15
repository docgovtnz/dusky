import { Component, Input, OnInit } from '@angular/core';
import { PersonService } from '../../person/person.service';
import { PersonEntity } from '../../domain/person.entity';

@Component({
  selector: 'app-person-name-label',
  templateUrl: './person-name-label.component.html',
})
export class PersonNameLabelComponent implements OnInit {
  private _personId: string;
  person: PersonEntity;

  @Input()
  makeStrong = false;

  constructor(private personService: PersonService) {}

  ngOnInit() {}

  @Input()
  set personId(value: string) {
    this._personId = value;
    if (value) {
      this.personService
        .findById(value)
        .subscribe((person: PersonEntity) => (this.person = person));
    } else {
      this.person = null;
    }
  }
}
