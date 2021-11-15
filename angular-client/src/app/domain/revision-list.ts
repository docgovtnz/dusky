import { BaseEntity } from './base-entity';

export class RevisionList {
  id: string;
  docType: string;
  revision: string;

  entityId: string;
  entityDocType: string;

  revisions: BaseEntity[];

  constructor() {}
}
