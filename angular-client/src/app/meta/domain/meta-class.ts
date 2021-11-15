import { MetaProperty } from './meta-property';
import { DisplayModel } from './display-model';

export class MetaClass {
  name: string;
  metaProperties: MetaProperty[];
  displayModel: DisplayModel;

  constructor() {}
}
