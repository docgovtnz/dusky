import { Observable } from 'rxjs';

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { ChangePasswordRequest } from './change-password-request';
import { ResetPasswordRequest } from './reset-password-request';
import { ResetPasswordResponse } from './reset-password-response';

@Injectable()
export class CredentialService {
  constructor(private http: HttpClient) {}

  public changePassword(
    changePasswordRequest: ChangePasswordRequest
  ): Observable<any> {
    return this.http.post<any>(
      '/api/credential/changePassword',
      changePasswordRequest
    );
  }

  public resetPassword(
    resetPasswordRequest: ResetPasswordRequest
  ): Observable<ResetPasswordResponse> {
    return this.http.post<any>(
      '/api/credential/resetPassword',
      resetPasswordRequest
    );
  }
}
