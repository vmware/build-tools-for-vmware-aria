#!/usr/bin/env bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.vcf-auto-modern.archetypes \
    -DarchetypeArtifactId=package-vcf-auto-modern-archetype \
    -DarchetypeVersion=4.x.y \ 
    -DgroupId=org.example \
    -DartifactId=sample \
    -DlicenseUrl= \
    -DlicenseHeader= \
    -DlicenseTechnicalPreview=false



