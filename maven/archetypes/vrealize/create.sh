#!/usr/bin/env bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.o11n.archetypes \
    -DarchetypeArtifactId=package-vrealize-archetype \
    -DgroupId=local.corp.devops.common \
    -DartifactId=example \
    -DworkflowsPath=integration-service-1/workflows \
    -DlicenseUrl= \
    -DlicenseHeader= \
    -DlicenseTechnicalPreview=false
