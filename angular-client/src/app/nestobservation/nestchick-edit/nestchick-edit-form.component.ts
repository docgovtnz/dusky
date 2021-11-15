import { Subject, Subscription } from 'rxjs';
import {
  startWith,
  map,
  pairwise,
  filter,
  scan,
  debounceTime,
} from 'rxjs/operators';

import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { FormGroup, FormArray } from '@angular/forms';

import { ModalDirective } from 'ngx-bootstrap/modal';

import { NestChickEntity } from '../../domain/nestchick.entity';
import { OptionsService } from '../../common/options.service';
import { ObserverEntity } from '../../domain/observer.entity';
import { FledgedChickEditComponent } from '../fledgedchick-edit/fledgedchick-edit.component';
import { WeanedChickEditComponent } from '../weanedchick-edit/weanedchick-edit.component';
import { BirdService } from '../../bird/bird.service';

@Component({
  selector: 'app-nestchick-edit-form',
  templateUrl: './nestchick-edit-form.component.html',
})
export class NestChickEditFormComponent implements OnInit {
  private _myFormArray: FormArray;

  @ViewChild('fledgedChickModal', { static: true })
  public fledgedChickModal: ModalDirective;

  @ViewChild(FledgedChickEditComponent, { static: true })
  public fledgedChickComponent: FledgedChickEditComponent;

  @ViewChild('weanedChickModal', { static: true })
  public weanedChickModal: ModalDirective;

  @ViewChild(WeanedChickEditComponent, { static: true })
  public weanedChickComponent: WeanedChickEditComponent;

  private subscription = new Subscription();

  private refreshLifeStagesArray = new Subject<string>();

  constructor(
    public optionsService: OptionsService,
    private birdService: BirdService
  ) {
    this.refreshLifeStagesArray
      .pipe(
        scan((acc: string[], value: string) => {
          acc.push(value);
          return acc;
        }, []),
        debounceTime(50)
      )
      .subscribe((birdIDs) => {
        this.birdService.getLifeStages(birdIDs).subscribe((lifestages) => {
          lifestages.forEach((lifestage) => {
            const i = this.toIndex(lifestage.birdID);
            this.myFormArray.controls[i]
              .get('ageClass')
              .setValue(lifestage.ageClass);
            this.myFormArray.controls[i]
              .get('milestone')
              .setValue(lifestage.milestone);
            this.myFormArray.controls[i]
              .get('mortality')
              .setValue(lifestage.mortality);
          });
        });
      });
  }

  ngOnInit() {}

  @Input()
  set myFormArray(array: FormArray) {
    this.subscription.unsubscribe();
    this._myFormArray = array;
    if (this._myFormArray) {
      // subscribe to the event where a new bird is in the array (by either being adding a new element or changing an existing one)
      this.subscription = this._myFormArray.valueChanges
        .pipe(
          // seed with the initial value of the array (as we may be initialised after the array already has values)
          startWith(this._myFormArray.getRawValue()),
          // map to bird id
          map((neList) => neList.map((ne) => ne.birdID)),
          // use pairwise to be able to compare previous with next value
          pairwise(),
          // see with the initial value of the array as the next value and previous value as an empty array
          // effect is new birds list determined to be all birds in list
          startWith([
            [],
            this._myFormArray.getRawValue().map((ne) => ne.birdID),
          ]),
          // map to new birds list (any birds not previously in the form array)
          map(([previousNes, nes]) =>
            nes.filter((ne) => previousNes.indexOf(ne) === -1)
          ),
          // filter out the empty birds list
          filter((newNe) => newNe.length > 0)
        )
        .subscribe((bList) => {
          bList.forEach((birdID) => {
            if (birdID) {
              this.refreshLifeStage(birdID);
            }
          });
        });
    }
  }

  get myFormArray() {
    return this._myFormArray;
  }

  refreshLifeStage(birdID: string) {
    if (birdID) {
      this.refreshLifeStagesArray.next(birdID);
      // const i = this.toIndex(birdID);
      // this.birdService.getAgeClass(birdID).subscribe(ageClass => this.myFormArray.controls[i].get('ageClass').setValue(ageClass));
      // this.birdService.getMilestone(birdID).subscribe(milestone => this.myFormArray.controls[i].get('milestone').setValue(milestone));
      // this.birdService.getMortality(birdID).subscribe(mortality => this.myFormArray.controls[i].get('mortality').setValue(mortality));
    }
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

  private toBirdID(i: number): string {
    return this.myFormArray.at(i).get('birdID').value;
  }

  private toIndex(birdID: string): number {
    return this.myFormArray.controls.findIndex(
      (c) => c.get('birdID').value === birdID
    );
  }

  showFledged(ageClass: string, milestone: string, mortality) {
    return (
      ageClass === 'Chick' && milestone === 'Hatched' && mortality === 'Alive'
    );
  }

  onFledged(i: number) {
    this.fledgedChickComponent.setValues(
      this.toBirdID(i),
      this.myFormGroup.controls.dateTime.value,
      (this.myFormGroup.controls
        .observerList as FormArray).getRawValue() as ObserverEntity[]
    );
    this.fledgedChickModal.show();
  }

  addItem() {
    // call addItem method added to FormArray instance by the FormService. See form.service.ts
    (this.myFormArray as any).addItem(new NestChickEntity());
  }

  removeItem(i: number) {
    this.myFormArray.removeAt(i);
  }

  onFledgedChickSaved(birdID: string) {
    this.fledgedChickModal.hide();
    if (birdID) {
      this.refreshLifeStage(birdID);
    }
  }

  onWeanedChickSaved(birdID: string) {
    this.weanedChickModal.hide();
    if (birdID) {
      this.refreshLifeStage(birdID);
    }
  }
}
