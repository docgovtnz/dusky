/* eslint-disable */

import { combineLatest, from, Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  ViewChild,
} from '@angular/core';

import * as moment from 'moment';
import * as ColorHash from 'color-hash';
import { loadCss, loadModules } from 'esri-loader';

import { IslandService } from '../setting/island/island.service';
import { IslandEntity } from '../domain/island.entity';
import { LocationService } from '../location/location.service';
import { LocationCriteria } from '../location/location.criteria';
import { LocationEntity } from '../domain/location.entity';
import { BaseEntity } from '../domain/base-entity';
import { LayerDecorator } from './layer-decorator';
import { RecordSearchDTO } from '../record/record-search-dto';
import { BirdLabelToggleComponent } from './bird-label-toggle/bird-label-toggle.component';
import { LocationBearingEntity } from '../domain/locationbearing.entity';
import { TriangulationService } from '../triangulation/service/triangulation.service';
import { MapColorService } from './map-color.service';

//import esri = __esri;

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  Additional Classes
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

export class BirdData {
  birdID: string;
  birdName: string;
  selected = true;

  constructor(birdID: string, birdName: string) {
    this.birdID = birdID;
    this.birdName = birdName;
  }
}

export class LocationPoint {
  constructor(public easting: number, public northing: number) {}
}

export class Feature {
  attributes = {
    objectID: null,
    labelText: null,
    infoWindowTitle: null,
    easting: null,
    northing: null,
    locationType: null,
    locationName: null,
    locationId: null,
    mapFeatureType: null,
  };

  geometry = null;

  constructor(objectID: number, mapFeatureType: string, geometry: any) {
    this.attributes.objectID = objectID;
    this.attributes.mapFeatureType = mapFeatureType;
    this.geometry = geometry;
  }
}

export class LayerDefinition {
  geometryType = 'esriGeometryPoint';
  objectIdField = 'objectID';

  drawingInfo = {
    renderer: {
      type: 'simple',
      symbol: {
        color: [255, 45, 192, 180],
        size: 8,
        angle: 0,
        xoffset: 0,
        yoffset: 0,
        type: 'esriSMS',
        style: 'esriSMSCircle',
        outline: {
          color: [0, 0, 0, 255],
          width: 0,
          type: 'esriSLS',
          style: 'esriSLSSolid',
        },
      },
    },
  };

  fields = [
    {
      name: 'ObjectID',
      alias: 'ObjectID',
      type: 'esriFieldTypeOID',
    },
    {
      name: 'locationName',
      alias: 'locationName',
      type: 'esriFieldTypeString',
    },
    {
      name: 'labelText',
      alias: 'labelText',
      type: 'esriFieldTypeString',
    },
  ];

  constructor() {}
}

/**
 * The eventual answer to getting labels to show on the map came from these links.
 *
 * https://community.esri.com/thread/193924-label-feature-layer-created-by-feature-collection
 * https://community.esri.com/thread/204864-how-to-label-point-feature-class-created-dynamically-in-wab
 *
 */
@Component({
  selector: 'app-esri-map',
  templateUrl: './esri-map.component.html',
  styleUrls: ['./esri-map.component.scss'],
})
export class EsriMapComponent implements OnInit {
  // This first block of attribute declarations are actually old school JS Class functions. The loadModules() method
  // asynchronously loads these functions, and we need to keep a pointer to them in other to use them elsewhere
  // in the component.
  Map: any;
  Extent: any;
  ArcGISTiledMapServiceLayer: any;
  FeatureLayer: any;
  Color: any;
  Graphic: any;
  SpatialReference: any;
  Geometry: any;
  Point: any;
  SimpleMarkerSymbol: any;
  SimpleLineSymbol: any;
  SimpleRenderer: any;
  UniqueValueRenderer: any;
  InfoTemplate: any;
  TextSymbol: any;
  LabelClass: any;
  Polyline: any;

  mapLoaded = false;
  mapLoadedEvent = new EventEmitter();

  @Input()
  displayMapTypeBtns = true;

  @Input()
  displayLocationTypeCheckBoxes = true;

  @Input()
  displayIslandSelector = true;

  @Input()
  positionFixed = true;

  _locationBearingList: LocationBearingEntity[];
  _triangulationPoint: LocationPoint;

  _selectedLocation: LocationEntity = null;

  _selectedRecord: RecordSearchDTO = null;

  @Input()
  locations: LocationEntity[] = null;

  private _birdLocations: RecordSearchDTO[] = null;

  @ViewChild(BirdLabelToggleComponent)
  birdLabelToggleComponent: BirdLabelToggleComponent;

