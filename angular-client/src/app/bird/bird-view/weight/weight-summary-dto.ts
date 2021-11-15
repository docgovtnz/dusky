export class WeightSummaryDto {
  recordId: string;
  method: string;
  weight: number;
  dateTime: Date;
  dailyWeightChange: number;
  ageDays: number;
  cropStatus: string;

  constructor() {}
}
