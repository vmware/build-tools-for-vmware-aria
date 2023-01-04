#!/usr/bin/env bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.cs.archetypes \
    -DarchetypeArtifactId=package-cs-archetype \
    -DgroupId=local.corp.devops.common \
    -DartifactId=example

