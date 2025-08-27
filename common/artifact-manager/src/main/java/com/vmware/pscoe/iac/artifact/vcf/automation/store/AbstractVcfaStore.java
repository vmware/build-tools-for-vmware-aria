/*
 * #%L
 * artifact-manager
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
package com.vmware.pscoe.iac.artifact.vcf.automation.store;

import java.io.File;
import java.io.FilenameFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.vcf.automation.configuration.VcfaConfiguration;
import com.vmware.pscoe.iac.artifact.vcf.automation.rest.VcfaRestClient;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor;

/**
 * Abstract VCFA store that provides basic init semantics used by concrete
 * stores. Concrete implementations can override import/export/delete behaviour.
 */
public abstract class AbstractVcfaStore implements IVcfaStore {
    protected VcfaRestClient restClient;
    protected Package vcfaPackage;
    protected VcfaPackageDescriptor descriptor = new VcfaPackageDescriptor();
    protected VcfaConfiguration config;
    protected Logger logger;

    private void ini(VcfaRestClient restClient, Package vcfaPackage, VcfaConfiguration config,
            VcfaPackageDescriptor descriptor) {
        this.restClient = restClient;
        this.vcfaPackage = vcfaPackage;
        this.config = config;
        if (descriptor != null) {
            this.descriptor = descriptor;
        }
    }

    public void init(VcfaRestClient restClient, Package vcfaPackage, VcfaConfiguration config,
            VcfaPackageDescriptor descriptor) {
        this.ini(restClient, vcfaPackage, config, descriptor);
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    public void init(VcfaRestClient restClient, Package vcfaPackage, VcfaConfiguration config,
            VcfaPackageDescriptor descriptor, Logger logger) {
        this.ini(restClient, vcfaPackage, config, descriptor);
        this.logger = logger;
    }

    @Override
    public void importContent(File sourceDirectory) {
        logger.info("Import not implemented for {}", this.getClass().getSimpleName());
    }

    @Override
    public void exportContent() {
        logger.info("Export not implemented for {}", this.getClass().getSimpleName());
    }

    @Override
    public void deleteContent() {
        logger.info("Delete not implemented for {}", this.getClass().getSimpleName());
    }

    protected File[] filterBasedOnConfiguration(File itemFolder, FilenameFilter filter) {
        return itemFolder.listFiles(filter);
    }

    /**
     * Read details.json from an item folder and return as Map.
     */
    @SuppressWarnings("unchecked")
    protected java.util.Map<String,Object> readDetailsJson(File itemFolder) {
        try {
            File details = new File(itemFolder.getPath() + File.separator + "details.json");
            if (!details.exists()) return null;
            com.fasterxml.jackson.databind.ObjectMapper m = new com.fasterxml.jackson.databind.ObjectMapper();
            return m.readValue(details, java.util.Map.class);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Unable to read details.json", e);
        }
    }

    protected <T> T readDetailsJson(File itemFolder, Class<T> cls) {
        try {
            File details = new File(itemFolder.getPath() + File.separator + "details.json");
            if (!details.exists()) return null;
            com.fasterxml.jackson.databind.ObjectMapper m = new com.fasterxml.jackson.databind.ObjectMapper();
            return m.readValue(details, cls);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Unable to read details.json", e);
        }
    }
}
