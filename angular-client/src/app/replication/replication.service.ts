import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LocalAddress } from './local-address';
import { ConnectionRequest } from './connection-request';
import { map } from 'rxjs/operators';
import { Response } from '../domain/response/response';
import { SyncGatewayStatus } from './sync-gateway-status';

@Injectable()
export class ReplicationService {
  constructor(private http: HttpClient) {}

  getLocalAddress(): Observable<LocalAddress> {
    return this.http.get<LocalAddress>('/api/replication/localAddress');
  }

  getSyncGatewayStatus(): Observable<SyncGatewayStatus> {
    /* eslint-disable @typescript-eslint/naming-convention */
    const headers = new HttpHeaders({
      x_service_level: 'optional',
    });
    /* eslint-enable */

    return this.http
      .get<SyncGatewayStatus>('/api/replication/syncGatewayStatus', {
        headers,
      })
      .pipe(map((connectionMap: any) => connectionMap));
  }

  setMode(mode: string): Observable<Response<string>> {
    return this.http.post<Response<string>>(
      '/api/replication/setMode/' + mode,
      {}
    );
  }

  connectToNode(
    connectionRequest: ConnectionRequest
  ): Observable<Response<ConnectionRequest>> {
    return this.http.post<Response<ConnectionRequest>>(
      '/api/replication/connectToNode',
      connectionRequest
    );
  }

  disconnect(connectionKey: string): Observable<void> {
    const connectionRequest = new ConnectionRequest();
    connectionRequest.connectToServer = connectionKey;
    return this.http.post<void>(
      '/api/replication/disconnect',
      connectionRequest
    );
  }

  getAllowedTargetAddresses(): Observable<string[]> {
    return this.http.get<string[]>('/api/replication/allowedTargetAddresses');
  }
}
