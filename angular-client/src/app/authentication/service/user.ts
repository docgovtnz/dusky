export class User {
  personId: number;
  name: string;
  token: string;
  authorities: string[];

  hasRole(roleName: string): boolean {
    return this.authorities && this.authorities.indexOf(roleName) > -1;
  }
}
