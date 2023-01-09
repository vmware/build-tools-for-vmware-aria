# v2.25.2

## **Breaking Changes**
**NONE**

## Deprecations
**NONE**
 
## Features:
**NONE**

## Improvements

### *Polyglot packages can now be deployed to vRO as standalone artifacts*
You can execute mvn clean package vrealize:push -Pprofile within your polyglot project to deploy the package and
the corresponding action to a remote vRO server.

#### Previous Behaviour
Deploying Polyglot packages was only possible as dependency to other project types like typescript, vra-ng, etc.

#### New Behaviour
Deploying Polyglot packages is possible as standalone projects by directly executing the push command from the project
root.

#### Relevant Documentation:
*NONE*


### *Build process fails with during test phase execution on Windows*
Compiling the toolchain on Windows OS the compilation process of the vropkg
component fails during package signature. The reason is the EOL of Windows,
the code is always using '\r\n' EOL.

#### Previous Behavior
The error message thrown is very ambiguous because talks about issues with the certificate.

```log
Error: Cannot read X.509 certificate. ASN.1 object is not an X509v3 Certificate.
    at Object.pki.certificateFromAsn1 (C:\Users\acastroman\Documents\Projects\toolchain\typescript\vropkg\node_modules\node-forge\lib\x509.js:1280:17)
    at Object.pki.certificateFromPem (C:\Users\acastroman\Documents\Projects\toolchain\typescript\vropkg\node_modules\node-forge\lib\x509.js:727:14)
    at getSubject (C:\Users\acastroman\Documents\Projects\toolchain\typescript\vropkg\dist\security.js:73:23)
    at C:\Users\acastroman\Documents\Projects\toolchain\typescript\vropkg\dist\security.js:20:32
    at Array.forEach (<anonymous>)
    at Object.loadCertificate (C:\Users\acastroman\Documents\Projects\toolchain\typescript\vropkg\dist\security.js:19:15)
    at C:\Users\acastroman\Documents\Projects\toolchain\typescript\vropkg\dist\cli.js:119:42
    at Generator.next (<anonymous>)
    at fulfilled (C:\Users\acastroman\Documents\Projects\toolchain\typescript\vropkg\dist\cli.js:5:58)
    at processTicksAndRejections (node:internal/process/task_queues:96:5) {
  errors: [ '[Certificate] Expected type "16", got "5"' ]
}

```

#### New Behavior
The error was fixed. The challenge was to identify the correct EOL, to do this
the first line of the *.pem certificate is read and the EOL is extracted.
In case that the EOL can not be detected, the default EOL of the OS (Windows, Mac, Linux)
will be used.

#### Relevant Documentation:
*NONE*

### *Action name length limitation in vRO*
Importing actions with long paths results would fail in a hard to detect way.

#### Previous Behavior
The error message thrown used to be very ambiguous

```log
{"status":400,"message":"java.lang.RuntimeException: ch.dunes.util.DunesServerException: 
Unknown object type 'Module' caused by: ch.dunes.util.DunesServerException: Unknown object type
 'Module' caused by: Unknown object type 'Module'"}
```

#### New Behavior
The message has changed to a more easy to understand one:

```log
One of the actions that we tried to import contains a path that is too long.
```

### *Static version in pom.xml file inside package-installer*
The compilation process is affected because the package-installer component
is not updating all the Maven dependencies when a release process is in progress.

#### Previous Behavior
The version of the dependency **artifact-manager** is always the same '2.22.2-SNAPSHOT'
inside package-installer component

#### New Behavior
The file release.sh was modified to update the dependency list to the last available
including the **artifact-manager** dependency inside package-installer component.

#### Relevant Documentation:
*NONE*



### *vRO Scripting API ConfigurationElement.getAttributeWithKey now returns null if an attribute is missing*

#### Previous Behavior
When using ConfigurationElement.getAttributeWithKey inside of tests, if the attribute did not exist, `undefined` would be returned

