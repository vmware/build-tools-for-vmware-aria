#!/usr/bin/env bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.vcfa-all-apps.archetypes \
    -DarchetypeArtifactId=package-vcfa-all-apps-archetype \
    -DarchetypeVersion=4.x.y \ 
    -DgroupId=org.example \
    -DartifactId=sample \
    -DlicenseUrl= \
    -DlicenseHeader= \
    -DlicenseTechnicalPreview=false



