import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { IslandEntity } from '../../../domain/island.entity';
import { IslandService } from '../island.service';
import { DeleteByIdCheckDto } from '../../../domain/response/delete-by-id-check-dto';
import { SettingService } from '../../../setting/setting.service';

@Component({
  selector: 'app-island-view',
  templateUrl: 'island-view.component.html',
})
export class IslandViewComponent implements OnInit {
  islandEntity: IslandEntity;
  deleteCheck: DeleteByIdCheckDto;
  isAuthorized = false;

  // the current revision displayed (may be the current entity or an older revision)
  revision: IslandEntity;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: IslandService,
    private settingService: SettingService
  ) {}

  ngOnInit(): void {
    this.service.findById(this.route.snapshot.params.id).subscribe((entity) => {
      this.islandEntity = entity;
      // start by using the current island as the current revision
      this.revision = entity;
      this.loadDeleteCheck();
      this.isAuthorized = this.settingService.isAuthorizedToAdd();
    });
  }

  loadDeleteCheck() {
    this.service
      .deleteByIdCheck(this.islandEntity.id)
      .subscribe((deleteCheck) => (this.deleteCheck = deleteCheck));
  }

  isDeleteOk() {
    return this.deleteCheck && this.deleteCheck.deleteOk;
  }

  onEdit() {
    this.router.navigate(['/settings/island/edit/' + this.islandEntity.id]);
  }

  onDelete() {
    this.service.delete(this.islandEntity.id).subscribe(() => {
      this.router.navigate(['/settings/island']);
    });
  }

  onRevisionEvent(revision) {
    this.revision = revision;
  }
}
