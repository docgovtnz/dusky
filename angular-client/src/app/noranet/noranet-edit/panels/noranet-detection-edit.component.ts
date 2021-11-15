import { Component, Input, OnInit } from '@angular/core';
import { FormArray, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';

import { OptionsService } from '../../../common/options.service';
import { NoraNetDetectionEntity } from '../../entities/noranetdetection.entity';

@Component({
  selector: 'app-noranet-detection-edit',
  templateUrl: './noranet-detection-edit.component.html',
})
export class NoraNetDetectionEditPanelComponent implements OnInit {
  @Input()
  myFormArray: FormArray;

  constructor(public optionsService: OptionsService, private router: Router) {}

  ngOnInit(): void {}

  get myFormGroup(): FormGroup {
    return this.myFormArray.parent as FormGroup;
  }

  get myFormArrayName(): string {
    return Object.keys(this.myFormGroup.controls).find(
      (name) => this.myFormGroup.get(name) === this.myFormArray
    );
  }

  addItem() {
    const added: FormGroup = (this.myFormArray as any).addItem(
      new NoraNetDetectionEntity()
    );
  }

  removeItem(i: number) {
    this.myFormArray.removeAt(i);
  }
}
