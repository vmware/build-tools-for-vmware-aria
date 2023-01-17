package com.vmware.pscoe.iac.artifact.store.vrang;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCloudAccount;
import com.vmware.pscoe.iac.artifact.model.vrang.objectmapping.VraNgCloudRegionProfile;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.*;

public class VraNgRegionalContentUtils {

    private static Logger logger = LoggerFactory.getLogger(VraNgRegionalContentUtils.class);

    /**
     * Create the cloud region profile file containing mapping for region tags and region id
     * @param cloudAccount cloud account
     * @param regionId region id
     * @param srcDir containing directory
     * @param cloudRegionProfileFolderName folder name for the region with region-specific content
     */
    public static void createCloudRegionProfileFile(VraNgCloudAccount cloudAccount, String regionId, File srcDir, String cloudRegionProfileFolderName) {
        File cloudRegionProfile =
                Paths.get(srcDir.getPath(), DIR_REGIONS, cloudRegionProfileFolderName, "src-region-profile.json").toFile();

        cloudRegionProfile.getParentFile().mkdirs();

        Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();

        VraNgCloudRegionProfile cloudRegionProfileJson = new VraNgCloudRegionProfile();
        cloudRegionProfileJson.setCloudAccountId(cloudAccount.getId());
        cloudRegionProfileJson.setRegionId(regionId);
        cloudRegionProfileJson.setTags(cloudAccount.getTags());
        cloudRegionProfileJson.setRegionType(cloudAccount.getType());

        String regionJson = gson.toJson(cloudRegionProfileJson, VraNgCloudRegionProfile.class);

        try {
            logger.info("Created file {} ", Files.write(Paths.get(cloudRegionProfile.getPath()),
                    regionJson.getBytes(), StandardOpenOption.CREATE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check whether two lists of strings are intersecting, i.e. have
     * at least one common element.
     * @param list1 first list
     * @param list2 second list
     * @return true if lists are intersecting
     */
    public static boolean isIntersecting(List<String> list1, List<String> list2) {
        Set<String> set1 = new HashSet<String>(list1);
        Set<String> set2 = new HashSet<String>(list2);

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        return !intersection.isEmpty();
    }

    public static VraNgCloudRegionProfile getCloudRegionProfile(File cloudRegionProfileDir) throws IOException {
        File regionProfile = Paths.get(cloudRegionProfileDir.getPath(), "src-region-profile.json").toFile();
        String regionProfileContent = FileUtils.readFileToString(regionProfile, "UTF-8");

        return new ObjectMapper().readValue(regionProfileContent, VraNgCloudRegionProfile.class);
    }

    /**
     * Create a new JSON object containing only set of keys
     * @param ob original JSON object
     * @param keysToKeep list of keys to keep
     * @param keysToRemove list of keys to remove
     * @return cleaned JSON object
     */
    public static JsonObject cleanJson(JsonObject ob, List<String> keysToKeep, List<String> keysToRemove) {
        JsonObject cleanedOb = new JsonObject();
        if (keysToKeep != null) {
            keysToKeep.forEach(key -> {
                if (ob.has(key)) {
                    cleanedOb.add(key, ob.get(key));
                }
            });
        }
        if (keysToRemove != null) {
            for (Map.Entry<String, JsonElement> entry : ob.entrySet()) {
                if (!keysToRemove.contains(entry.getKey())) {
                    cleanedOb.add(entry.getKey(), entry.getValue());
                }
            }
        }
        return cleanedOb;
    }

}
