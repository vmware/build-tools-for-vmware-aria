/*
 * #%L
 * o11n-actions-package-maven-plugin
 * %%
 * Copyright (C) 2023 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.  
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */
File packageFile = new File( basedir, "target/org.example.basic-1.0.0.package" )
assert packageFile.exists()
def zip = new java.util.zip.ZipFile(packageFile)

def actionInfoEntry = zip.getEntry("elements/f91c43b5-98fe-3e81-9a7f-c1693c384570/info")
assert  actionInfoEntry != null
def entries = zip.entries().iterator()
assert entries.count{ it.name.startsWith("elements/") && it.name.endsWith("/info") } == 1

def slurper = new groovy.util.XmlSlurper()
slurper.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
slurper.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)

def dunesMetaInf = slurper.parse(zip.getInputStream(zip.getEntry("dunes-meta-inf")))
def pkgName = dunesMetaInf.'*'.find { node -> node.@key == 'pkg-name' }

assert pkgName.text() == 'org.example.basic-1.0.0'