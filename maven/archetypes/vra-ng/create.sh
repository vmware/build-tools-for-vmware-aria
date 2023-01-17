#!/usr/bin/env bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.vra-ng.archetypes \
    -DarchetypeArtifactId=package-vra-ng-archetype \
    -DgroupId=org.example \
    -DartifactId=sample \
    -DlicenseUrl= \
    -DlicenseHeader= \
    -DlicenseTechnicalPreview=false
