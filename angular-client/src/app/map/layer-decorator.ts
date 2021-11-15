export class LayerDecorator {
  private _layerVisibility = false;
  private _labelVisibility = false;
  private _mapLayer: any;

  constructor(public layerName: string) {}

  get layerVisibility(): boolean {
    return this._layerVisibility;
  }

  set layerVisibility(value: boolean) {
    this._layerVisibility = value;
    if (this._mapLayer) {
      this._mapLayer.setVisibility(value);
    }
  }

  get labelVisibility(): boolean {
    return this._labelVisibility;
  }

  set labelVisibility(value: boolean) {
    this._labelVisibility = value;
    if (this._mapLayer) {
      this._mapLayer.setShowLabels(value);
    }
  }

  get mapLayer(): any {
    return this._mapLayer;
  }

  set mapLayer(value: any) {
    this._mapLayer = value;
    this._mapLayer.setVisibility(this._layerVisibility);
  }
}
