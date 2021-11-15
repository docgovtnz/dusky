import { Component, Input, OnInit } from '@angular/core';
import { ObserverEntity } from '../../domain/observer.entity';

@Component({
  selector: 'app-observer-view',
  templateUrl: './observer-view.component.html',
})
export class ObserverViewComponent implements OnInit {
  @Input()
  observers: ObserverEntity[];

  constructor() {}

  ngOnInit() {}
}
