import { BaseEntity } from './base-entity';

export class PersonEntity extends BaseEntity {
  accountExpiry: Date;
  comments: string;
  currentCapacity: string;
  emailAddress: string;
  hasAccount: boolean;
  name: string;
  personRole: string;
  phoneNumber: string;
  userName: string;

  constructor() {
    super();
    this.docType = 'Person';
  }
}
