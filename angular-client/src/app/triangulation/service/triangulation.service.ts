import { Injectable } from '@angular/core';
import { Point } from './point';
import * as Victor from 'victor';
import { LocationBearingEntity } from '../../domain/locationbearing.entity';
import { LocationPoint } from './location-point';

/**
 *
 * See also: https://www.doc.govt.nz/globalassets/documents/science-and-technical/sops/ground-based-radio-tracking-protocol.pdf
 */
@Injectable({
  providedIn: 'root',
})
export class TriangulationService {
  constructor() {}

  triangulateBearingList(
    locationBearingList: LocationBearingEntity[]
  ): LocationPoint {
    const points: LocationPoint[] = [];
    for (let i = 0; i < locationBearingList.length; i++) {
      for (let j = i + 1; j < locationBearingList.length; j++) {
        // i has been incremented by 1 by the time we get here and that's what we want
        const b1 = locationBearingList[i];
        const b2 = locationBearingList[j];
        if (
          b1 &&
          b2 &&
          b1.active &&
          b2.active &&
          b1.easting &&
          b1.northing &&
          b2.easting &&
          b2.northing
        ) {
          //console.log('triangulating: ' + i + ', ' + j + ' = ' + JSON.stringify(b1) + ', ' + JSON.stringify(b2));
          // given any two locations(b1, b2), ONLY triangulate if two line segments intersect on
          // their compass bearing direction.
          if (TriangulationService.intersectOnCompassBearing(b1, b2)) {
            points.push(this.triangulate(b1, b2));
          }
        }
      }
    }

    return this.averagePointList(points);
  }

  /**
   * Given two points with compass bearing (direction) and check whether the two line segments
   * intersect.
   * In this case, the line segment is generated from the given point with auxiliary line
   * on the given direction.
   */
  private static intersectOnCompassBearing(
    locationA: LocationBearingEntity,
    locationB: LocationBearingEntity
  ) {
    // initiate the given points (A, B)
    const pointA = new Point(locationA.easting, locationA.northing);
    const pointB = new Point(locationB.easting, locationB.northing);

    // calculate the vectors based on the compass bearing with a long distance. It enables the
    // auxiliary lines on the direction from the initial points.
    // e.g. line segment AP - vectorAP takes point A and extends a distance with 4000 to point P.
    const vectorAP = this.createVectorFromCompassBearing(
      locationA.compassBearing,
      4000
    );
    const vectorBQ = this.createVectorFromCompassBearing(
      locationB.compassBearing,
      4000
    );

    // Calculate point P and point Q based on the initial points (A,B) and the vectors
    const locationPointP = new LocationPoint(
      pointA.x + vectorAP.x,
      pointA.y + vectorAP.y
    );
    const locationPointQ = new LocationPoint(
      pointB.x + vectorBQ.x,
      pointB.y + vectorBQ.y
    );
    const pointP = new Point(locationPointP.easting, locationPointP.northing);
    const pointQ = new Point(locationPointQ.easting, locationPointQ.northing);

    // check if AP and BQ intersect
    return this.checkIntersection(pointA, pointP, pointB, pointQ);
  }

  /**
   * Check if line segment AP and BQ intersect
   * see also: https://newbedev.com/test-if-two-lines-intersect-javascript-function
   */
  private static checkIntersection(
    pointA: Point,
    pointP: Point,
    pointB: Point,
    pointQ: Point
  ) {
    // Find the four orientations needed for general and special cases
    const o1 = this.orientation(pointA, pointP, pointB);
    const o2 = this.orientation(pointA, pointP, pointQ);
    const o3 = this.orientation(pointB, pointQ, pointA);
    const o4 = this.orientation(pointB, pointQ, pointP);

    // AP and BQ intersect on the following condition
    // (A, P, B) and (A, P, Q) have different orientations AND:
    // (B, Q, A) and (B, Q, P) have different orientations
    if (o1 !== o2 && o3 !== o4) {
      return true;
    }

    // A, P and B are collinear and B lies on segment AP
    if (o1 === 0 && this.isOnSegment(pointA, pointB, pointP)) {
      return true;
    }

    // A, P and Q are collinear and Q lies on segment AP
    if (o2 === 0 && this.isOnSegment(pointA, pointQ, pointP)) {
      return true;
    }

    // B, Q and A are collinear and A lies on segment BQ
    if (o3 === 0 && this.isOnSegment(pointB, pointA, pointQ)) {
      return true;
    }

    // B, Q and P are collinear and P lies on segment BQ
    if (o4 === 0 && this.isOnSegment(pointB, pointP, pointQ)) {
      return true;
    }

    return false;
  }

  /**
   * Given three points and determine the orientation.
   * https://www.geeksforgeeks.org/orientation-3-ordered-points/amp/
   *  0: collinear
   *  1: clockwise
   *  2: counterclockwise
   */
  private static orientation(p: Point, q: Point, r: Point) {
    // use cross product to check if two vectors are collinear
    // see also: https://en.wikipedia.org/wiki/Cross_product
    const determinant = (r.x - q.x) * (q.y - p.y) - (q.x - p.x) * (r.y - q.y);
    if (determinant === 0) {
      return 0;
    } else {
      return determinant > 0 ? 1 : 2;
    }
  }

