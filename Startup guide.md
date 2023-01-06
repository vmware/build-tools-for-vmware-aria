# Setup development environment
./doc/markdown/setup-workstation.md

# Starting work on a task
Whatever is done should have a corresponding JIRA task with proper description and release version. All the changes and progress on a task has to be tracked under the corresponding JIRA ticket.
Documentation is key and has to be updated in corresponding markdown files in the project under /toolchain/doc/markdown/ .

# Project structure
**/toolchain/common/artifact-manager/src/main/java/com/vmware/artifact/  AND /toolchain/common/artifact-manager/src/main/java/com/vmware/pscoe/iac/artifact/**
Contains Java classes regarding all the supported objects that operations can be applied on. 
*cli*
*configuration*
*extensions*
*model*
*rest*
*ssh*
*store*
*strategy*
*utils*

**/toolchain/common/artifact-manager/src/test/**
Java unit tests.

**/toolchain/doc/**
Markdown files containing all the documentation of the modules and how they are used.

**/toolchain/maven/**
Maven plugins, goals, and MOJOs for Build Tools for VMware Aria (toolchain).
A MOJO is an executable goal in Maven, and a plugin is a distribution of one or more related MOJOs
*By convention, core Maven plugins provided by Maven are named as maven-<taskName>-plugin. Maven encourages custom plugins to be named as <taskName>-maven-plugin. Note that using maven-<taskName>-plugin pattern is an infringement of the Apache Maven Trademark.*

**/toolchain/package-installer/**
Java files for the package-installer part of Build Tools for VMware Aria.
Installer.java controls the entire process
Validation.java util class used for validation

**/toolchain/typescript/**
Corresponding with the Java folders, but for typescript projects.

# Building and testing

Do mvn install (check readme files in toolchain project to see how to compile the entire project in details).
If you have small changes after the first compilation you can do installs on specific folders mvn install -f /path/to/changed/files
Start maven in debug mode, e.g.
*mvndebug vra-ng:pu;; -P{profileName} --offline*
*mvndebug vrealize:push -P{profileName} --offline*


