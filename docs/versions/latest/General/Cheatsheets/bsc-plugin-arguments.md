com.vmware.pscoe.maven.plugins:bsc-package-maven-plugin:4.6.1-SNAPSHOT

Name: bsc-package-maven-plugin
Description: Build Tools for VMware Aria provides development and release
  management tools for implementing automation solutions based on the VMware
  Aria Suite and VMware Cloud Director. The solution enables Virtual
  Infrastructure Administrators and Automation Developers to use standard
  DevOps practices for managing and deploying content.
Group Id: com.vmware.pscoe.maven.plugins
Artifact Id: bsc-package-maven-plugin
Version: 4.6.1-SNAPSHOT
Goal Prefix: bsc

This plugin has 1 goal:

bsc:package
  Description: (no description available)
  Implementation: com.vmware.pscoe.maven.plugins.BasicPackageMojo
  Language: java
  Bound to phase: package

  Available parameters:

    project (Default: ${project})
      Project handle.

    vroIgnoreFile (Default: .vroignore)
      User property: vroIgnoreFile
      name of the vRO ignore file. Default is ".vroignore"

