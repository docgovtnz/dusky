import { OptionListItemEntity } from './optionlistitem.entity';
import { BaseEntity } from './base-entity';

export class OptionListEntity extends BaseEntity {
  name: string;
  optionListItemList: OptionListItemEntity[];

  constructor() {
    super();
    this.docType = 'OptionList';
  }
}
