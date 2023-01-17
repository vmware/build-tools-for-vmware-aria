#!/usr/bin/env bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.polyglot.archetypes \
    -DarchetypeArtifactId=package-polyglot-archetype \
    -DgroupId=local.corp.devops.common \
    -DartifactId=example \
    -Druntime=nodejs \
    -Dtype=vro \
    -DlicenseUrl= \
    -DlicenseHeader= \
    -DlicenseTechnicalPreview=false