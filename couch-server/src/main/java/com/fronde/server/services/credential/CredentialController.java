package com.fronde.server.services.credential;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/credential")
public class CredentialController {

  @Autowired
  protected CredentialService service;

  /**
   * No method level permission because change password contains the current password.
   *
   * @param changePasswordRequest
   */
  @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
    service.changePassword(changePasswordRequest);
  }

  /**
   * @param resetPasswordRequest
   */
  @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
  public ResetPasswordResponse resetPassword(
      @RequestBody ResetPasswordRequest resetPasswordRequest) {
    ResetPasswordResponse response = service.resetPassword(resetPasswordRequest);
    return response;
  }

}
