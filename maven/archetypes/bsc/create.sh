#!/usr/bin/env bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.bsc.archetypes \
    -DarchetypeArtifactId=package-bsc-archetype \
    -DgroupId=org.example \
    -DartifactId=sample
