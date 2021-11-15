import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-multi-value-view',
  templateUrl: './multi-value-view.component.html',
})
export class MultiValueViewComponent implements OnInit {
  @Input()
  values: string[];

  constructor() {}

  ngOnInit() {}
}
