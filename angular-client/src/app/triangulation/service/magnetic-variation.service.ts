import { Injectable } from '@angular/core';
import { LocationBearingEntity } from '../../domain/locationbearing.entity';

@Injectable({
  providedIn: 'root',
})
export class MagneticVariationService {
  constructor() {}

  static applyMagneticVariation(
    locationBearingList: LocationBearingEntity[],
    magneticVariation: number
  ): LocationBearingEntity[] {
    const list = [];
    locationBearingList.forEach((locationBearing) => {
      const adjustedBearing = Object.assign(
        new LocationBearingEntity(),
        locationBearing
      );
      if (
        adjustedBearing.compassBearing !== undefined &&
        adjustedBearing.compassBearing !== null
      ) {
        adjustedBearing.compassBearing =
          Number(adjustedBearing.compassBearing) + magneticVariation;
      }
      list.push(adjustedBearing);
    });

    return list;
  }
}
