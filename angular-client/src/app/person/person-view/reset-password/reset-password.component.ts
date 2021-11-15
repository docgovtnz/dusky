import {
  Component,
  EventEmitter,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { ModalDirective } from 'ngx-bootstrap/modal';
import { CredentialService } from '../../../authentication/credential.service';
import { ResetPasswordRequest } from '../../../authentication/reset-password-request';
import { ResetPasswordResponse } from '../../../authentication/reset-password-response';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
})
export class ResetPasswordComponent implements OnInit {
  @ViewChild('resetPasswordModal', { static: true })
  public resetPasswordModal: ModalDirective;

  @Output()
  resetPasswordEvent = new EventEmitter();

  userName: string;

  resetPasswordResponse: ResetPasswordResponse;

  constructor(private credentialService: CredentialService) {}

  ngOnInit() {}

  startReset(userName: string) {
    this.resetPasswordResponse = null;
    this.userName = userName;
    this.resetPasswordModal.show();
  }

  startNewPassword(userName: string) {
    this.userName = userName;
    this.onConfirmResetPassword(true);
  }

  onConfirmResetPassword(showModal: boolean = false) {
    const resetPasswordRequest = new ResetPasswordRequest(this.userName);
    this.credentialService
      .resetPassword(resetPasswordRequest)
      .subscribe((response) => {
        this.resetPasswordResponse = response;
        this.resetPasswordEvent.next('ResetComplete');
        if (showModal) {
          this.resetPasswordModal.show();
        }
      });
  }

  onCancelResetPassword() {
    this.resetPasswordResponse = null;
    this.resetPasswordModal.hide();
  }

  onDismissOk() {
    this.resetPasswordResponse = null;
    this.resetPasswordModal.hide();
  }
}
