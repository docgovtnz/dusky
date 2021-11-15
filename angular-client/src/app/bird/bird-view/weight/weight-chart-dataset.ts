import { Dataset } from './dataset';

export class DataSet extends Dataset {
  backgroundColor = 'rgba(255,255,255,0)';
  fill = false;
  pointRadius = 0;
  lineTension = 0.1;

  constructor(
    label: string,
    data: any[],
    color: string,
    dashes: number[],
    fill: any = false,
    fillColor: string = 'rgba(255,255,255,0)'
  ) {
    super();
    this.label = label;
    this.data = data;
    this.borderColor = color;
    this.backgroundColor = fillColor;
    this.borderDash = dashes;
    this.fill = fill;
  }
}
