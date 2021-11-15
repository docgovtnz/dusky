

# WIP: some commands for recreating the database from scratch. Not really a working program at this stage, but these
# commands did work when run separately on the command line.

# Passwords have been redacted for source control.

set PATH=%PATH%;C:\Program Files\Couchbase\Server\bin

couchbase-cli cluster-init --cluster couchbase://localhost --cluster-username Administrator --cluster-password ******* --cluster-ramsize 1024 --cluster-name Laptop --cluster-index-ramsize 256 --cluster-fts-ramsize 256 --index-storage-setting default --services data,index,query,fts

couchbase-cli bucket-create --cluster couchbase://localhost --username Administrator --password ******* --bucket kakapo-bird --bucket-type couchbase --bucket-ramsize 1024 --wait

couchbase-cli user-manage -c couchbase://localhost -u Administrator -p ******** --set --rbac-username kakapo-bird --rbac-password ******** --roles admin --auth-domain local