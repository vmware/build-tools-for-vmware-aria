package com.vmware.pscoe.iac.artifact.vcf.automation.common;

/*-
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 - 2026 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 *
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.
 *
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import com.vmware.pscoe.iac.artifact.common.store.Package;

/**
 * Shared utility to parse and query the workspace 'content.yaml' configuration
 * rules.
 */
public final class VcfaDescriptorHelper {

    private static final Logger logger = LoggerFactory.getLogger(VcfaDescriptorHelper.class);

    private VcfaDescriptorHelper() {
        // Private constructor for utility pattern compliance
    }

    /**
     * Resolves the target list of asset strings from content.yaml based on
     * acceptable key mappings.
     *
     * @param vcfaPackage    The injected store package instance (can be null if
     *                       checking working directory only).
     * @param acceptableKeys An array of key combinations to search for (e.g.,
     *                       {"blueprint", "blueprints"}).
     * @return A list of declared asset names, null if the key is missing entirely,
     *         or an empty list if explicitly written as '[]'.
     */
    @SuppressWarnings("unchecked")
    public static List<String> getTargetedItems(Package vcfaPackage, String... acceptableKeys) {
        File contentYamlFile = locateContentYaml(vcfaPackage);

        if (contentYamlFile == null || !contentYamlFile.exists()) {
            return null;
        }

        Yaml yaml = new Yaml();
        try (InputStream inputStream = new FileInputStream(contentYamlFile)) {
            Map<String, Object> rawMap = yaml.load(inputStream);
            if (rawMap == null) {
                return null;
            }

            boolean keyFound = false;
            for (String key : acceptableKeys) {
                if (rawMap.containsKey(key)) {
                    keyFound = true;
                    Object listObj = rawMap.get(key);
                    
                    // Condition 1: Subproperty exists but is set to null -> Work with ALL workflows
                    if (listObj == null) {
                        logger.info("Descriptor property '{}' is explicitly null. Target matching includes ALL items.", key);
                        return null;
                    }
                    
                    // Condition 2: Subproperty is a valid populated or empty YAML array
                    if (listObj instanceof List) {
                        return (List<String>) listObj;
                    }
                }
            }

            // Condition 3: Subproperty is missing entirely -> Treat as empty array (process nothing)
            if (!keyFound) {
                logger.info("Descriptor target properties {} are entirely missing. Defaulting to an empty array target scope.", 
                        String.join(", ", acceptableKeys));
                return new java.util.ArrayList<>();
            }
            
        } catch (Exception e) {
            logger.warn("Non-fatal exception encountered checking descriptor definitions matching {}: {}",
                    String.join(", ", acceptableKeys), e.getMessage());
        }

        return null;
    }

    /**
     * Loops through items explicitly listed in content.yaml and logs a loud error
     * if their matching filesystem
     * workspace folder cannot be found on disk during a push operation.
     *
     * @param vcfaPackage    The injected store package instance.
     * @param localAssetDir  The directory where the asset folders should live
     *                       (e.g., .../blueprints).
     * @param assetTypeName  Friendly name for logging (e.g., "Blueprint").
     * @param acceptableKeys The content.yaml dictionary keys to lookup.
     */
    public static void validatePhysicalAssetsPresent(Package vcfaPackage, File localAssetDir, String assetTypeName,
            String... acceptableKeys) {
        List<String> explicitItems = getTargetedItems(vcfaPackage, acceptableKeys);
        if (explicitItems == null || explicitItems.isEmpty()) {
            return;
        }

        for (String expectedItem : explicitItems) {
            File expectedFolder = new File(localAssetDir, expectedItem);
            if (!expectedFolder.exists() || !expectedFolder.isDirectory()) {
                logger.error(
                        "CRITICAL CONFIGURATION MISMATCH: {} '{}' is explicitly listed in content.yaml, but its workspace asset directory is missing from disk path: {}",
                        assetTypeName, expectedItem, expectedFolder.getAbsolutePath());
            }
        }
    }

    private static File locateContentYaml(Package vcfaPackage) {
        File target = new File(System.getProperty("user.dir"), "content.yaml");
        if (!target.exists() && vcfaPackage != null && vcfaPackage.getFilesystemPath() != null) {
            File packageDir = new File(vcfaPackage.getFilesystemPath());
            File projectRoot = packageDir.getParentFile() != null ? packageDir.getParentFile() : packageDir;
            target = new File(projectRoot, "content.yaml");
        }
        return target;
    }

    /**
     * Loops through items explicitly listed in content.yaml and logs a loud error
     * if their matching filesystem
     * workspace file or folder cannot be found on disk during a push operation.
     *
     * @param vcfaPackage    The injected store package instance.
     * @param localAssetDir  The parent directory where the assets live (e.g.,
     *                       .../subscriptions).
     * @param assetTypeName  Friendly name for logging (e.g., "Subscription").
     * @param isFolderOnly   True if the asset type must be a directory
     *                       (blueprints); false if it can be a file or directory
     *                       (subscriptions).
     * @param acceptableKeys The content.yaml dictionary keys to lookup.
     */
    public static void validateAssetsPresent(Package vcfaPackage, File localAssetDir, String assetTypeName,
            boolean isFolderOnly, String... acceptableKeys) {
        List<String> explicitItems = getTargetedItems(vcfaPackage, acceptableKeys);
        if (explicitItems == null || explicitItems.isEmpty()) {
            return;
        }

        for (String expectedItem : explicitItems) {
            File expectedFolder = new File(localAssetDir, expectedItem);

            if (isFolderOnly) {
                if (!expectedFolder.exists() || !expectedFolder.isDirectory()) {
                    logger.error(
                            "CRITICAL CONFIGURATION MISMATCH: {} '{}' is explicitly listed in content.yaml, but its workspace asset directory is missing from disk path: {}",
                            assetTypeName, expectedItem, expectedFolder.getAbsolutePath());
                }
            } else {
                // Flexible match: look for a folder or a flat json file
                File expectedFlatFile = new File(localAssetDir, expectedItem + ".json");
                if (!expectedFolder.exists() && !expectedFlatFile.exists()) {
                    logger.error(
                            "CRITICAL CONFIGURATION MISMATCH: {} '{}' is explicitly listed in content.yaml, but its workspace asset file or folder is missing from disk path: {}",
                            assetTypeName, expectedItem, expectedFolder.getAbsolutePath());
                }
            }
        }
    }
}