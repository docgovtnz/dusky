import { Dataset } from './dataset';

export class LegendItem {
  label: string;
  birdID: string;
  datasets: Dataset[];
  hidden: boolean;
  type: number;
  borderWidth: number;
  borderColor: string;
  borderDash: number[];
  referenceData: boolean;
  backgroundColor: string;
}
