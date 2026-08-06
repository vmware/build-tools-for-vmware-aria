#### Additional Parameters

Additional parameters that can be passed as flags to the maven command, e.g. `mvn clean package -DskipInstallNodeDeps=true`.

* `skipInstallNodeDeps` - skip the deletion and re-installation of node-deps.

!!! note
    If node_modules folder doesn't exist, then this flag is ineffective.

* `vro.forceImportLatestVersions` - This strategy will force you to upload the same or newer version of a package, otherwise it will fail the build, allowing us for better CI/CD pipelines, where we can ensure that the latest versions are always used on the server. Default value is `false`.

!!! note
    Snapshot versions are considered newer if they are the same as the version on the server.

* `vro.importOldVersions` - This strategy will upload a version of the package even if it is older than the version on the server.
!!! note
    Snapshot versions are considered newer if they are the same as the version on the server.
!!! note
    If `forceImportLatestVersions` is set to `true` this configuration is ignored.
