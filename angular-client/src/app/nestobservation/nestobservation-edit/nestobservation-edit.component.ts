import { empty } from 'rxjs';
import { combineLatest } from 'rxjs/index';
import { switchMap, tap, filter, catchError, take, map } from 'rxjs/operators';

import { Component, OnInit, ViewChild } from '@angular/core';
import { FormGroup, FormArray } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ModalDirective } from 'ngx-bootstrap/modal';

import { LocationEntity } from './../../domain/location.entity';
import { NestObservationService } from '../nestobservation.service';
import { NestObservationEntity } from '../../domain/nestobservation.entity';
import { OptionsService } from '../../common/options.service';
import { ValidationMessage } from '../../domain/response/validation-message';
import { FormService } from '../../form/form.service';
import { BaseEntityFormFactory } from './../../form/base-entity-form-factory';
import { ObserverEntity } from '../../domain/observer.entity';
import { NestEggEntity } from '../../domain/nestegg.entity';
import { NewEggEditComponent } from '../newegg-edit/newegg-edit.component';
import { NestChickEntity } from '../../domain/nestchick.entity';
import { LocationService } from '../../location/location.service';
import { ObservationTimesEntity } from '../../domain/observationtimes.entity';
import { NestChamberEntity } from '../../domain/nestchamber.entity';
import { RecordEntity } from '../../domain/record.entity';

@Component({
  selector: 'app-nestobservation-edit',
  templateUrl: 'nestobservation-edit.component.html',
})
export class NestObservationEditComponent implements OnInit {
  // New Form layout

  private _nestobservationEntity: NestObservationEntity;
  private formFactory: BaseEntityFormFactory;
  messages: ValidationMessage[];

  eggRecords: RecordEntity[];
  chickRecords: RecordEntity[];

  myFormGroup: FormGroup;

  nestSelected = false;

  @ViewChild('newEggModal', { static: true })
  public newEggModal: ModalDirective;

