import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { SnarkActivityEntity } from '../../domain/snarkactivity.entity';
import { SnarkActivityService } from '../snarkactivity.service';
import { DeleteByIdCheckDto } from '../../domain/response/delete-by-id-check-dto';

@Component({
  selector: 'app-snarkactivity-view',
  templateUrl: 'snarkactivity-view.component.html',
})
export class SnarkActivityViewComponent implements OnInit {
  snarkactivityEntity: SnarkActivityEntity;
  deleteCheck: DeleteByIdCheckDto;

  // the current revision displayed (may be the current entity or an older revision)
  revision: SnarkActivityEntity;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: SnarkActivityService
  ) {}

  ngOnInit(): void {
    this.service.findById(this.route.snapshot.params.id).subscribe((entity) => {
      this.snarkactivityEntity = entity;
      // start by using the current snarkactivity as the current revision
      this.revision = entity;
      this.loadDeleteCheck();
    });
  }

  loadDeleteCheck() {
    this.service
      .deleteByIdCheck(this.snarkactivityEntity.id)
      .subscribe((deleteCheck) => (this.deleteCheck = deleteCheck));
  }

  isDeleteOk() {
    return this.deleteCheck && this.deleteCheck.deleteOk;
  }

  onEdit() {
    this.router.navigate([
      '/snarkactivity/edit/' + this.snarkactivityEntity.id,
    ]);
  }

  onDelete() {
    this.service.delete(this.snarkactivityEntity.id).subscribe(() => {
      this.router.navigate(['/snarkactivity']);
    });
  }

  onRevisionEvent(revision) {
    this.revision = revision;
  }
}
