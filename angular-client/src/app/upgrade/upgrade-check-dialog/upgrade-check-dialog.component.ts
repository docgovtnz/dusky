import { Component, OnInit, ViewChild } from '@angular/core';
import { AuthenticationService } from '../../authentication/service/authentication.service';
import { UpgradeService } from '../upgrade.service';
import { Router } from '@angular/router';
import { ModalDirective } from 'ngx-bootstrap/modal';
import { FileMetaDataList } from '../file-meta-data-list';
import { ReplicationService } from '../../replication/replication.service';

@Component({
  selector: 'app-upgrade-check-dialog',
  templateUrl: './upgrade-check-dialog.component.html',
})
export class UpgradeCheckDialogComponent implements OnInit {
  @ViewChild('upgradeCheckModal', { static: true })
  upgradeCheckModal: ModalDirective;

  fileMetaDataList: FileMetaDataList;

  constructor(
    private authenticationService: AuthenticationService,
    private upgradeService: UpgradeService,
    private replicationService: ReplicationService,
    private router: Router
  ) {}

  ngOnInit() {
    this.authenticationService.loginEvent$.subscribe((loginEvent) => {
      if (loginEvent) {
        this.replicationService.getSyncGatewayStatus().subscribe((status) => {
          if (status.serverMode !== 'Server') {
            this.performUpgradeCheck();
          }
        });
      }
    });
  }

  performUpgradeCheck() {
    this.upgradeService.downloadFileList().subscribe((fileMetaDataList) => {
      this.fileMetaDataList = fileMetaDataList;
      if (
        fileMetaDataList &&
        fileMetaDataList.cloudVersion &&
        fileMetaDataList.cloudVersion !== fileMetaDataList.localVersion
      ) {
        this.upgradeCheckModal.show();
      }
    });
  }

  onUpgrade() {
    this.upgradeCheckModal.hide();
    this.router.navigate(['/upgrade']);
  }

  onCancel() {
    this.upgradeCheckModal.hide();
  }
}
