import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NestObservationEntity } from '../../domain/nestobservation.entity';
import { NestObservationService } from '../nestobservation.service';
import { DeleteByIdCheckDto } from '../../domain/response/delete-by-id-check-dto';
import { RecordEntity } from '../../domain/record.entity';

@Component({
  selector: 'app-nestobservation-view',
  templateUrl: 'nestobservation-view.component.html',
})
export class NestObservationViewComponent implements OnInit {
  nestobservationEntity: NestObservationEntity;
  deleteCheck: DeleteByIdCheckDto;

  // the current revision displayed (may be the current entity or an older revision)
  revision: NestObservationEntity;

  eggRecordList: RecordEntity[];
  chickRecordList: RecordEntity[];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: NestObservationService
  ) {}

  ngOnInit(): void {
    this.service.findById(this.route.snapshot.params.id).subscribe((entity) => {
      this.nestobservationEntity = entity;
      this.revision = this.nestobservationEntity;

      // Load the eggs.
      this.service
        .getEggRecords(this.nestobservationEntity.id)
        .subscribe((recordList) => {
          this.eggRecordList = recordList;
        });

      // Load the chicks.
      this.service
        .getChickRecords(this.nestobservationEntity.id)
        .subscribe((recordList) => {
          this.chickRecordList = recordList;
        });

      this.loadDeleteCheck();
    });
  }

  loadDeleteCheck() {
    this.service
      .deleteByIdCheck(this.nestobservationEntity.id)
      .subscribe((deleteCheck) => (this.deleteCheck = deleteCheck));
  }

  isDeleteOk() {
    return this.deleteCheck && this.deleteCheck.deleteOk;
  }

  onEdit() {
    this.router.navigate([
      '/nestobservation/edit/' + this.nestobservationEntity.id,
    ]);
  }

  onDelete() {
    this.service.delete(this.nestobservationEntity.id).subscribe(() => {
      this.router.navigate(['/nestobservation']);
    });
  }

  onRevisionEvent(revision) {
    this.revision = revision;
  }
}
