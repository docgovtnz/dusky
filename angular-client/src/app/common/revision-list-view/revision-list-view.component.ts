import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { RevisionService } from '../revision.service';
import { RevisionList } from '../../domain/revision-list';
import { BaseEntity } from '../../domain/base-entity';
import { PersonEntity } from '../../domain/person.entity';
import { PersonService } from '../../person/person.service';

@Component({
  selector: 'app-revision-list-view',
  templateUrl: './revision-list-view.component.html',
})
export class RevisionListViewComponent implements OnInit {
  private _revisionIdx = 0;
  private _entity: BaseEntity;

  isActive = false;

  revisionList: RevisionList;

  @Output()
  revisionEvent = new EventEmitter<BaseEntity>();
  private _revision: BaseEntity;

  person: PersonEntity;
  modifiedByPersonLabel: string;

  constructor(
    private revisionService: RevisionService,
    private personService: PersonService
  ) {}

  ngOnInit() {}

  get entity(): BaseEntity {
    return this._entity;
  }

  @Input()
  set entity(value: BaseEntity) {
    this._entity = value;
    this._revision = value;
    this.loadRevisionList();
  }

  get revision(): BaseEntity {
    return this._revision;
  }

  set revision(value: BaseEntity) {
    this._revision = value;
    this.revisionEvent.next(this._revision);
  }

  get revisionIdx(): number {
    return this._revisionIdx;
  }

  set revisionIdx(value: number) {
    this._revisionIdx = value;
    this.revision = this.revisionList.revisions[value];

    this.person = null;
    const personId = this.revision.modifiedByPersonId;
    if (personId) {
      if (personId === 'Data Migration') {
        this.modifiedByPersonLabel = 'Data Migration';
      } else {
        this.modifiedByPersonLabel = '';
        this.personService.findById(personId).subscribe((person) => {
          this.person = person;
          this.modifiedByPersonLabel = this.person.name;
        });
      }
    } else {
      this.modifiedByPersonLabel = 'Unknown';
    }
  }

  onHidden(): void {
    this.isActive = false;
  }
  onShown(): void {
    this.isActive = true;
    this.loadRevisionList();
  }

  loadRevisionList(): void {
    if (this.isActive && this.entity) {
      this.revisionService
        .findRevisionList(this.entity.id)
        .subscribe((revisionList) => {
          this.revisionList = revisionList;
          this.revisionIdx = revisionList.revisions.length - 1;
        });
    }
  }

  onNextRevision(direction: number) {
    let nextRevisionIdx = this.revisionIdx;
    if (this.revisionList && this.revisionList.revisions) {
      nextRevisionIdx += direction;
      if (nextRevisionIdx < 0) {
        nextRevisionIdx = 0;
      }

      if (nextRevisionIdx >= this.revisionList.revisions.length) {
        nextRevisionIdx = this.revisionList.revisions.length - 1;
      }

      this.revisionIdx = nextRevisionIdx;
    }
  }

  isPreviousBtnDisabled(): boolean {
    return (
      this.revisionList && this.revisionList.revisions && this.revisionIdx === 0
    );
  }

  isNextBtnDisabled(): boolean {
    return (
      this.revisionList &&
      this.revisionList.revisions &&
      this.revisionIdx === this.revisionList.revisions.length - 1
    );
  }
}
