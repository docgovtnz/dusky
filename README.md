# Kākāpo Database (a.k.a. Dusky)

## Resources

* Project background (public facing): https://docgovtnz.github.io/docs/projects/dusky

## Setup

It is advisible to read through the whole README before commencing as there are a few potential gotchas that it might be useful to know about in advance of starting the process.

### Prerequisites

**Note: use the specific versions of the software outlined below, as later versions presently have compatibility issues.**

* Java 11 <https://jdk.java.net/> or <https://www.oracle.com/technetwork/java/javase/downloads/index.html>
* Couchbase Community 6.0.0 [Windows] <https://packages.couchbase.com/releases/6.0.0/couchbase-server-community_6.0.0-windows_amd64.msi>
* Couchbase Sync Gateway Community 2.0 [Windows] <https://packages.couchbase.com/releases/couchbase-sync-gateway/2.0.0/couchbase-sync-gateway-community_2.0.0_x86_64.msi>
* *Optional:* IntelliJ IDEA (community version available) <https://www.jetbrains.com/idea/download/download-thanks.html?platform=windows&code=IIC>

### Setup IDE (optional)

1. Checkout code and import into IntelliJ
   * Project from Existing Sources
   * Gradle, Auto Import, Add Java JDK as Project SDK (if required)
2. Configure IntelliJ Project
   * Add Run Configurations (Run -> Edit Configurations)
   * Add Applications (names may not match exactly):
     * Name: `EncryptionManager`, Class (Main class): `com.fronde.dusky.EncryptionManager`, Module (Use classpath of module): `tools.main`
     * Name: `CouchServerApplication`, Class: `com.fronde.server.CouchServerApplication`, Module: `couch-server.main`
     * Name: `code-generator`, Class: `com.fronde.code.CodeGeneratorApplication`, Module: `code-generator.main`
   * Add npm
     * Name: `angular-client`, Script: `start`
   * Add Javascript Debug
     * Name: `localhost:4200`, URL: `https://localhost:4200`

### Install Couchbase and Sync Gateway

1. Follow prompts during install of Couchbase Server and Couchbase Sync Gateway. Leave install locations as defaults.
2. After installation go to http://localhost:8091/ and set the Administrator password to `Password01`.
3. Customise the settings to allocate 1024 MB memory to the Data Service. This can be changed later if necessary.

### Configuration

