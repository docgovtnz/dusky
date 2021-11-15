import { Component, OnInit } from '@angular/core';
import { UpgradeService } from './upgrade.service';
import { FileMetaDataList } from './file-meta-data-list';
import { ReplicationService } from '../replication/replication.service';
import { AuthenticationService } from '../authentication/service/authentication.service';

@Component({
  selector: 'app-upgrade',
  templateUrl: './upgrade.component.html',
})
export class UpgradeComponent implements OnInit {
  fileMetaDataList: FileMetaDataList;

  initialized = false;
  cloudServerReachable = false;
  upgradeRequired = false;
  downloadRequired = false;
  upgradeEnabled = false;

  upgradeResult: any;

  serverMode = 'Unknown';

  constructor(
    private upgradeService: UpgradeService,
    private authenticationService: AuthenticationService,
    private replicationService: ReplicationService
  ) {}

  ngOnInit() {
    this.replicationService.getSyncGatewayStatus().subscribe((status) => {
      this.serverMode = status.serverMode;
      if (status.serverMode !== 'Server') {
        this.downloadFileList();
      }
    });
  }

  downloadFileList() {
    this.upgradeService.downloadFileList().subscribe((fileList) => {
      this.fileMetaDataList = fileList;

      if (this.fileMetaDataList.cloudVersion) {
        this.cloudServerReachable = true;
        if (
          this.fileMetaDataList.cloudVersion !==
          this.fileMetaDataList.localVersion
        ) {
          this.upgradeRequired = true;
        }

        let nextDownloadRequired = false;
        for (const fileMetaData of this.fileMetaDataList.list) {
          if (fileMetaData.status !== 'Ok') {
            nextDownloadRequired = true;
          }
        }
        this.downloadRequired = nextDownloadRequired;
      } else {
        this.cloudServerReachable = false;
      }

      this.upgradeEnabled = !this.downloadRequired;
      this.initialized = true;
    });
  }

  onDownload() {
    this.upgradeService.downloadFiles().subscribe((result) => {
      console.log('downloadFiles() ' + result.result);
      this.downloadFileList();
    });
  }

  onUpgrade() {
    this.upgradeService.upgradeRelease().subscribe((result) => {
      console.log('upgradeRelease() ' + result.result);
      this.upgradeResult = result;
      if (result && result.result && result.result.indexOf('success') > 0) {
        setTimeout(() => {
          this.onRestartCheck();
        }, 10000);
      }
    });
  }

  onReset() {
    this.upgradeService.resetFiles().subscribe((result) => {
      console.log('resetFiles() ' + result.result);
      this.downloadFileList();
    });
  }

  onClosed() {
    this.upgradeResult = null;
  }

  onRestartCheck() {
    this.upgradeService.restartCheck().subscribe(
      (result) => {
        console.log('Restart OK: ' + JSON.stringify(result));
        this.authenticationService.onUserLogout();
      },
      (err) => {
        console.log('Restart Failed: ' + JSON.stringify(err));
      }
    );
  }

  getAlertType() {
    let alertType = 'danger';
    if (
      this.upgradeResult &&
      this.upgradeResult.result &&
      this.upgradeResult.result.indexOf('success') >= 0
    ) {
      alertType = 'success';
    }
    return alertType;
  }
}
