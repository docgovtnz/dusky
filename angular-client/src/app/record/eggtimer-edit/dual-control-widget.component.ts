import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-dual-control-widget',
  templateUrl: './dual-control-widget.component.html',
})
export class DualControlWidgetComponent implements OnInit {
  @Input()
  label: string;

  @Input()
  control1Name: string;

  @Input()
  control2Name: string;

  @Input()
  myFormGroup: FormGroup;

  ngOnInit() {}
}