  map: any;
  layerMap = new Map<string, LayerDecorator>(); // this is a data Map for sticking stuff in, not a Map for display of GIS data

  //readonly mapServerURLTemplate = "https://services.arcgisonline.co.nz/arcgis/rest/services/{mapType}/MapServer";
  readonly mapServerURLTemplate = '/api/esri/{mapType}/MapServer';
  mapLayerGeneric: any;
  mapLayerGeotiffs: any;
  mapLayerImagery: any;

  private _selectedIslandName;
  selectedIsland: IslandEntity;
  islandList: IslandEntity[] = [];

  objectIdFactory = 1;
  // TODO populate from the async OptionsService.getOptions('LocationTypes');
  locationTypes = [
    'Brooder',
    'Helipad',
    'Hopper',
    'Incubator',
    'Nest',
    'NoraNet',
    'Pen',
    'Roost',
    'Seed tray',
    'Track and Bowl',
    'Trap',
    'Triangulation point',
    'Veg monitoring site',
  ];
  private _visibleLayers: string[] = [];

  private _colorHash = new ColorHash();

  birdList: BirdData[];

  constructor(
    private islandService: IslandService,
    private locationService: LocationService,
    private colorService: MapColorService
  ) {
    // create the layer decorators now, the mapLayer for these will come later
    this.locationTypes.forEach((locationType) => {
      this.layerMap.set(locationType, new LayerDecorator(locationType));
    });
  }

  /**
   * "compareWith" code/id comparison for selecting an Island
   */
  byId(o1: BaseEntity, o2: BaseEntity) {
    return o1 && o2 && o1.id === o2.id;
  }