  @ViewChild(NewEggEditComponent, { static: true })
  private newEggComponent: NewEggEditComponent;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public optionsService: OptionsService,
    private formService: FormService,
    private service: NestObservationService,
    private locationService: LocationService
  ) {}

  ngOnInit(): void {
    // Take the latest value from each of the supplied observables and execute the subscriber
    // function. Note: the subscribe method isn't called until all observables have issued at
    // least one value. This subscription will keep being fired when there are more values,
    // however the createFormFactory() observable should only issue one value.
    combineLatest(
      this.route.params,
      this.route.queryParams,
      this.formService.createFormFactory('NestObservationEntity')
    ).subscribe(([params, queryParams, formFactory]) => {
      this.formFactory = formFactory;
      if (queryParams.newEntity) {
        this.newEntity();
      } else {
        this.loadEntity(params.id);
      }
    });
  }

  get nestobservationEntity(): NestObservationEntity {
    return this._nestobservationEntity;
  }

  set nestobservationEntity(value: NestObservationEntity) {
    // we don't yet know if the new nest observation is for a nest, so set nestSelected to false
    this.nestSelected = false;
    this._nestobservationEntity = value;
    if (this.formFactory) {
      this.myFormGroup = this.formFactory.createForm(value);

      // setup so that nestSelected is set based on the selected location
      // do this before setting values on form (patchValue below) so that the new location value affects nestSelected
      this.myFormGroup.controls.locationID.valueChanges
        .pipe(
          tap(() => (this.nestSelected = false)),
          filter((locationID) => locationID !== null),
          switchMap((locationID) => this.locationService.findById(locationID)),
          catchError(() => empty()),
          map((location: LocationEntity) => location.locationType === 'Nest')
        )
        .subscribe((isNest: boolean) => (this.nestSelected = isNest));

      this.myFormGroup.patchValue(this._nestobservationEntity);

      // We do not want this logic when loading an existing record.
      if (!value.id) {
        this.myFormGroup.controls.locationID.valueChanges
          .pipe(
            // reset the eggs on the form
            tap(() => this.clearEggs()),
            filter((locationID) => locationID !== null),
            switchMap((locationID) =>
              this.locationService.getCurrentEggs(locationID)
            ),
            catchError(() => empty())
          )
          .subscribe((birds) => {
            birds.forEach((b) => this.addEgg(b));
          });
        this.myFormGroup.controls.locationID.valueChanges
          .pipe(
            // reset the chicks on the form
            tap(() => this.clearChicks()),
            filter((locationID) => locationID !== null),
            switchMap((locationID) =>
              this.locationService.getCurrentChicks(locationID)
            ),
            catchError(() => empty())
          )
          .subscribe((birds) => {
            birds.forEach((b) => this.addChick(b));
          });
      }
    }
  }

  newEntity() {
    const e: NestObservationEntity = new NestObservationEntity();
    e.observationTimes = new ObservationTimesEntity();
    e.nestChamber = new NestChamberEntity();
    e.observerList = [new ObserverEntity()];
    e.motherTripList = [];
    e.nestEggList = [];
    e.nestChickList = [];
    // set value after init so that form gets patched correctly
    this.nestobservationEntity = e;
  }

  loadEntity(id: string) {
    this.service.findById(id).subscribe((entity) => {
      this.nestobservationEntity = entity;

      if (
        this.nestobservationEntity.eggRecordReferenceList &&
        this.nestobservationEntity.eggRecordReferenceList.length > 0
      ) {
        this.service.getEggRecords(entity.id).subscribe((eggs) => {
          this.eggRecords = eggs;
        });
      }
      if (
        this.nestobservationEntity.chickRecordReferenceList &&
        this.nestobservationEntity.chickRecordReferenceList.length > 0
      ) {
        this.service.getChickRecords(entity.id).subscribe((chicks) => {
          this.chickRecords = chicks;
        });
      }
    });
  }

  onSave(): void {
    this.messages = null;

    const oldEggs = this.nestobservationEntity.nestEggList;
    const oldChicks = this.nestobservationEntity.nestChickList;

    Object.assign(this.nestobservationEntity, this.myFormGroup.getRawValue());

    // Reassign the record IDs.
    this.nestobservationEntity.nestEggList.forEach((nestEgg) => {
      const relatedOldEgg = oldEggs.filter(
        (oldEgg) => oldEgg.birdID === nestEgg.birdID
      );
      if (relatedOldEgg && relatedOldEgg.length === 1) {
        nestEgg.recordID = relatedOldEgg[0].recordID;
      }
    });

    // Reassign the record IDs.
    this.nestobservationEntity.nestChickList.forEach((nestChick) => {
      const relatedOldChick = oldChicks.filter(
        (oldChick) => oldChick.birdID === nestChick.birdID
      );
      if (relatedOldChick && relatedOldChick.length === 1) {
        nestChick.recordID = relatedOldChick[0].recordID;
      }
    });

    this.service.save(this.nestobservationEntity).subscribe((response) => {
      this._nestobservationEntity = response.model;
      this.messages = response.messages;
      if (this.messages.length === 0) {
        this.onCancel();
      }
    });
  }

  onCancel(): void {
    if (this.nestobservationEntity.id) {
      this.router.navigate([
        '/nestobservation/' + this.nestobservationEntity.id,
      ]);
    } else {
      this.router.navigate(['/nestobservation']);
    }
  }

  onAddEgg(): void {
    const locationID = this.myFormGroup.controls.locationID.value;
    this.locationService.getNextClutchOrder(locationID).subscribe(() => {
      this.newEggComponent.setValues(
        this.myFormGroup.controls.birdID.value,
        locationID,
        this.myFormGroup.controls.dateTime.value
      );
      this.newEggModal.show();
    });
  }

  onEggSaved(birdID: string): void {
    console.log('Saving egg');
    this.newEggModal.hide();
    this.addEgg(birdID);
  }

  addEgg(birdID): void {
    if (birdID) {
      const nestE = new NestEggEntity();
      nestE.birdID = birdID;
      const fg: FormGroup = (this.myFormGroup.controls
        .nestEggList as any).addItem();
      fg.patchValue(nestE);
      //this.sortEggs();
    }
  }

  sortEggs(): void {
    // We need the bird summaries to get the names to sort the eggs by. This has a  bit of a risk as it
    // may take some time to load the summaries. And then the list of eggs will re-order underneath the user. This
    // affects eggs more than chicks because the name is new and so the list must be reloaded.
    this.optionsService
      .getBirdSummaries()
      .pipe(take(1))
      .subscribe((birdSummaries) => {
        const x = (this.myFormGroup.controls
          .nestEggList as FormArray).controls.sort((a, b) => {
          const aName = birdSummaries.find(
            (bs) => bs.id === a.get('birdID').value
          ).birdName;
          const bName = birdSummaries.find(
            (bs) => bs.id === b.get('birdID').value
          ).birdName;
          return aName.localeCompare(bName);
        });

        this.myFormGroup.setControl('nestEggList', new FormArray(x));
      });
  }

  sortChicks(): void {
    // We need the bird summaries to get the names to sort the chicks by. This has a  bit of a risk as it
    // may take some time to load the summaries. And then the list of eggs will re-order underneath the user.
    this.optionsService
      .getBirdSummaries()
      .pipe(take(1))
      .subscribe((birdSummaries) => {
        const x = (this.myFormGroup.controls
          .nestChildList as FormArray).controls.sort((a, b) => {
          const aName = birdSummaries.find(
            (bs) => bs.id === a.get('birdID').value
          ).birdName;
          const bName = birdSummaries.find(
            (bs) => bs.id === b.get('birdID').value
          ).birdName;
          return aName.localeCompare(bName);
        });

        this.myFormGroup.setControl('nestChickList', new FormArray(x));
      });
  }

  showEggs(): boolean {
    return (
      this.myFormGroup &&
      this.myFormGroup.controls.nestEggList &&
      (this.myFormGroup.controls.nestEggList as FormArray).length > 0
    );
  }

  clearEggs(): void {
    if (this.myFormGroup) {
      const fa = this.myFormGroup.controls.nestEggList as FormArray;
      const length = fa.length;
      for (let i = 0; i < length; i++) {
        fa.removeAt(0);
      }
    }
  }

  onEggHatched(birdID: string): void {
    if (birdID) {
      this.addChick(birdID);
    }
  }

  addChick(birdID): void {
    if (birdID) {
      const nestE = new NestChickEntity();
      nestE.birdID = birdID;
      const fg: FormGroup = (this.myFormGroup.controls
        .nestChickList as any).addItem();
      fg.patchValue(nestE);
      //this.sortChicks();
    }
  }

  showChicks(): boolean {
    return (
      this.myFormGroup &&
      this.myFormGroup.controls.nestChickList &&
      (this.myFormGroup.controls.nestChickList as FormArray).length > 0
    );
  }

  clearChicks(): void {
    if (this.myFormGroup) {
      const fa = this.myFormGroup.controls.nestChickList as FormArray;
      const length = fa.length;
      for (let i = 0; i < length; i++) {
        fa.removeAt(0);
      }
    }
  }
}
