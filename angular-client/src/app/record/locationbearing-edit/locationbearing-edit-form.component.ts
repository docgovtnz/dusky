import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { LocationBearingEntity } from '../../domain/locationbearing.entity';
import { OptionsService } from '../../common/options.service';
import { FormArray, FormControl, FormGroup } from '@angular/forms';
import { switchMap, tap } from 'rxjs/operators';
import { empty } from 'rxjs';
import { LocationService } from '../../location/location.service';
import { TriangulationService } from '../../triangulation/service/triangulation.service';
import { LocationPoint } from '../../triangulation/service/location-point';
import { MagneticVariationService } from '../../triangulation/service/magnetic-variation.service';
import { EsriMapComponent } from '../../map/esri-map.component';

@Component({
  selector: 'app-locationbearing-edit-form',
  templateUrl: './locationbearing-edit-form.component.html',
  styleUrls: ['./locationbearing-edit-form.component.scss'],
})
export class LocationBearingEditFormComponent implements OnInit {
  private _myFormArray: FormArray;

  @Input()
  eastingFormControl: FormControl;

  @Input()
  northingFormControl: FormControl;

  @Input()
  islandNameFormControl: FormControl;

  @Input()
  magneticDeclinationFormControl: FormControl;

  selectedIslandName: string;

  locationBearingList: LocationBearingEntity[];
  triangulationPoint: LocationPoint;

  magneticDeclination: number = null;
  adjustedLocationBearingList: LocationBearingEntity[];

  @ViewChild(EsriMapComponent, { static: true })
  mapComponent: EsriMapComponent;
  mapLoaded = false;

  constructor(
    public optionsService: OptionsService,
    private triangulationService: TriangulationService,
    private locationService: LocationService
  ) {}

  ngOnInit() {
    // Just some test code
    //this.addLocation('b0126c66-b4dd-49c6-9ef2-a99a67606582', 180);
    //this.addLocation('8bd39f18-4556-4a0b-b654-ae30aa3504a2', 310);
    //this.addLocation('0ddf161f-ae0e-4aa2-ae23-f6274a506efc', 110);

    this.selectedIslandName = this.islandNameFormControl.value;
    this.islandNameFormControl.valueChanges.subscribe((islandName) => {
      this.selectedIslandName = islandName;
    });

    this.magneticDeclination = this.magneticDeclinationFormControl.value;

    this.magneticDeclinationChanges();
    this.syncModel();
  }

  magneticDeclinationChanges(): void {
    this.magneticDeclinationFormControl.valueChanges.subscribe(
      (magneticDeclination) => {
        this.magneticDeclination = magneticDeclination;
        this.syncModel();
      }
    );
  }

  private addLocation(locationID: string, compassBearing: number) {
    const formGroup = this.addItem();
    formGroup.controls['locationID'].setValue(locationID);
    formGroup.controls['compassBearing'].setValue(compassBearing);
  }

  get myFormGroup(): FormGroup {
    // return the form group that contains the provided FormArray so that the FormArray bound to the root div element
    // in the template using the FormArrayName directive (there is no FormArrayDirective)
    return this._myFormArray.parent as FormGroup;
  }

  get myFormArrayName(): string {
    // determine the name given to the provided FormArray so that it can be bound to the root div template using the
    // FormArrayName directive (there is no FormArrayDirective)
    return Object.keys(this.myFormGroup.controls).find(
      (name) => this.myFormGroup.get(name) === this._myFormArray
    );
  }

  get myFormArray(): FormArray {
    return this._myFormArray;
  }

  @Input()
  set myFormArray(value: FormArray) {
    this._myFormArray = value;
    if (this._myFormArray) {
      this._myFormArray.controls.forEach((formGroup: any) => {
        Object.keys(formGroup.controls).forEach((key) => {
          formGroup.get(key).valueChanges.subscribe(() => this.syncModel());
        });
      });
    }
  }

  addItem(): FormGroup {
    // call addItem method added to FormArray instance by the FormService. See form.service.ts
    const added: FormGroup = (this._myFormArray as any).addItem(
      new LocationBearingEntity()
    );

    Object.keys(added.controls).forEach((key) => {
      added.get(key).valueChanges.subscribe(() => this.syncModel());
    });

    // populate the northing and easting from the selected location
    const eastingControl = added.controls['easting'];
    const northingControl = added.controls['northing'];
    added.controls['locationID'].valueChanges
      .pipe(
        tap(() => {
          eastingControl.setValue(null);
          northingControl.setValue(null);
        }),
        switchMap((locationID: string) =>
          locationID ? this.locationService.findById(locationID) : empty()
        )
      )
      .subscribe((response) => {
        eastingControl.setValue(response.easting);
        northingControl.setValue(response.northing);
      });

    added.get('active').setValue(true);

    return added;
  }

  syncModel() {
    // This timeout needed so that calculation is done after the model change has been applied (i.e. with the "new" value)
    setTimeout(() => {
      this.applyMagneticDeclination();
      this.triangulate();
    }, 1);
  }

  private applyMagneticDeclination() {
    this.locationBearingList = this.myFormGroup.get(
      'locationBearingList'
    ).value;
    this.magneticDeclination = this.myFormGroup.get(
      'magneticDeclination'
    ).value;
    console.log(
      '>>>>> locationbearing  applyMagneticDeclination = ' +
        this.magneticDeclination
    );
    this.adjustedLocationBearingList = MagneticVariationService.applyMagneticVariation(
      this.locationBearingList,
      this.magneticDeclination
    );
  }

  private triangulate() {
    this.triangulationPoint = this.triangulationService.triangulateBearingList(
      this.adjustedLocationBearingList
    );
    if (this.triangulationPoint) {
      const easting = this.triangulationPoint.easting
        ? this.triangulationPoint.easting.toFixed(1)
        : null;
      const northing = this.triangulationPoint.northing
        ? this.triangulationPoint.northing.toFixed(1)
        : null;
      this.eastingFormControl.setValue(easting);
      this.northingFormControl.setValue(northing);
    }
  }

  removeItem(i: number) {
    this._myFormArray.removeAt(i);
  }

  getLetterIndex(i: number): string {
    return String('abcdefghijklmnopqrstuvwxyz'.charAt(i));
  }
}
