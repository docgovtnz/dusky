import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { SampleEntity } from '../../domain/sample.entity';
import { SampleService } from '../sample.service';
import { DeleteByIdCheckDto } from '../../domain/response/delete-by-id-check-dto';
import { RecordEntity } from '../../domain/record.entity';
import { TestStatsDto } from '../test-stats-dto';

@Component({
  selector: 'app-sample-view',
  templateUrl: 'sample-view.component.html',
})
export class SampleViewComponent implements OnInit {
  sampleEntity: SampleEntity;
  deleteCheck: DeleteByIdCheckDto;

  // the current revision displayed (may be the current entity or an older revision)
  revision: SampleEntity;

  recordEntity: RecordEntity;

  haematologyTestsStats: TestStatsDto[];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: SampleService
  ) {}

  ngOnInit(): void {
    this.service.findById(this.route.snapshot.params.id).subscribe((entity) => {
      this.sampleEntity = entity;
      // start by using the current sample as the current revision
      this.revision = entity;
      this.loadDeleteCheck();
      this.loadRecord();
      // we have the sample type, so we can load the tests stats
      this.service
        .getHaematologyTestsStats(entity.sampleType)
        .subscribe((hts) => {
          this.haematologyTestsStats = hts;
        });
    });
  }

  loadDeleteCheck() {
    this.service
      .deleteByIdCheck(this.sampleEntity.id)
      .subscribe((deleteCheck) => (this.deleteCheck = deleteCheck));
  }

  loadRecord() {
    this.service.getRecord(this.sampleEntity.id).subscribe((recordEntity) => {
      this.recordEntity = recordEntity;
    });
  }

  isDeleteOk() {
    return this.deleteCheck && this.deleteCheck.deleteOk;
  }

  onEdit() {
    this.router.navigate(['/sample/edit/' + this.sampleEntity.id]);
  }

  onDelete() {
    this.service.delete(this.sampleEntity.id).subscribe(() => {
      this.router.navigate(['/sample']);
    });
  }

  onRevisionEvent(revision) {
    this.revision = revision;
  }
}
