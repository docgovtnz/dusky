import {
  AfterViewInit,
  Component,
  Input,
  OnInit,
  ViewChild,
} from '@angular/core';

import { LocationBearingEntity } from '../../domain/locationbearing.entity';
import { MagneticVariationService } from '../../triangulation/service/magnetic-variation.service';
import { EsriMapComponent, LocationPoint } from '../../map/esri-map.component';
import { RecordEntity } from '../../domain/record.entity';

@Component({
  selector: 'app-locationbearing-panel',
  templateUrl: './locationbearing-panel.component.html',
})
export class LocationBearingPanelComponent implements OnInit, AfterViewInit {
  @Input()
  locationBearingList: LocationBearingEntity[];

  @Input()
  record: RecordEntity;

  locationPoint: LocationPoint;

  adjustedLocationBearingList: LocationBearingEntity[];

  @ViewChild(EsriMapComponent, { static: true })
  mapComponent: EsriMapComponent;

  constructor() {}

  ngOnInit(): void {
    this.locationPoint = new LocationPoint(
      this.record.easting,
      this.record.northing
    );
  }

  ngAfterViewInit(): void {
    this.mapComponent.mapLoadedEvent.subscribe(() => {
      this.adjustedLocationBearingList = MagneticVariationService.applyMagneticVariation(
        this.locationBearingList,
        this.record.magneticDeclination
      );
    });
  }
}
