import { MeasureDetailEntity } from '../domain/measuredetail.entity';

export class DatedMeasureDetailDto {
  dateTime: Date;
  measureDetail: MeasureDetailEntity;
}
