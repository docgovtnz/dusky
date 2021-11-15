import { Dataset } from './dataset';

export class DataSet extends Dataset {
  backgroundColor = 'rgba(255,255,255,0)';
  fill = false;
  pointRadius = 3;
  lineTension = 0.1;

  constructor(label: string, color: string, data: any[]) {
    super();
    this.label = label;
    this.data = data;
    this.borderColor = color;
  }
}
