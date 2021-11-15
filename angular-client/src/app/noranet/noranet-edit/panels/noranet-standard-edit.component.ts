import { Component, Input, OnInit } from '@angular/core';
import { FormArray, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';

import { NoraNetStandardEntity } from '../../entities/noranetstandard.entity';

@Component({
  selector: 'app-noranet-standard-edit',
  templateUrl: './noranet-standard-edit.component.html',
})
export class NoraNetStandardEditPanelComponent implements OnInit {
  @Input()
  myFormArray: FormArray;

  constructor(private router: Router) {}

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
      new NoraNetStandardEntity()
    );
  }

  removeItem(i: number) {
    this.myFormArray.removeAt(i);
  }
}
