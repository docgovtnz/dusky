import { BirdEntity } from './../../domain/bird.entity';
import { Component, Input, OnInit } from '@angular/core';
import { OptionsService } from '../../common/options.service';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-healthcheck-edit-form',
  templateUrl: './healthcheck-edit-form.component.html',
})
export class HealthCheckEditFormComponent implements OnInit {
  // New Form layout
  @Input()
  myFormGroup: FormGroup;

  @Input()
  birdEntity: BirdEntity;

  constructor(public optionsService: OptionsService) {}

  ngOnInit() {}
}
