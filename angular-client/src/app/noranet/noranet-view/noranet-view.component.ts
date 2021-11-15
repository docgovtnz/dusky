import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NoraNetEntity } from '../entities/noranet.entity';
import { NoraNetService } from '../noranet.service';
import { DeleteByIdCheckDto } from '../../domain/response/delete-by-id-check-dto';
import { RecordEntity } from '../../domain/record.entity';

@Component({
  selector: 'app-noranet-view',
  templateUrl: 'noranet-view.component.html',
})
export class NoraNetViewComponent implements OnInit {
  noranetEntity: NoraNetEntity;
  deleteCheck: DeleteByIdCheckDto;

  // the current revision displayed (may be the current entity or an older revision)
  revision: NoraNetEntity;

  eggRecordList: RecordEntity[];
  chickRecordList: RecordEntity[];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: NoraNetService
  ) {}

  ngOnInit(): void {
    this.service.findById(this.route.snapshot.params.id).subscribe((entity) => {
      this.noranetEntity = entity;
      this.revision = this.noranetEntity;

      this.loadDeleteCheck();
    });
  }

  loadDeleteCheck() {
    this.service
      .deleteByIdCheck(this.noranetEntity.id)
      .subscribe((deleteCheck) => (this.deleteCheck = deleteCheck));
  }

  isDeleteOk() {
    return this.deleteCheck && this.deleteCheck.deleteOk;
  }

  onEdit() {
    this.router.navigate(['/noranet/edit/' + this.noranetEntity.id]);
  }

  onDelete() {
    this.service.delete(this.noranetEntity.id).subscribe(() => {
      this.router.navigate(['/noranet']);
    });
  }

  onRevisionEvent(revision) {
    this.revision = revision;
  }
}
