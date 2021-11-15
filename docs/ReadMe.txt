
Normally the application replicates over Https. There are separate documents that describe the procedure for creating
and configuration https connections.

This document describes how to create a plain http connection. Typically for a Dev/Test scenario where it's not possible
or practical to spend the time to create the https connections.

1. In the serviceconfig.json file remove the SSL entries

	"SSLCert":"C:/dusky/publickey.crt",
	"SSLKey":"C:/dusky/privatekey.key",

2. Change the following application.properties to http

sync.gateway.protocol=http
sync.gateway.protocol.internal=http


3. Restart both the SG and Spring Application


Notes:
 - You should be able to point a browser at the local machine on http://localhost:4984 and it should get a valid response something like...

 {"couchdb":"Welcome","vendor":{"name":"Couchbase Sync Gateway","version":"2.0"},"version":"Couchbase Sync Gateway/2.0.0(832;2d8a6c0)"}

 - If it doesn't then something has gone wrong with the SG being switched to http and you'll need to investigate that.
