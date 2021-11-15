import { Component, Input, OnInit } from '@angular/core';
import { FormArray, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';

import { NoraNetCmLongEntity } from '../../entities/noranetcmlong.entity';

@Component({
  selector: 'app-noranet-cmlong-edit',
  templateUrl: './noranet-cmlong-edit.component.html',
})
export class NoraNetCmLongEditPanelComponent implements OnInit {
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
      new NoraNetCmLongEntity()
    );
  }

  removeItem(i: number) {
    this.myFormArray.removeAt(i);
  }
}
