export class LoginRequestEvent {
  constructor(public targetUrl: string, public optionalMessage?: string) {}
}
