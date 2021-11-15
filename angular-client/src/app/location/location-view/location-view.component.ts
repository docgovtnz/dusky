import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { LocationEntity } from '../../domain/location.entity';
import { LocationService } from '../location.service';
import { DeleteByIdCheckDto } from '../../domain/response/delete-by-id-check-dto';
import { EsriMapComponent } from 'src/app/map/esri-map.component';

@Component({
  selector: 'app-location-view',
  templateUrl: 'location-view.component.html',
})
export class LocationViewComponent implements OnInit {
  isCollapsed = false;

  locationEntity: LocationEntity;
  deleteCheck: DeleteByIdCheckDto;

  // the current revision displayed (may be the current entity or an older revision)
  revision: LocationEntity;

  _mapComponent: EsriMapComponent;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: LocationService
  ) {}

  ngOnInit(): void {
    this.service.findById(this.route.snapshot.params.id).subscribe((entity) => {
      this.locationEntity = entity;
      // start by using the current location as the current revision
      this.revision = entity;
      this.loadDeleteCheck();

      this.refreshMap();
    });
  }

  loadDeleteCheck() {
    this.service
      .deleteByIdCheck(this.locationEntity.id)
      .subscribe((deleteCheck) => (this.deleteCheck = deleteCheck));
  }

  isDeleteOk() {
    return this.deleteCheck && this.deleteCheck.deleteOk;
  }

  onEdit() {
    this.router.navigate(['/location/edit/' + this.locationEntity.id]);
  }

  onDelete() {
    this.service.delete(this.locationEntity.id).subscribe(() => {
      this.router.navigate(['/location']);
    });
  }

  onRevisionEvent(revision) {
    this.revision = revision;
  }

  refreshMap() {
    if (this._mapComponent) {
      if (this.locationEntity) {
        this._mapComponent.selectedIslandName = this.locationEntity.island;
        this._mapComponent.selectedLocation = this.locationEntity;
      } else {
        this._mapComponent.selectedIslandName = null;
        this._mapComponent.selectedRecord = null;
      }
    }
  }

  @ViewChild(EsriMapComponent)
  set mapComponent(mapComponent: EsriMapComponent) {
    this._mapComponent = mapComponent;
    this.refreshMap();
  }
}