#### New Behavior
ConfigurationElement.getAttributeWithKey now checks if the attibute is set first. If it is not set, `null` will be returned,
instead of `undefined`.

#### Relevant Documentation:
*NONE*



### *vRO Scripting API Properties.get now returns null if a property is missing*

#### Previous Behavior
When using Properties.get inside of tests, if the key did not exist, `undefined` would be returned

#### New Behavior
Properties.get now do a check with `.hasOwnProperty` to determine if that key is set first. If it is not set, `null` 
will be returned, instead of `undefined`.

#### Relevant Documentation:
*NONE*



### *Removed certificates from vropkg testing*
Static certificate files as well as a `*.package` file where present in `vropkg` testing suite.

#### Previous Behavior
Static certificate files were used for testing.

#### New Behavior
The certificates have been removed and logic has been added to generate them dynamically.

#### Relevant Documentation:
*NONE*



### *Storage Profiles now can distinguish First Class Disk*
When pulling Storage Profile json now `diskType` is recorded if the disk is of type `firstClass`. If the disk is indeed `firstClass` then the property `diskMode` is omitted in order to enable pushing the content since `diskMode` is not permitted when creating/updating First Class Disks in the payload.

#### Previous Behaviour
Storage Profile json doesn't record `diskType` and cannot distinguish regular disks from First Class Disks and `diskMode` is always in the json.

#### New Behaviour
Storage Profile json now records `diskType` and can distinguish regular disks from First Class Disks and `diskMode` is written in the json only if the disk is not of `diskType` equal to `firstClass`.

#### Relevant Documentation:
*NONE*



### *Tests when dependency has a policy template or other unknown types of elements now work*

#### Previous Behaviour
Having a dependency to a library with some unknown types ( like PolicyTemplates ) would cause an error like: 
```log
TypeError: Cannot read property 'type' of undefined [ERROR] at .../node_modules/@vmware-pscoe/vrotest/lib/vrotest.js:1:196681
```

#### New Behaviour
These types are now handled and ignored if they are not supported

#### Relevant Documentation:
*NONE*



### *Bumped minimal version of NodeJs from 12 to 16*
All Typescript packages have the nodejs version bumped to the latest 16 at the time ( 21.06.2022 ). This only affects the built however,
during execution the version is not checked.

#### Relevant Documentation:
*NONE*



### *vrotest code coverage issues with thresholds not being set correctly*

#### Previous Behaviour
```xml
    <test.coverage.thresholds.warn>20</test.coverage.thresholds.warn>
```
was not being respected when setting thresholds. Instead, the error threshold was always taken.

```xml
    <test.coverage.thresholds.statements.error>60</test.coverage.thresholds.statements.error>
    <test.coverage.thresholds.statements.warn>70</test.coverage.thresholds.statements.warn>
```
Statements were overwriting the branches code coverage, at least for the global.

#### New Behaviour
The threshold warn and the statements now work as expected.

#### Relevant Documentation:
*NONE*



### *Polyglot now uses new PowerShell executable*
When bundling dependencies for PowerShell scripts on Windows OS, the executable `powershell.exe` was used, which is a part of PowerShell version 5.
To utilize the new versions of PowerShell (version 6 and above), the command is updated to `pwsh`. Moreover, the time for resolving dependencies is increased to 10 minutes.

#### Previous Behaviour
Previously, when saving the dependencies for PowerShell modules using Windows OS, the `powershell.exe` command was used, which is from PowerShell version 5.xx. Additionally, the time for resolving dependencies was 1 minute.

#### New Behaviour
To utilize PowerShell version 6 and above when saving modules, the command was changed to `pwsh`, which now is the same command for Windows OS and other OSs. The result is the same as using the powershell.exe command; however, the user is required to have installed PowerShell version 6 and above on their Windows machine. The time for resolving dependencies is now increased to 10 minutes.

#### Relevant Documentation:
*NONE*



## Upgrade procedure:
*NONE*