  /**
   * Given three points and checks if point q lies on line segment pr
   */
  private static isOnSegment(p: Point, q: Point, r: Point) {
    return (
      q.x <= Math.max(p.x, r.x) &&
      q.x >= Math.min(p.x, r.x) &&
      q.y <= Math.max(p.y, r.y) &&
      q.y >= Math.min(p.y, r.y)
    );
  }

  private averagePointList(pointList: LocationPoint[]): LocationPoint {
    let locationPoint = null;
    if (pointList && pointList.length > 0) {
      let eastingTotal = 0;
      let northingTotal = 0;

      pointList.forEach((point) => {
        eastingTotal += point.easting;
        northingTotal += point.northing;
      });

      locationPoint = new LocationPoint(
        eastingTotal / pointList.length,
        northingTotal / pointList.length
      );
    }

    return locationPoint;
  }

  /**
   * From two known locations each with a bearing to the target calculate the target location.
   */
  triangulate(
    locationA: LocationBearingEntity,
    locationB: LocationBearingEntity
  ): LocationPoint {
    const pointA = new Point(locationA.easting, locationA.northing);
    const pointB = new Point(locationB.easting, locationB.northing);

    // calculate distance from A to B, (distanceAB)
    const distanceAB = TriangulationService.createVector(
      pointA,
      pointB
    ).magnitude();

    // find the vector of A to B (vectorAB), and B to A
    const vectorAB = TriangulationService.createVector(pointA, pointB);
    const vectorBA = TriangulationService.createVector(pointB, pointA);

    // convert the compass bearing into a cartesian angle, and also convert it into radians
    const angleAC = TriangulationService.radians(
      TriangulationService.cartesianDegrees(locationA.compassBearing)
    );
    const angleBC = TriangulationService.radians(
      TriangulationService.cartesianDegrees(locationB.compassBearing)
    );

    // We don't know what the vectorAC is because we don't know the length yet. But we can use the unit vector which
    // has a length of 1 to determine angles to other vectors.
    const unitVectorAC = TriangulationService.createVectorFromPolar(angleAC, 1);
    const unitVectorBC = TriangulationService.createVectorFromPolar(angleBC, 1);

    // calculate the internal angle of A's bearing to vectorAB  (alpha)
    // calculate the internal angle of B's bearing to vectorBA  (beta)
    const alpha = TriangulationService.calculateInternalAngle(
      unitVectorAC,
      vectorAB
    );
    const beta =
      Math.PI -
      TriangulationService.calculateInternalAngle(unitVectorBC, vectorBA);

    // d is the length of the "opposite" side of each triangle
    // The triangulation formula is just one of those derived formulas: https://en.wikipedia.org/wiki/Triangulation_(surveying)
    // d = distanceAB * (sin(alpha) * sin(beta) / sin(alpha + beta))
    const d =
      distanceAB *
      ((Math.sin(alpha) * Math.sin(beta)) / Math.sin(alpha + beta));

    // h is the hypotenuse of the triangle formed by d
    // since both vectors point to the same location we only need to find one hypotenuse

    // Now that we have the length of d finding the vectorAC is a case of using boring old high school SOHCAHTOA to find
    // the hypotenuse of the triangle. Sin(alpha) = O/H so h = d / Sin(alpha)
    const h = d / Math.sin(alpha);

    // now that we know the angle of the vectorAC and the magnitude we can now work out the components of the vector
    const vectorAC = TriangulationService.createVectorFromPolar(angleAC, h);

    // now that we know the vectorAC the pointC = pointA + vectorAC
    const pointC = new LocationPoint(
      pointA.x + vectorAC.x,
      pointA.y + vectorAC.y
    );
    return pointC;
  }

  static createVector(pointA: Point, pointB: Point): Victor {
    const dx = pointB.x - pointA.x;
    const dy = pointB.y - pointA.y;
    return new Victor(dx, dy);
  }

  static createVectorFromPolar(angle: number, distance: number): Victor {
    const x = Math.cos(angle) * distance;
    const y = Math.sin(angle) * distance;
    return new Victor(x, y);
  }

  static createVectorFromCompassBearing(
    compassBearing: number,
    distance: number
  ): Victor {
    const angle = this.radians(this.cartesianDegrees(compassBearing));

    const x = Math.cos(angle) * distance;
    const y = Math.sin(angle) * distance;
    return new Victor(x, y);
  }

  /**
   * The reference for this formula is: https://community.esri.com/thread/114605
   * Apparently it also works exactly the same for cartesianDegrees into geographicDegrees
   *
   * @param geographicDegrees
   */
  static cartesianDegrees(geographicDegrees: number): number {
    // the LocationBearing contains the bearing from true north in degrees,
    // we need to convert this into degrees from the x axis
    const cartesianDegrees = (450 - geographicDegrees) % 360;
    return cartesianDegrees;
  }

  static radians(degrees: number): number {
    return degrees * (Math.PI / 180);
  }

  static degrees(radians: number): number {
    return radians * (180 / Math.PI);
  }

  /**
   * https://stackoverflow.com/questions/21483999/using-atan2-to-find-angle-between-two-vectors
   */
  static calculateInternalAngle(vectorA: Victor, vectorB: Victor): number {
    // Yes - this looks wrong with having the y values as the first value, but that's how the atan2() function expects things
    let a = Math.atan2(vectorB.y, vectorB.x) - Math.atan2(vectorA.y, vectorA.x);

    if (a > Math.PI) {
      a -= 2 * Math.PI;
    } else if (a <= -Math.PI) {
      a += 2 * Math.PI;
    }
    return a;
  }
}
