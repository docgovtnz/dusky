import { Component, Input, OnInit } from '@angular/core';
import { FormArray, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';

import { NoraNetCmShortEntity } from '../../entities/noranetcmshort.entity';

@Component({
  selector: 'app-noranet-cmshort-edit',
  templateUrl: './noranet-cmshort-edit.component.html',
})
export class NoraNetCmShortEditPanelComponent implements OnInit {
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
      new NoraNetCmShortEntity()
    );
  }

  removeItem(i: number) {
    this.myFormArray.removeAt(i);
  }
}
