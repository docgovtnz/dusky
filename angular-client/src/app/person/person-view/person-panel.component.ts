import { Component, Input, OnInit } from '@angular/core';

import { PersonEntity } from '../../domain/person.entity';

@Component({
  selector: 'app-person-panel',
  templateUrl: 'person-panel.component.html',
})
export class PersonPanelComponent implements OnInit {
  @Input()
  personRevision: PersonEntity;

  constructor() {}

  ngOnInit(): void {}
}
