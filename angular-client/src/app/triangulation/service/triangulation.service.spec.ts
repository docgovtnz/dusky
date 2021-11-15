import { TestBed } from '@angular/core/testing';

import { TriangulationService } from './triangulation.service';
import { Point } from './point';
import * as Victor from 'Victor';
import { LocationBearingEntity } from '../../domain/locationbearing.entity';

const myFixedDecimal = (value: number): string => value.toFixed(3);

describe('TriangulationService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should create vector from two points', () => {
    //const service: TriangulationService = TestBed.get(TriangulationService);

    const pointA = new Point(1, 1);
    const pointB = new Point(1, 2);
    const v = TriangulationService.createVector(pointA, pointB);
    expect(v.magnitude()).toEqual(1);
  });

  it('should create vector from polar coordinates', () => {
    //const service: TriangulationService = TestBed.get(TriangulationService);

    // 53.130 degrees is the internal angle of a 3,4,5 triangle
    const radians = TriangulationService.radians(53.13);
    const v = TriangulationService.createVectorFromPolar(radians, 5);
    expect(Math.round(v.x)).toEqual(3);
    expect(Math.round(v.y)).toEqual(4);
  });

  it('should calculate the internal angle for two vectors', () => {
    //const service: TriangulationService = TestBed.get(TriangulationService);

    // 53.130 degrees is the internal angle of a 3,4,5 triangle
    const radians = TriangulationService.radians(53.13);

    {
      // 345 triangle up the page
      const vectorA = new Victor(3, 0);
      const vectorB = new Victor(3, 4);
      const angle = TriangulationService.calculateInternalAngle(
        vectorA,
        vectorB
      );
      expect(myFixedDecimal(angle)).toEqual(myFixedDecimal(radians));
    }

    {
      // 345 triangle down the page
      const vectorA = new Victor(3, 0);
      const vectorB = new Victor(3, -4);
      const angle = TriangulationService.calculateInternalAngle(
        vectorA,
        vectorB
      );
      expect(myFixedDecimal(angle)).toEqual(myFixedDecimal(radians * -1));
    }

    {
      // a bigger vector in the exact opposite direction
      const vectorA = new Victor(3, 0);
      const vectorB = new Victor(-30, 0);
      const angle = TriangulationService.calculateInternalAngle(
        vectorA,
        vectorB
      );
      expect(myFixedDecimal(angle)).toEqual(
        myFixedDecimal(TriangulationService.radians(180))
      );
    }
  });

  it('should convert geographic degrees into cartesian radians', () => {
    const northEast = TriangulationService.cartesianDegrees(45);
    expect(northEast).toEqual(45);

    const southEast = TriangulationService.cartesianDegrees(135);
    expect(southEast).toEqual(315);

    const southWest = TriangulationService.cartesianDegrees(225);
    expect(southWest).toEqual(225);

    const northWest = TriangulationService.cartesianDegrees(315);
    expect(northWest).toEqual(135);
  });

  it('should triangulate a 345 triangle', () => {
    const service: TriangulationService = TestBed.get(TriangulationService);

    const data = [
      { x1: 0, y1: 0, b1: 90 - 53.13, x2: 3, y2: 0, b2: 0, x3: 3, y3: 4 },
      { x1: 0, y1: 0, b1: 360 - 53.13, x2: 0, y2: 3, b2: 270, x3: -4, y3: 3 },
    ];

    data.forEach((d) => {
      const locationA = new LocationBearingEntity();
      locationA.easting = d.x1; // origin location
      locationA.northing = d.y1;
      locationA.compassBearing = d.b1; // compass bearing for a 345 triangle

      const locationB = new LocationBearingEntity();
      locationB.easting = d.x2; // out on the x-axis for the "3"
      locationB.northing = d.y2;
      locationB.compassBearing = d.b2; // due north

      const pointC = service.triangulate(locationA, locationB);

      console.log('PointC = (' + pointC.easting + ', ' + pointC.northing + ')');
      expect(myFixedDecimal(pointC.easting)).toEqual(myFixedDecimal(d.x3));
      expect(myFixedDecimal(pointC.northing)).toEqual(myFixedDecimal(d.y3));
    });
  });
});
