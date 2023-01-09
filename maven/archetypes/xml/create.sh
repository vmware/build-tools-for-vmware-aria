#!/usr/bin/env bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.o11n.archetypes \
    -DarchetypeArtifactId=package-xml-archetype \
    -DgroupId=local.corp.devops.common \
    -DartifactId=example \
    -DworkflowsPath=Corporation/Common/Example \
    -DlicenseUrl= \
    -DlicenseHeader= \
    -DlicenseTechnicalPreview=false