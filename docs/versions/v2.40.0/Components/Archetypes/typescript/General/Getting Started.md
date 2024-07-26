[//]: # (DEFAULT TEMPLATE, Used if no others match)

[//]: # (Remove Comments when you are done)
[//]: # (What is this?)

# Title

[//]: # (Additional Information on the topic goes here)

[//]: # (What will you learn)
[//]: # (Optional)

## Overview

[//]: # (Internal navigation)
[//]: # (Navigational links may have a short description after them separated by a `-`)

## Table Of Contents

1. [Properties](#properties)

### Properties

#### skipInstallNodeDeps

Add the `skipInstallNodeDeps` flag to skip the deletion and re-installation of node-deps. Ex: `mvn clean package -DskipInstallNodeDeps=true`.

Note: If node_modules folder doesn't exist, then this flag is ineffective.

#### -Dvro.forceImportLatestVersions

This strategy will force you to upload the same or newer version of a package, otherwise it will fail the build, allowing us for better CI/CD pipelines, where we can ensure that the latest versions are always used on the server.