In order to get a copy of data to work with, Couchbase is configured to synchronise with the Shared Developer Environment (https://dev.<domain>).

*NB: you need a hostname in DNS. If you don't have one (e.g. using a Mac), then you'll have to obtain one first (that is, getting fixed IP and DNS entry).*

#### Create and trust a self-signed SSL certificate

*Instructions adapted from [source](https://gist.github.com/Eng-Fouad/6cdc8263068700ade87e4e3bf459a988).*

This can be achieved using `keytool` or asking DOC (TODO: who?) to supply this for your local machine. Note that there is a PowerShell script in `docs/GenerateCertificatesWindows.md` that may be able to automate this process for Windows users.

Replace <HOSTNAME> with your hostname and <FQDN> with your full DNS-qualified hostname in the instructions below.

Create a keystore in a p12 format:
```
keytool -genkeypair -keystore <HOSTNAME>_keystore.p12 -storetype PKCS12 -storepass Password01 -alias <HOSTNAME> -keyalg RSA -keysize 2048 -validity 99999 -dname "CN=<FQDN>" -ext san=dns:<FQDN>,dns:localhost,ip:127.0.0.1
```

Export the public certificate from the keystore:
```
keytool -exportcert -keystore ./<HOSTNAME>_keystore.p12 -storepass Password01 -alias <HOSTNAME> -rfc -file public-certificate.cer
```

Trust the public certificate you exported by double clicking on it then following:

* Install Certificate
* Local Machine
* Place all certificates…
* Trusted Root Certification Authorities
* Next, Finish, etc.

If you're having problems synchronising over HTTPS, you can change the Sync Gateway to use straight HTTP for the purposes of development.

### Set up data replication

Add your host as Data Replication user to <https://dev.<domain>> following instructions at <https://departmentofconservation.atlassian.net/wiki/spaces/KP/pages/250445843/Data+Replication+accounts>.

Once you've obtained the encrypted key (`encrypted(…)`), you can then decrypt it through either:

* Running EncryptionManager via IntelliJ and following the prompts. The encryption key is in LastPass (or ask a developer).
* Manually running the tool: `java ./EncryptionManager.java` found in the tools directory.

Place the key in plain text in `couch-server/replication.properties`:
`replication.password=<password>`

### Start Dusky (first time)

Assuming you now have Couchbase installed and configured, and the Sync Gateway installed, you should be able to start Dusky in IntelliJ or by using: `./gradlew couch-server:bootRun`. Note that if you are using IntelliJ you should ensure you are running it as an Administrator, as the Java Dusky app does some system configuration to make the Sync Gateway work. If the process falls over, check the 'Potential gotchas' later in this README.

Dusky tries to be smart and configure Sync Gateway automatically, but it expects files to be in certain places. See the relevant `sync.gateway.` properties in  `application.properties`.

You can keep an eye on the Dusky back-end in the file `couch-server/logs/Dusky.txt.log` (e.g. using `tail -f`).

The front-end can be started using the commands outlined below in 'Running'.

*NB: passwords are stored in `couch-server/generated.properties`, in production these should be generated encrypted passwords*


### Potential gotchas

* Ensure that the environment variable `COMPUTERNAME` is set to the (short) hostname of your machine. This is done by default on Windows, but on \*nix systems it must be set manually by `` export COMPUTERNAME=`hostname` ``.
* Check that your network hostname actually resolves to your local machine. By default Dusky will try and talk to your local instance of Sync Gateway via your network hostname (see `sync.gateway.host` in `application.properties`). If this doesn't resolve, then either update your `hosts` file or update the `application.properties` file to point at `localhost`.
* Dusky sometimes expects files to be in certain places (e.g. C:\Dusky or C:\DuskyMaps). If you're finding things aren't working, particularly with the automatic configuration of Sync Gateway, have a look around the source code for some clarification.
* The `generated.properties` file supports encrypted properties, however the `application.properties` file does not.
* Encrypted values must be surrounded by brackets. For example: `couchbase.bucket.password=encrypted(<encrypted value>)`
* If you're receiving errors relating to `lint-staged`, ensure you are running at least version 7 of NPM and that you have run `npm install`, even if you're only working on the Java code
* Creating a the certificate stuff requires that the key alias specified in application.properties matches the friendly name of the generated key

## Debugging

If you're having problems launching `couch-server` (for example, processes finishing with non-zero exit values), have a look at the `couch-server/logs/` directory of this repository.

Sync Gateway errors will be in `sync_gateway_error.log` found in `C:\Program Files\Couchbase\Sync Gateway\var\lib\couchbase\logs\` on a Windows installation.


## Running

You can run the back-end and/or front-end via IntelliJ if it has been configured appropriately as above.
If you wish to run the components via the command line:

* *Front-end*: from the `angular-client` directory run `npm start` (assuming that `npm install` has already been run)
* *Back-end*: from the repository root run `./gradlew couch-server:bootRun`


## Testing

The testing process previously used Katalon Studio but this is currently under review.
New Angular code should be checked via `ng lint` but there are still many historical linter errors from the original project development era, some of which have been ignored via eslint configuration.
New JS code is automatically linted and prettier-ified.


## Building

Before running ensure that the `couch-server/src/main/resources/application.properties` file has been updated to the relevant new version number. This should be committed to Git.

Assuming you have the relevant encryption password, Dusky can then be built for the various environments using the command:
`./gradlew -PencryptionPassword="<encryption password>" clean :couch-server:packageDistribution`

Once the script completes, you will find the compiled files at `couch-server/build/dist/`.


## Deploying

There are three hosted environments:

* Dev: Essentially a staging environment, the first place to deploy and trial code
* Test: Essentially a UAT environment, also to be used for end-user testing
* Production: Main source of data to which the laptop clients sync

The current deploy process consists of sending the relevant `.zip` file compiled as per the instructions found above to the relevant person within DOC Production Support.
Instructions for the deploy process (including project management aspects) are visible on Confluence (titled 'Deployment Process')


## Licence

Dusky (Kākāpō Database)  
Copyright (C) 2021 Department of Conservation | Te Papa Atawhai, Fronde Systems Group Limited, Pikselin Limited

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
