import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { TxMortalityEntity } from '../../../domain/txmortality.entity';
import { TxMortalityService } from '../txmortality.service';
import { DeleteByIdCheckDto } from '../../../domain/response/delete-by-id-check-dto';
import { SettingService } from '../../../setting/setting.service';

@Component({
  selector: 'app-txmortality-view',
  templateUrl: 'txmortality-view.component.html',
})
export class TxMortalityViewComponent implements OnInit {
  txmortalityEntity: TxMortalityEntity;
  deleteCheck: DeleteByIdCheckDto;

  // the current revision displayed (may be the current entity or an older revision)
  revision: TxMortalityEntity;
  isAuthorized = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: TxMortalityService,
    private settingService: SettingService
  ) {}

  ngOnInit(): void {
    this.service.findById(this.route.snapshot.params.id).subscribe((entity) => {
      this.txmortalityEntity = entity;
      // start by using the current txmortality as the current revision
      this.revision = entity;
      this.loadDeleteCheck();
      this.isAuthorized = this.settingService.isAuthorizedToAdd();
    });
  }

  loadDeleteCheck() {
    this.service
      .deleteByIdCheck(this.txmortalityEntity.id)
      .subscribe((deleteCheck) => (this.deleteCheck = deleteCheck));
  }

  isDeleteOk() {
    return this.deleteCheck && this.deleteCheck.deleteOk;
  }

  onEdit() {
    this.router.navigate([
      '/settings/txmortality/edit/' + this.txmortalityEntity.id,
    ]);
  }

  onDelete() {
    this.service.delete(this.txmortalityEntity.id).subscribe(() => {
      this.router.navigate(['/settings/txmortality']);
    });
  }

  onRevisionEvent(revision) {
    this.revision = revision;
  }
}
