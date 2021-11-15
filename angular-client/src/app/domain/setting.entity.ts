import { BaseEntity } from './base-entity';

export class SettingEntity extends BaseEntity {
  name: string;
  type: string;
  value: any;

  constructor() {
    super();
    this.docType = 'Setting';
  }
}
