import { Directive, Input, TemplateRef, ViewContainerRef } from '@angular/core';
import { AuthenticationService } from '../service/authentication.service';

@Directive({ selector: '[appPermission]' })
export class IfPermissionDirective {
  hasView = false;

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private authenticationService: AuthenticationService
  ) {}

  @Input() set appPermission(permission: string) {
    // Subscribe to the list of permissions and enable/disable the component
    // as necessary.
    this.authenticationService.getPermissions().subscribe((permissionSet) => {
      const hasPermission = permissionSet.indexOf(permission) > -1;
      if (hasPermission && !this.hasView) {
        this.viewContainer.createEmbeddedView(this.templateRef);
        this.hasView = true;
      } else if (!hasPermission && this.hasView) {
        this.viewContainer.clear();
        this.hasView = false;
      }
    });
  }
}
