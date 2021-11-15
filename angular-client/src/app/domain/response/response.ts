import { AbstractResponse } from './abstract-response';

export class Response<T> extends AbstractResponse {
  public model: T;
}
