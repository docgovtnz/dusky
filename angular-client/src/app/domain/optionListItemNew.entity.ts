import { BaseEntity } from './base-entity';

export class OptionListItemNewEntity extends BaseEntity {
  name: string;

  constructor() {
    super();
    this.docType = 'OptionListItem';
  }
}
