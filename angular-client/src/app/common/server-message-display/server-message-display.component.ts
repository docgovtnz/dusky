import { Component, Input, OnInit } from '@angular/core';
import { ValidationMessage } from '../../domain/response/validation-message';

@Component({
  selector: 'app-server-message-display',
  templateUrl: './server-message-display.component.html',
})
export class ServerMessageDisplayComponent implements OnInit {
  @Input()
  validationMessages: ValidationMessage[];

  constructor() {}

  ngOnInit() {}

  onClosed() {
    // set validationMessages to null so the alert is recreated next time messages is set
    // this solves a problem where the alert doesn't come back after dismissing it
    this.validationMessages = null;
  }
}
