import { EsriMapComponent } from '../../map/esri-map.component';
import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { LocationService } from '../location.service';
import { LocationEntity } from '../../domain/location.entity';
import { OptionsService } from '../../common/options.service';

import { combineLatest } from 'rxjs/index';
import { ValidationMessage } from '../../domain/response/validation-message';
import { FormGroup } from '@angular/forms';
import { FormService } from '../../form/form.service';
import { BaseEntityFormFactory } from './../../form/base-entity-form-factory';
import { NestDetailsEntity } from '../../domain/nestdetails.entity';

@Component({
  selector: 'app-location-edit',
  templateUrl: 'location-edit.component.html',
})
export class LocationEditComponent implements OnInit {
  // New Form layout

  mappedLocation: LocationEntity;
  private _locationEntity: LocationEntity;
  private formFactory: BaseEntityFormFactory;
  messages: ValidationMessage[];

  myFormGroup: FormGroup;

  _mapComponent: EsriMapComponent;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public optionsService: OptionsService,
    private formService: FormService,
    private service: LocationService
  ) {}

  ngOnInit(): void {
    // Take the latest value from each of the supplied observables and execute the subscriber
    // function. Note: the subscribe method isn't called until all observables have issued at
    // least one value. This subscription will keep being fired when there are more values,
    // however the createFormFactory() observable should only issue one value.
    combineLatest(
      this.route.params,
      this.route.queryParams,
      this.formService.createFormFactory('LocationEntity')
    ).subscribe(([params, queryParams, formFactory]) => {
      this.formFactory = formFactory;
      if (queryParams.newEntity) {
        this.newEntity();
      } else {
        this.loadEntity(params.id);
      }
    });
  }

  get locationEntity(): LocationEntity {
    return this._locationEntity;
  }

  set locationEntity(value: LocationEntity) {
    this._locationEntity = value;
    this.showInMap();
    if (this.formFactory) {
      this.myFormGroup = this.formFactory.createForm(value);

      // TODO this next bit need a proper refactor
      // add or remove nest details based on location type
      // have in effect before patching
      this.myFormGroup.controls['locationType'].valueChanges.subscribe(
        (locationType) => {
          if (this.isNestType(locationType)) {
            this.addNestDetails();
          } else {
            this.removeNestDetails();
          }
        }
      );

      this.myFormGroup.patchValue(this._locationEntity);

      // sync locationEntity object with details needed for dynamically updating the map component
      const c = this.myFormGroup.controls;
      c.locationName.valueChanges.subscribe((locationName) => {
        this._locationEntity.locationName = locationName;
        this.showInMap();
      });
      c.easting.valueChanges.subscribe((easting) => {
        this._locationEntity.easting = easting;
        this.showInMap();
      });
      c.northing.valueChanges.subscribe((northing) => {
        this._locationEntity.northing = northing;
        this.showInMap();
      });
      c.island.valueChanges.subscribe((island) => {
        this._locationEntity.island = island;
        this.showInMap();
      });
    }
  }

  showInMap() {
    if (this._mapComponent) {
      if (this._locationEntity) {
        this._mapComponent.selectedIslandName = this._locationEntity.island;
      }
      this._mapComponent.selectedLocation = this._locationEntity;
    }
  }

  newEntity() {
    const e = new LocationEntity();
    // set value after init so that form gets patched correctly
    e.active = true;
    this.locationEntity = e;
  }

  loadEntity(id: string) {
    this.service.findById(id).subscribe((entity) => {
      this.locationEntity = entity;
    });
  }

  onSave(): void {
    this.messages = null;
    Object.assign(this.locationEntity, this.myFormGroup.getRawValue());
    this.service.save(this.locationEntity).subscribe((response) => {
      this._locationEntity = response.model;
      this.messages = response.messages;
      if (this.messages.length === 0) {
        this.onCancel();
      }
    });
  }

  onCancel(): void {
    if (this.locationEntity.id) {
      this.router.navigate(['/location/' + this.locationEntity.id]);
    } else {
      this.router.navigate(['/location']);
    }
  }

  private isNestType(locationType: string): boolean {
    return locationType === 'Nest';
  }

  get hasNestDetails(): boolean {
    if (this.myFormGroup && this.myFormGroup.controls['nestDetails']) {
      return true;
    } else {
      return false;
    }
  }

  onRemoveNestDetails() {
    this.removeNestDetails();
  }

  removeNestDetails() {
    if (this.hasNestDetails) {
      (this.myFormGroup as any).nestDetailsRemove();
      this.locationEntity.nestDetails = null;
    }
  }

  private addNestDetails() {
    if (!this.hasNestDetails) {
      (this.myFormGroup as any).nestDetailsAdd(new NestDetailsEntity());
    }
  }

  @ViewChild(EsriMapComponent, { static: true })
  set mapComponent(mapComponent: EsriMapComponent) {
    this._mapComponent = mapComponent;
    this.showInMap();
  }
}
