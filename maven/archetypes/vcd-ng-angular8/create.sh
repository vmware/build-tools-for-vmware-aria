#!/usr/bin/env bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.vcd.archetypes \
    -DarchetypeArtifactId=package-vcd-ng-angular8-archetype \
    -DgroupId=org.example \
    -DartifactId=sample \
    -DlicenseUrl= \
    -DlicenseHeader= \
    -DlicenseTechnicalPreview=false