  ngOnInit() {
    this.loadIslandList().subscribe(() => {
      // this next bit depends on the island being selected, and loaded
      combineLatest(this.loadLocations(), this.loadMap()).subscribe(
        ([locations]) => {
          // possible performance optimisation here, if we don't need to show locations then don't load them
          this.addLocations(locations);
          if (this._selectedLocation) {
            this.addSelectedLocation();
          }
          if (this._selectedRecord) {
            this.addSelectedRecord();
          }
          if (this._birdLocations) {
            this.addBirdLocations();
          }
          if (this.selectedIsland) {
            this.onIslandSelected(this.selectedIsland);
          }
        }
      );
    });
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //  "Pure" Map stuff
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  loadMap(): Observable<any> {
    const initialExtent = {
      xmax: this.selectedIsland.upperEasting,
      xmin: this.selectedIsland.lowerEasting,
      ymax: this.selectedIsland.upperNorthing,
      ymin: this.selectedIsland.lowerNorthing,
      spatialReference: { wkid: 2193 },
    };

    return this.loadMapModules().pipe(
      map(() => {
        this.map = new this.Map('mapNode', {
          // basemap: "gray",
          // center: [ 167.643995, -46.769071 ],
          // zoom: 4
          showLabels: true,
        });

        this.mapLayerGeotiffs = new this.ArcGISTiledMapServiceLayer(
          this.mapServerURLTemplate.replace('{mapType}', 'LINZ/geotiffs')
        );
        this.mapLayerGeotiffs.setOpacity(0.5);
        this.mapLayerGeotiffs.visible = true;
        this.map.addLayer(this.mapLayerGeotiffs);

        this.mapLayerGeneric = new this.ArcGISTiledMapServiceLayer(
          this.mapServerURLTemplate.replace('{mapType}', 'Generic/newzealand')
        );
        this.mapLayerGeneric.setOpacity(0.5);
        this.mapLayerGeneric.visible = false;
        this.map.addLayer(this.mapLayerGeneric);

        this.mapLayerImagery = new this.ArcGISTiledMapServiceLayer(
          this.mapServerURLTemplate.replace('{mapType}', 'Imagery/newzealand')
        );
        this.mapLayerImagery.setOpacity(0.5);
        this.mapLayerImagery.visible = false;
        this.map.addLayer(this.mapLayerImagery);

        this.map.setExtent(new this.Extent(initialExtent));

        this.mapLoaded = true;
        this.mapLoadedEvent.next();
      })
    );
  }

  loadMapModules(): Observable<any> {
    loadCss('/esri/3.25/esri/css/esri.css');

    const options = {
      url: '/esri/3.25/init.js',
    };

    return from(
      loadModules(
        [
          'esri/map',
          'esri/geometry/Extent',
          'esri/layers/ArcGISTiledMapServiceLayer',
          'esri/layers/FeatureLayer',
          'esri/Color',
          'esri/graphic',
          'esri/SpatialReference',
          'esri/geometry/Geometry',
          'esri/geometry/Point',
          'esri/symbols/SimpleMarkerSymbol',
          'esri/symbols/SimpleLineSymbol',
          'esri/renderers/SimpleRenderer',
          'esri/renderers/UniqueValueRenderer',
          'esri/InfoTemplate',
          'esri/symbols/TextSymbol',
          'esri/layers/LabelClass',
          'esri/geometry/Polyline',
          'esri/config',
          'dojo/domReady!',
        ],
        options
      )
        .then(
          ([
            pMap,
            pExtent,
            pArcGISTiledMapServiceLayer,
            pFeatureLayer,
            pColor,
            Graphic,
            pSpatialReference,
            pGeometry,
            pPoint,
            pSimpleMarkerSymbol,
            pSimpleLineSymbol,
            pSimpleRenderer,
            pUniqueValueRenderer,
            pInfoTemplate,
            pTextSymbol,
            pLabelClass,
            pPolyline,
            esriConfig,
          ]) => {
            this.Map = pMap;
            this.Extent = pExtent;
            this.ArcGISTiledMapServiceLayer = pArcGISTiledMapServiceLayer;
            this.FeatureLayer = pFeatureLayer;
            this.Color = pColor;
            this.Graphic = Graphic;
            this.SpatialReference = pSpatialReference;
            this.Geometry = pGeometry;
            this.Point = pPoint;
            this.SimpleMarkerSymbol = pSimpleMarkerSymbol;
            this.SimpleLineSymbol = pSimpleLineSymbol;
            this.SimpleRenderer = pSimpleRenderer;
            this.UniqueValueRenderer = pUniqueValueRenderer;
            this.InfoTemplate = pInfoTemplate;
            this.TextSymbol = pTextSymbol;
            this.LabelClass = pLabelClass;
            this.Polyline = pPolyline;
          }
        )
        .catch((err) => {
          console.error(err);
        })
    );
  }

  createFeatureLayer(layerId: string, features: Feature[]) {
    const layerDefinition = new LayerDefinition();

    //create a feature collection for the points
    const featureCollection = {
      layerDefinition,
      featureSet: {
        features,
        geometryType: 'esriGeometryPoint',
      },
    };

    //create a feature layer based on the feature collection
    const featureLayer = new this.FeatureLayer(featureCollection, {
      id: layerId,
      outFields: ['*'],
      showLabels: true,
    });

    const defaultSymbol = this.createDefaultSymbol();
    const renderer = new this.UniqueValueRenderer(
      defaultSymbol,
      'mapFeatureType'
    );
    featureLayer.setRenderer(renderer);

    const labelClass = this.createLabelClass();
    featureLayer.setLabelingInfo([labelClass]);

    return featureLayer;
  }

  createDefaultSymbol() {
    const defaultSymbol = new this.SimpleMarkerSymbol(
      this.SimpleMarkerSymbol.STYLE_CIRCLE,
      10,
      new this.SimpleLineSymbol(
        this.SimpleLineSymbol.STYLE_SOLID,
        new this.Color([0, 0, 0, 0]),
        1
      ),
      new this.Color([255, 45, 192, 1])
    );
    return defaultSymbol;
  }

  createLabelClass() {
    const haloColor = new this.Color([245, 255, 45, 0.8]);
    const textColor = new this.Color([20, 20, 20, 0.9]);

    //create a text symbol and renderer to define the style of labels
    const labelSymbol = new this.TextSymbol();
    labelSymbol.color = textColor;
    labelSymbol.haloColor = haloColor;
    labelSymbol.haloSize = 3;
    labelSymbol.setOffset(4, -12);
    labelSymbol.font.setSize('10pt');
    labelSymbol.font.setFamily('arial');

    const labelClass = new this.LabelClass({
      labelExpressionInfo: { value: '{labelText}' },
    });

    labelClass.symbol = labelSymbol; // symbol also can be set in LabelClass' json
    return labelClass;
  }

  onChangeMapType(mapType: string) {
    // TODO address timing issue when changing mapType before map is loaded
    console.log('onChangeMapType: ' + mapType);
    this.mapLayerGeneric.setVisibility(false);
    this.mapLayerGeotiffs.setVisibility(false);
    this.mapLayerImagery.setVisibility(false);
    switch (mapType) {
      case 'Geotiffs':
        this.mapLayerGeotiffs.setVisibility(true);
        break;
      case 'Generic':
        this.mapLayerGeneric.setVisibility(true);
        break;
      case 'Imagery':
        this.mapLayerImagery.setVisibility(true);
        break;
      default:
        throw new Error('Unknown mapType of ' + mapType);
    }
  }

  getLocationTypeState(locationType) {
    let state = 0;
    const layerDecorator = this.layerMap.get(locationType);
    if (layerDecorator) {
      if (layerDecorator.labelVisibility) {
        state = 2;
      } else if (layerDecorator.layerVisibility) {
        state = 1;
      } else {
        state = 0;
      }
    }
    return state;
  }

  onLocationTypeStateChange(locationType: string, state: number) {
    const layerVisibility = state > 0;
    this.setLayerVisibility(locationType, layerVisibility);

    const labelVisibility = state === 2;
    this.layerMap.get(locationType).labelVisibility = labelVisibility;
  }

  getLayerVisibility(locationType: string) {
    return this.layerMap.get(locationType).layerVisibility;
  }

  setLayerVisibility(locationType: string, $event) {
    if (this.layerMap && this.layerMap.get(locationType)) {
      this.layerMap.get(locationType).layerVisibility = $event;
    }
  }

  get visibleLayers(): string[] {
    return this._visibleLayers;
  }

  @Input()
  set visibleLayers(value: string[]) {
    console.log('set visibleLayers() ' + JSON.stringify(value));
    this._visibleLayers = value;

    // switch off all the layers first (hopefully this doesn't cause any flicker
    this.locationTypes.forEach((layerName) => {
      this.setLayerVisibility(layerName, false);
    });

    // then only switch on the ones we've been asked to make visible
    if (value && value.length) {
      value.forEach((layerName) => {
        //this.setLayerVisibility(layerName, true);
        this.onLocationTypeStateChange(layerName, 2);
      });
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //  Locations
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  addLocations(locations: LocationEntity[]) {
    console.log('addLocations() length = ' + locations.length);

    this.locationTypes.forEach((locationType) => {
      const features = this.createLocationFeatures(locations, locationType);
      const featureLayer = this.createFeatureLayer(locationType, features);

      const template = new this.InfoTemplate('${infoWindowTitle}');
      template.setContent(this.locationInfoTemplate());
      featureLayer.setInfoTemplate(template);

      this.map.addLayer(featureLayer);
      this.layerMap.get(locationType).mapLayer = featureLayer;
    });
  }

  private locationInfoTemplate(): string {
    return (
      'Location Type: <strong>${locationType}</strong>' +
      '<br>Coordinates: <strong>${easting}, ${northing}</strong>' +
      '<br>Link: <a href="/app/location/${locationId}">Open Location</a>'
    );
  }

  get selectedLocation() {
    return this._selectedLocation;
  }

  @Input()
  set selectedLocation(location: LocationEntity) {
    if (this.mapLoaded && this._selectedLocation) {
      this.removeSelectedLocation();
    }
    this._selectedLocation = location;
    if (this.mapLoaded) {
      this.addSelectedLocation();
    }
  }

  addSelectedLocation() {
    if (this._selectedLocation) {
      // Warning: creating a fake locationType here, but seemed the easiest way to implement things.
      const locationType = 'Selected Location';
      this.layerMap.set(locationType, new LayerDecorator(locationType));

      const feature = this.createOneLocationFeature(this.selectedLocation);
      const featureLayer = this.createFeatureLayer(locationType, [feature]);
      this.map.addLayer(featureLayer);
      this.layerMap.get(locationType).mapLayer = featureLayer;
      this.setLayerVisibility(locationType, true);
    }
  }

  removeSelectedLocation() {
    if (this.map && this._selectedLocation) {
      // using same 'fake locationType' from addSelectedLocation
      const locationType = 'Selected Location';
      const layerDecorator = this.layerMap.get(locationType);
      // layer won't exist when selected location was never added
      if (layerDecorator && layerDecorator.mapLayer) {
        this.map.removeLayer(layerDecorator.mapLayer);
      }
    }
  }

  get selectedRecord() {
    return this._selectedRecord;
  }

  @Input()
  set selectedRecord(record: RecordSearchDTO) {
    if (this.mapLoaded && this._selectedRecord) {
      this.removeSelectedRecord();
    }
    this._selectedRecord = record;
    if (this.mapLoaded) {
      this.addSelectedRecord();
    }
  }

  addSelectedRecord() {
    if (this._selectedRecord) {
      // Warning: creating a fake locationType here, but seemed the easiest way to implement things.
      const locationType = 'Bird Locations';
      this.layerMap.set(locationType, new LayerDecorator(locationType));

      const feature = this.createOneRecordSearchDTOFeature(
        this._selectedRecord,
        true,
        true,
        true
      );
      // feature will come back null if selected record contains no coordinates
      if (feature) {
        const featureLayer = this.createFeatureLayer(locationType, [feature]);
        this.map.addLayer(featureLayer);
        this.layerMap.get(locationType).mapLayer = featureLayer;
        this.setLayerVisibility(locationType, true);
      }
    }
  }

  removeSelectedRecord() {
    if (this.map && this._selectedRecord) {
      // using same 'fake locationType' from addSelectedRecord
      const locationType = 'Bird Locations';
      const layerDecorator = this.layerMap.get(locationType);
      // layer won't exist when selected record was never added
      if (layerDecorator && layerDecorator.mapLayer) {
        this.map.removeLayer(layerDecorator.mapLayer);
      }
    }
  }

  loadLocations(): Observable<LocationEntity[]> {
    if (this.locations) {
      return of(this.locations);
    } else {
      const locationCriteria = new LocationCriteria();
      locationCriteria.pageSize = 9999;
      return this.locationService
        .search(locationCriteria)
        .pipe(map((response) => response.results));
    }
  }

  createLocationFeatures(
    locations: LocationEntity[],
    locationType: string
  ): Feature[] {
    const features = [];

    locations.forEach((location) => {
      if (location.locationType === locationType) {
        features.push(this.createOneLocationFeature(location));
      }
    });

    return features;
  }

  createOneLocationFeature(location: LocationEntity): Feature {
    const point = new this.Point(
      Number(location.easting),
      Number(location.northing),
      new this.SpatialReference({ wkid: 2193 })
    );
    const feature = new Feature(this.objectIdFactory++, null, point);
    const attributes = feature.attributes;
    attributes.labelText = location.locationName; // labelText is the label that appears on the map
    attributes.infoWindowTitle = location.locationName; // infoWindowTitle is the label that is the title of popup (so, it's the same but different)
    attributes.locationType = location.locationType;
    attributes.easting = location.easting;
    attributes.northing = location.northing;
    attributes.locationId = location.id;
    return feature;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //  Location Bearings
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  get locationBearingList(): LocationBearingEntity[] {
    return this._locationBearingList;
  }

  @Input()
  set locationBearingList(value: LocationBearingEntity[]) {
    this._locationBearingList = value;
    this.addTriangulationGraphics();
  }

  addLocationBearings(graphicsList: any[]) {
    if (this.locationBearingList) {
      this.locationBearingList.forEach((locationBearing) => {
        if (
          locationBearing &&
          locationBearing.active &&
          locationBearing.easting &&
          locationBearing.northing
        ) {
          this.addOneLocationBearing(locationBearing, graphicsList);
        }
      });
    }
  }

  addOneLocationBearing(
    locationBearing: LocationBearingEntity,
    graphicsList: any[]
  ): void {
    // Add a circle to represent the starting point
    const startOutlineSymbol = new this.SimpleLineSymbol()
      .setColor(this.Color([50, 50, 50, 50]), 0.6)
      .setStyle(this.SimpleLineSymbol.STYLE_SOLID)
      .setWidth(3);

    const startSymbol = new this.SimpleMarkerSymbol()
      .setColor(this.Color([50, 50, 50, 0.25]))
      .setOutline(startOutlineSymbol)
      .setStyle(this.SimpleMarkerSymbol.STYLE_CIRCLE);

    const startGeometry = new this.Point(
      locationBearing.easting,
      locationBearing.northing,
      new this.SpatialReference({ wkid: 2193 })
    );
    graphicsList.push(new this.Graphic(startGeometry, startSymbol));

    // calculate a vector based on the compass bearing and how long we want the indicator lines to be
    if (
      locationBearing.compassBearing !== undefined &&
      locationBearing.compassBearing !== null
    ) {
      const vector = TriangulationService.createVectorFromCompassBearing(
        locationBearing.compassBearing,
        4000
      );

      // the indicator line is a list of x,y coordinates, add the vector to the locationBearing to get the second
      // point of the line
      const linePath = [
        [locationBearing.easting, locationBearing.northing],
        [
          locationBearing.easting + vector.x,
          locationBearing.northing + vector.y,
        ],
      ];

      // create a Graphic object with a line following each of the points in this path
      const geometry = new this.Polyline(
        new this.SpatialReference({ wkid: 2193 })
      );
      geometry.addPath(linePath);

      const symbol = new this.SimpleLineSymbol()
        .setColor(this.Color([50, 50, 50, 50]), 0.6)
        .setStyle(this.SimpleLineSymbol.STYLE_SOLID)
        .setWidth(2);

      graphicsList.push(new this.Graphic(geometry, symbol));
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //  Triangulation Point
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  get triangulationPoint(): LocationPoint {
    return this._triangulationPoint;
  }

  @Input()
  set triangulationPoint(value: LocationPoint) {
    this._triangulationPoint = value;
    this.addTriangulationGraphics();
  }

  addTriangulationGraphics() {
    console.log('addTriangulationGraphics() ');
    if (this.mapLoaded && this.map && this.map.graphics) {
      this.addGraphics();
    } else {
      // works but slow
      /*setTimeout(() => {
        this.addTriangulationGraphics();
      }, 5000);*/

      // doesn't work
      /*this.mapLoadedEvent.subscribe(() => {
        this.addTriangulationGraphics();
      });*/

      // fails with errors
      /*this.map.on("load", () => {
        this.addTriangulationGraphics();
      });*/

      // Seems to be working, wait for our event (this.map will be true) then wait for the map's load event
      this.mapLoadedEvent.subscribe(() => {
        this.map.on('load', () => {
          this.addTriangulationGraphics();
        });
      });
    }
  }

  addGraphics() {
    const graphicsList = [];

    this.addLocationBearings(graphicsList);
    this.addTriangulationPoint(graphicsList);

    this.map.graphics.clear();
    graphicsList.forEach((graphic) => {
      this.map.graphics.add(graphic);
    });
  }

  addTriangulationPoint(graphicsList: any[]) {
    if (
      this.triangulationPoint &&
      this.triangulationPoint.easting &&
      this.triangulationPoint.northing
    ) {
      const symbol = new this.SimpleMarkerSymbol()
        .setColor(this.Color([50, 50, 50, 50]), 0.6)
        .setStyle(this.SimpleMarkerSymbol.STYLE_X);

      const geometry = new this.Point(
        this.triangulationPoint.easting,
        this.triangulationPoint.northing,
        new this.SpatialReference({ wkid: 2193 })
      );

      const graphic = new this.Graphic(geometry, symbol);
      graphicsList.push(graphic);
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //  Birds
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  addBirdLocations() {
    this.addBirdPaths();
    this.addBirdFeatures();
  }

  private addBirdFeatures() {
    // Warning: creating a fake locationType here, but seemed the easiest way to implement things.
    const locationType = 'Bird Locations';
    this.layerMap.set(locationType, new LayerDecorator(locationType));

    const features = [];
    // Features are created from the Record list because we want one point per Record, however if the Record doesn't
    // have location data then createOneBirdFeature() will return null, so don't add that one. Also note that because
    // the data uses a lot of fixed Locations often multiple records are at the same location, so you won't see that
    // many points on the map.
    this.birdLocations.forEach((birdRecord) => {
      const birdData = this.findBirdData(birdRecord.birdID);
      if (birdData.selected) {
        const feature = this.createOneBirdFeature(birdRecord);
        if (feature) {
          features.push(feature);
        }
      }
    });
    console.log('addBirdLocations() count = ' + features.length);

    // Sort the feature list so all of the Latest ones are at the top of the list and so are drawn first and don't mask
    // other features
    this.sortBirdFeatures(features);

    const featureLayer = this.createFeatureLayer(locationType, features);
    const template = new this.InfoTemplate('${infoWindowTitle}');
    template.setContent(this.birdInfoTemplate());
    featureLayer.setInfoTemplate(template);

    // Symbols are created from the Bird list, because we only need a couple of types of symbols
    this.birdList.forEach((bird) => {
      this.createBirdSymbols(bird, featureLayer.renderer);
    });

    this.map.addLayer(featureLayer);
    this.layerMap.get(locationType).mapLayer = featureLayer;
    this.setLayerVisibility(locationType, true);
  }

  private findBirdData(birdID: string): BirdData {
    return this.birdList.find((bird) => bird.birdID === birdID);
  }

  private birdInfoTemplate(): string {
    return (
      'Record Type: <strong>${recordType}</strong>' +
      '<br>Coordinates: <strong>${easting}, ${northing}</strong>' +
      '<br>Location: <strong>${locationName}</strong>' +
      '<br>Link: <a href="/app/record/${recordID}">Open Record</a>'
    );
  }

  private addBirdPaths() {
    // Clear all of the paths to start with
    if (this.map && this.map.graphics) {
      this.map.graphics.clear();
    } else {
      this.map.on('load', () => {
        this.map.graphics.clear();
      });
    }

    // Only display path if the checkbox is on AND the Bird is selected
    if (
      this.birdLabelToggleComponent &&
      this.birdLabelToggleComponent.displayPaths
    ) {
      // for each bird from the birdList
      this.birdList.forEach((bird) => {
        if (bird.selected) {
          console.log(
            'addBirdPaths(): ' + bird.birdName + ' selected = ' + bird.selected
          );
          this.addOneBirdPath(bird);
        }
      });
    }
  }

  private addOneBirdPath(bird: BirdData) {
    // find all of the records for this Bird
    const thisBirdRecords = this.birdLocations.filter(
      (record) => record.birdID === bird.birdID
    );

    // find the path of locations for this bird
    const birdPath = [];
    thisBirdRecords.forEach((record) => {
      const locationPoint = EsriMapComponent.getLocationPointFromRecord(record);
      if (locationPoint) {
        birdPath.push([locationPoint.easting, locationPoint.northing]);
      }
    });

    // create a Graphic object with a line following each of the points in this path
    const geometry = new this.Polyline(
      new this.SpatialReference({ wkid: 2193 })
    );
    geometry.addPath(birdPath);

    const birdColor = this.getBirdColor(bird.birdID);
    const symbol = new this.SimpleLineSymbol()
      .setColor(new this.Color(birdColor))
      .setStyle(this.SimpleLineSymbol.STYLE_DASH)
      .setWidth(2);
    const graphic = new this.Graphic(geometry, symbol);

    // WARNING: this bit of ugly is needed because otherwise sometimes the add() method is being called before the
    // map is fully loaded. But not sure why because loadMap() is supposed to be synchronous
    if (this.map && this.map.graphics) {
      this.map.graphics.add(graphic);
    } else {
      this.map.on('load', () => {
        this.map.graphics.add(graphic);
      });
    }

    //console.log("Bird path: " + bird.birdName + ": " + JSON.stringify(birdPath));
  }

  private sortBirdFeatures(features: any[]) {
    features.sort((t1: any, t2: any) => {
      const t1Latest = t1.attributes.mapFeatureType.indexOf('Latest') > -1;
      const t2Latest = t2.attributes.mapFeatureType.indexOf('Latest') > -1;

      if (t1Latest && !t2Latest) {
        return -1;
      } else if (!t1Latest > t2Latest) {
        return 1;
      } else {
        return 0;
      }
    });
  }

  createOneRecordSearchDTOFeature(
    record: RecordSearchDTO,
    displayBirdNames: boolean,
    displayRecordDate: boolean,
    displayRecordType: boolean
  ) {
    let feature = null;
    const locationPoint = EsriMapComponent.getLocationPointFromRecord(record);
    if (record.mapFeatureType && locationPoint) {
      const objectId = this.objectIdFactory++;
      const point = new this.Point(
        locationPoint.easting,
        locationPoint.northing,
        new this.SpatialReference({ wkid: 2193 })
      );

      let labelText = '';
      if (displayBirdNames) {
        labelText = labelText + record.birdName + ' ';
      }

      if (displayRecordDate) {
        labelText =
          labelText + moment(record.dateTime).format('D-MMM-YY') + ' ';
      }

      if (displayRecordType) {
        labelText = labelText + record.recordType;
      }

      feature = new Feature(objectId, record.mapFeatureType, point);
      const attributes = feature.attributes;
      attributes.labelText = labelText;
      attributes.infoWindowTitle =
        record.birdName +
        ' ' +
        moment(record.dateTime).format('D-MMM-YY HH:mm');
      attributes.easting = locationPoint.easting;
      attributes.northing = locationPoint.northing;
      attributes.locationName = record.locationName;
      attributes.recordType = record.recordType;
      attributes.recordID = record.recordID;
    }

    return feature;
  }

  createOneBirdFeature(record: RecordSearchDTO): Feature {
    return this.createOneRecordSearchDTOFeature(
      record,
      this.birdLabelToggleComponent.displayBirdNames,
      this.birdLabelToggleComponent.displayRecordDate,
      this.birdLabelToggleComponent.displayRecordType
    );
  }

  static getLocationPointFromRecord(record: RecordSearchDTO): LocationPoint {
    let easting;
    let northing = null;
    if (record.easting && record.northing) {
      easting = record.easting;
      northing = record.northing;
    } else if (record.locationEasting && record.locationNorthing) {
      easting = record.locationEasting;
      northing = record.locationNorthing;
    }

    let locationPoint = null;
    if (easting && northing) {
      locationPoint = new LocationPoint(Number(easting), Number(northing));
    }

    return locationPoint;
  }

  hasBirdData(): boolean {
    return this.birdLocations !== null;
  }

  get birdLocations(): RecordSearchDTO[] {
    return this._birdLocations;
  }

  @Input()
  set birdLocations(value: RecordSearchDTO[]) {
    if (this.mapLoaded && this._birdLocations) {
      this.removeBirdLocations();
    }

    this._birdLocations = value;
    this.birdList = this.getBirdListFromRecords(value);

    if (this.mapLoaded) {
      this.addBirdLocations();
    }
  }

  getBirdListFromRecords(recordList: RecordSearchDTO[]) {
    const birdMap = new Map<string, any>();
    recordList.forEach((record) => {
      if (!birdMap.has(record.birdID)) {
        const birdData = new BirdData(record.birdID, record.birdName);
        birdMap.set(birdData.birdID, birdData);
      }
    });

    const birdList = Array.from(birdMap.values());

    birdList.sort((t1: any, t2: any) => {
      if (t1.birdName < t2.birdName) {
        return -1;
      } else if (t1.birdName > t2.birdName) {
        return 1;
      } else {
        return 0;
      }
    });
    //todo save in the color service
    this.colorService.birdList = birdList;
    return birdList;
  }

  updateBirdLocations() {
    // This dumps the BirdLocations layer and rebuilds it. Causes a redraw "flash" but it's the simplest way of
    // doing it.
    this.removeBirdLocations();
    this.addBirdLocations();
  }

  removeBirdLocations() {
    const locationType = 'Bird Locations';
    const layerDecorator = this.layerMap.get(locationType);
    // layer won't exist if bird locations have never been added
    if (layerDecorator) {
      this.map.removeLayer(layerDecorator.mapLayer);
    }
  }

  createBirdSymbols(bird: any, renderer: any) {
    if (bird) {
      const birdColor = this.getBirdColor(bird.birdID);

      const normalBirdSymbol = new this.SimpleMarkerSymbol(
        this.SimpleMarkerSymbol.STYLE_CIRCLE,
        12,
        new this.SimpleLineSymbol(
          this.SimpleLineSymbol.STYLE_SOLID,
          new this.Color([0, 0, 0, 0]),
          1
        ),
        new this.Color(birdColor)
      );

      const latestBirdSymbol = new this.SimpleMarkerSymbol(
        this.SimpleMarkerSymbol.STYLE_DIAMOND,
        22,
        new this.SimpleLineSymbol(
          this.SimpleLineSymbol.STYLE_SOLID,
          new this.Color([0, 0, 0, 0]),
          1
        ),
        new this.Color(birdColor)
      );

      renderer.addValue(bird.birdName, normalBirdSymbol);
      renderer.addValue(bird.birdName + '-Latest', latestBirdSymbol);
    }
  }

  private getBirdColor(birdID: string) {
    let colorP = 0;
    const colorRamp = this.colorService.colorRamp;
    for (colorP = 0; colorP < colorRamp.length; colorP++) {
      if (birdID === this.birdList[colorP].birdID) {
        break;
      }
    }
    if (colorP < colorRamp.length) {
      return colorRamp[colorP];
    } else {
      const color = this._colorHash.rgb(birdID);
      color.push(1);
      return color;
    }
  }

  onBirdSelectionChanges() {
    console.log('onBirdSelectionChanges()');
    this.updateBirdLocations();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //  Islands
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  loadIslandList(): Observable<any> {
    return this.islandService.findAll().pipe(
      map((islandList) => {
        islandList.forEach((island) => {
          // Only using the islands that have a bounding box
          if (island.upperNorthing && island.upperEasting) {
            this.islandList.push(island);
          }

          const islandToFind = this._selectedIslandName
            ? this._selectedIslandName
            : 'Anchor';
          if (island.name === islandToFind) {
            this.selectedIsland = island;
            if (this.mapLoaded) {
              this.onIslandSelected(this.selectedIsland);
            }
          }
        });

        if (!this.selectedIsland) {
          throw new Error('Unable to find the default island');
        }
      })
    );
  }

  onIslandSelected(island: IslandEntity) {
    console.log('onIslandSelected: ' + this.selectedIsland.name);

    const box = {
      xmax: island.upperEasting,
      xmin: island.lowerEasting,
      ymax: island.upperNorthing,
      ymin: island.lowerNorthing,
      spatialReference: { wkid: 2193 },
    };

    const nextExtent = new this.Extent(box);
    this.map.setExtent(nextExtent);
  }

  get selectedIslandName() {
    return this._selectedIslandName;
  }

  @Input()
  set selectedIslandName(value) {
    console.log('selectedIslandName() ' + value);
    if (this.islandList && this.islandList.length > 0) {
      let foundIsland = null;
      this.islandList.forEach((island) => {
        if (island.name === value) {
          foundIsland = island;
        }
      });

      if (foundIsland) {
        this._selectedIslandName = value;
        this.selectedIsland = foundIsland;
        if (this.mapLoaded) {
          this.onIslandSelected(foundIsland);
        }
      } else {
        console.log('Unable to find island for: ' + value);
      }
    } else {
      // Island list is not yet loaded, we can still set the value and the island loading should hopefully
      // then be able to select it at that point
      this._selectedIslandName = value;
    }
  }
}
