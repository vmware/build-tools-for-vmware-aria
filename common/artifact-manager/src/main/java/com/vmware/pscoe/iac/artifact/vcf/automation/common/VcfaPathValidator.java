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

import java.io.IOException;

public final class VcfaPathValidator {

    // Regex permits alphanumeric characters, underscores, hyphens, spaces, and literal parentheses
    private static final String SAFE_NAME_REGEX = "^[a-zA-Z0-9_\\- \\(\\)]+$";

    private VcfaPathValidator() {
        // Utility class private constructor
    }

    /**
     * Validates an asset name to ensure cross-platform file system compatibility.
     * Throws an IOException if forbidden characters or formatting violations are found.
     *
     * @param assetName The display name or tracking name of the configuration asset.
     * @param assetType The type of asset being checked (for clear logging).
     * @throws IOException If the name contains non-portable characters.
     */
    public static void validateSafePathName(String assetName, String assetType) throws IOException {
        if (assetName == null || assetName.isEmpty()) {
            throw new IOException(String.format("CRITICAL WORKSPACE ERROR: %s name cannot be null or empty.", assetType));
        }

        // Guard against leading or trailing spaces that trigger OS-level directory clipping
        if (!assetName.equals(assetName.trim())) {
            throw new IOException(String.format(
                "CRITICAL CROSS-PLATFORM ERROR: The %s named '%s' contains leading or trailing spaces. "
                + "While spaces are allowed inside names, they cannot be at the very beginning or end of the asset name.",
                assetType, assetName
            ));
        }

        if (!assetName.matches(SAFE_NAME_REGEX)) {
            throw new IOException(String.format(
                "CRITICAL CROSS-PLATFORM ERROR: The %s named '%s' contains invalid characters. "
                + "To ensure workspace portability across Windows, macOS, and Linux, names must "
                + "ONLY contain alphanumeric characters, spaces, hyphens (-), underscores (_), or parentheses ().",
                assetType, assetName
            ));
        }
    }
}
