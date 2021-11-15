import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-controlblock',
  templateUrl: './controlblock.component.html',
  styleUrls: ['./controlblock.component.scss'],
})
export class ControlBlockComponent implements OnInit {
  @Input()
  title: string;

  @Input()
  noBody = false;

  @Input()
  noOffset = false;

  @Input()
  withSpacer = false;

  constructor() {}

  ngOnInit(): void {}
}
