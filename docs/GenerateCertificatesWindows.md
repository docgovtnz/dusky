An (untested) PowerShell script can be used for Windows users wanting to generate a key for use with Sync Gateway.

```
$fqdn = [System.Net.Dns]::GetHostByName($env:computerName).HostName
$s = "& 'keytool.exe' -genkeypair -keystore ${env:COMPUTERNAME}_keystore.p12 -storetype PKCS12 -storepass Password01 -alias $env:COMPUTERNAME -keyalg RSA -keysize 2048 -validity 99999 -dname `"CN=${fqdn}`" -ext san=dns:${fqdn},dns:localhost,ip:127.0.0.1"
write-output $s
iex $s
$ks = "./${env:COMPUTERNAME}_keystore.p12"
keytool.exe -exportcert -keystore $ks -storepass Password01 -alias $env:COMPUTERNAME -rfc -file public-certificate.cer
Import-Certificate -FilePath "./public-certificate.cer" -CertStoreLocation Cert:\LocalMachine\Root

openssl pkcs12 -in .\${env:COMPUTERNAME}_keystore.p12 -nodes -nocerts -out private-key.key
```
