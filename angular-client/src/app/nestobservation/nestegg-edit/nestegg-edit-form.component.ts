import { Subscription, Subject } from 'rxjs';
import {
  startWith,
  pairwise,
  filter,
  scan,
  debounceTime,
  map,
} from 'rxjs/operators';

import { Input } from '@angular/core';
import { FormGroup, FormArray } from '@angular/forms';

import { ModalDirective } from 'ngx-bootstrap/modal';

import { ObserverEntity } from './../../domain/observer.entity';
import { BirdService } from '../../bird/bird.service';
import {
  Component,
  OnInit,
  ViewChild,
  Output,
  EventEmitter,
} from '@angular/core';
import { NestEggEntity } from '../../domain/nestegg.entity';
import { OptionsService } from '../../common/options.service';
import { FertileEggEditComponent } from '../fertileegg-edit/fertileegg-edit.component';
import { InfertileEggEditComponent } from '../infertileegg-edit/infertileegg-edit.component';
import { HatchEditComponent } from '../hatch-edit/hatch-edit.component';
import { DeadEmbryoEditComponent } from '../deadembryo-edit/deadembryo-edit.component';

@Component({
  selector: 'app-nestegg-edit-form',
  templateUrl: './nestegg-edit-form.component.html',
})
export class NestEggEditFormComponent implements OnInit {
  private _myFormArray: FormArray;

  @Output()
  eggHatched = new EventEmitter();

  @ViewChild('fertileEggModal', { static: true })
  public fertileEggModal: ModalDirective;

  @ViewChild(FertileEggEditComponent, { static: true })
  public fertileEggComponent: FertileEggEditComponent;

  @ViewChild('infertileEggModal', { static: true })
  public infertileEggModal: ModalDirective;

  @ViewChild(InfertileEggEditComponent, { static: true })
  public infertileEggComponent: InfertileEggEditComponent;

  @ViewChild('hatchModal', { static: true })
  public hatchModal: ModalDirective;

  @ViewChild(HatchEditComponent, { static: true })
  public hatchComponent: HatchEditComponent;

  @ViewChild('deadEmbryoModal', { static: true })
  public deadEmbryoModal: ModalDirective;

  @ViewChild(DeadEmbryoEditComponent, { static: true })
  public deadEmbryoComponent: DeadEmbryoEditComponent;

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
      // this.birdService.getLifeStage(birdID).subscribe(lifestage => {
      //   this.myFormArray.controls[i].get('ageClass').setValue(lifestage.ageClass);
      //   this.myFormArray.controls[i].get('milestone').setValue(lifestage.milestone);
      //   this.myFormArray.controls[i].get('mortality').setValue(lifestage.mortality);
      // });
      //this.birdService.getAgeClass(birdID).subscribe(ageClass => this.myFormArray.controls[i].get('ageClass').setValue(ageClass));
      //this.birdService.getMilestone(birdID).subscribe(milestone => this.myFormArray.controls[i].get('milestone').setValue(milestone));
      //this.birdService.getMortality(birdID).subscribe(mortality => this.myFormArray.controls[i].get('mortality').setValue(mortality));
    }
  }

  private toBirdID(i: number): string {
    return this.myFormArray.at(i).get('birdID').value;
  }

  private toIndex(birdID: string): number {
    return this.myFormArray.controls.findIndex(
      (c) => c.get('birdID').value === birdID
    );
  }

  showFertile(ageClass: string, milestone: string, mortality) {
    return (
      ageClass === 'Egg' && milestone === 'Laid' && mortality === 'Unknown'
    );
  }

  onFertile(i: number) {
    this.fertileEggComponent.setValues(this.toBirdID(i));
    this.fertileEggModal.show();
  }

  showInfertile(ageClass: string, milestone: string, mortality) {
    return (
      ageClass === 'Egg' && milestone === 'Laid' && mortality === 'Unknown'
    );
  }

  onInfertile(i: number) {
    this.infertileEggComponent.setValues(this.toBirdID(i));
    this.infertileEggModal.show();
  }

  showHatched(ageClass: string, milestone: string, mortality) {
    return (
      ageClass === 'Egg' && milestone === 'Laid' && mortality === 'Fertile'
    );
  }

  onHatched(i: number) {
    this.hatchComponent.setValues(this.toBirdID(i));
    this.hatchModal.show();
  }

  showBirdDied(ageClass: string, milestone: string, mortality) {
    return (
      ageClass === 'Egg' && milestone === 'Laid' && mortality === 'Fertile'
    );
  }

  onBirdDied(i: number) {
    this.deadEmbryoComponent.setValues(
      this.toBirdID(i),
      this.myFormGroup.controls.dateTime.value,
      (this.myFormGroup.controls
        .observerList as FormArray).getRawValue() as ObserverEntity[]
    );
    this.deadEmbryoModal.show();
  }

  addItem() {
    // call addItem method added to FormArray instance by the FormService. See form.service.ts
    (this.myFormArray as any).addItem(new NestEggEntity());
  }

  removeItem(i: number) {
    this.myFormArray.removeAt(i);
  }

  onFertileEggSaved(birdID: string) {
    this.fertileEggModal.hide();
    if (birdID) {
      this.refreshLifeStage(birdID);
    }
  }

  onInfertileEggSaved(birdID: string) {
    this.infertileEggModal.hide();
    if (birdID) {
      this.myFormArray.removeAt(this.toIndex(birdID));
    }
  }

  onHatchSaved(birdID: string) {
    this.hatchModal.hide();
    if (birdID) {
      this.myFormArray.removeAt(this.toIndex(birdID));
      this.eggHatched.emit(birdID);
    }
  }

  onDeadEmbryoSaved(birdID: string) {
    this.deadEmbryoModal.hide();
    if (birdID) {
      this.myFormArray.removeAt(this.toIndex(birdID));
    }
  }
}
