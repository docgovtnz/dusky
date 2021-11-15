import { Observable } from 'rxjs/internal/Observable';
import { Component, Input, OnInit } from '@angular/core';
import { OptionsService } from '../options.service';
import { of, empty } from 'rxjs';
import { PersonService } from '../../person/person.service';
import { FormGroup, FormArray } from '@angular/forms';
import { filter, tap, switchMap, catchError, map } from 'rxjs/operators';

@Component({
  selector: 'app-observer-edit',
  templateUrl: './observer-edit.component.html',
})
export class ObserverEditComponent implements OnInit {
  // New Form layout
  @Input()
  myFormArray: FormArray;

  @Input()
  recordType: string = null;

  observerRoles: Observable<string[]> = of([]);

  constructor(
    public optionsService: OptionsService,
    private personService: PersonService
  ) {}

  ngOnInit() {
    // subscribe to changes to existing components
    this.myFormArray.controls.forEach((item: FormGroup) => {
      item.controls.personID.valueChanges
        .pipe(
          tap(() => item.controls.observerCapacity.setValue(null)),
          filter((personID) => personID !== null),
          switchMap((personID) => this.personService.findById(personID)),
          catchError(() => empty()),
          map((p) => p.currentCapacity)
        )
        .subscribe((currentCapacity) => {
          item.controls.observerCapacity.setValue(currentCapacity);
        });
    });
  }

  get myFormGroup(): FormGroup {
    // return the form group that contains the provided FormArray so that the FormArray bound to the root div element
    // in the template using the FormArrayName directive (there is no FormArrayDirective)
    return this.myFormArray.parent as FormGroup;
  }

  get myFormArrayName(): string {
    // determine the name given to the provided FormArray so that it can be bound to the root div template using the
    // FormArrayName directive (there is no FormArrayDirective)
    return Object.keys(this.myFormGroup.controls).find(
      (name) => this.myFormGroup.get(name) === this.myFormArray
    );
  }

  onAddObserver() {
    const item: FormGroup = (this.myFormArray as any).addItem();
    item.controls.personID.valueChanges
      .pipe(
        tap(() => item.controls.observerCapacity.setValue(null)),
        filter((personID) => personID !== null),
        switchMap((personID) => this.personService.findById(personID)),
        catchError(() => empty()),
        map((p) => p.currentCapacity)
      )
      .subscribe((currentCapacity) => {
        item.controls.observerCapacity.setValue(currentCapacity);
      });
    this.checkRoles(item);
  }

  onRemoveObserver(index: number) {
    this.myFormArray.removeAt(index);
  }

  onSelectObserverRole(item: FormGroup, roleValue: string, target) {
    if (roleValue !== 'XXX') {
      let currentValue: string[] = item.controls.observationRoles.value;
      if (!currentValue) {
        currentValue = [];
      }

      let newValue: string[];
      // if array does not already contain role then add it
      if (currentValue.indexOf(roleValue) < 0) {
        newValue = currentValue.concat([roleValue]);
        newValue.sort();
        item.controls.observationRoles.setValue(newValue);
      }

      // setting target value back to 'XXX' ready for selecting the next role
      target.value = 'XXX';
    }
    this.checkRoles(item);
  }

  onRemoveTagAction(item: FormGroup, tagIdx: number) {
    const currentValue: string[] = item.controls.observationRoles.value;
    if (currentValue) {
      currentValue.splice(tagIdx, 1);
      const newValue = currentValue;
      item.controls.observationRoles.setValue(newValue);
      console.log(newValue);
      this.checkRoles(item);
    }
  }

  checkRoles(item: FormGroup) {
    let includesInspector = false;
    if (item.controls.observationRoles.value) {
      includesInspector = item.controls.observationRoles.value.includes(
        OptionsService.OBSERVER_ROLE_INSPECTOR
      );
    }
    this.observerRoles = this.optionsService.getOptions(
      'ObserverRoleOptions',
      null,
      { recordType: this.recordType, includesInspector }
    );
  }
}
