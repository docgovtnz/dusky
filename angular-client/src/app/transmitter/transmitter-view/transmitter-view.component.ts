import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { TransmitterEntity } from '../../domain/transmitter.entity';
import { TransmitterService } from '../transmitter.service';
import { DeleteByIdCheckDto } from '../../domain/response/delete-by-id-check-dto';

@Component({
  selector: 'app-transmitter-view',
  templateUrl: 'transmitter-view.component.html',
})
export class TransmitterViewComponent implements OnInit {
  transmitterEntity: TransmitterEntity;
  deleteCheck: DeleteByIdCheckDto;

  // the current revision displayed (may be the current entity or an older revision)
  revision: TransmitterEntity;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: TransmitterService
  ) {
    this.transmitterEntity = new TransmitterEntity();
  }

  ngOnInit(): void {
    this.service.findById(this.route.snapshot.params.id).subscribe((entity) => {
      this.transmitterEntity = entity;
      // start by using the current transmitter as the current revision
      this.revision = entity;
      this.loadDeleteCheck();
    });
  }

  loadDeleteCheck() {
    this.service
      .deleteByIdCheck(this.transmitterEntity.id)
      .subscribe((deleteCheck) => (this.deleteCheck = deleteCheck));
  }

  isDeleteOk() {
    return this.deleteCheck && this.deleteCheck.deleteOk;
  }

  onEdit() {
    this.router.navigate(['/transmitter/edit/' + this.transmitterEntity.id]);
  }

  onDelete() {
    this.service.delete(this.transmitterEntity.id).subscribe(() => {
      this.router.navigate(['/transmitter']);
    });
  }

  onRevisionEvent(revision) {
    this.revision = revision;
  }
}
