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

import com.google.gson.*;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.vrang.*;
import com.vmware.pscoe.iac.artifact.model.vrang.objectmapping.VraNgCloudRegionProfile;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minidev.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

import static com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_STORAGE_PROFILES;
import static com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_REGIONS;

public class VraNgStorageProfileStore extends AbstractVraNgRegionalStore {

    private final Logger logger = LoggerFactory.getLogger(VraNgStorageProfileStore.class);

	// =================================================
	// STORAGE PROFILES EXPORT
	// =================================================

	/**
	 * Used to fetch the store's data from the package descriptor
	 *
	 * @return list of storage profiles
	 */
	@Override
	protected List<String> getItemListFromDescriptor() {
		return this.vraNgPackageDescriptor.getStorageProfile();
	}

	/**
	 * Called when the List returned from getItemListFromDescriptor is empty
	 *
	 * @param cloudAccounts list of cloud accounts
	 */
	@Override
	protected void exportStoreContent(List<VraNgCloudAccount> cloudAccounts) {
		Map<String, List<VraNgStorageProfile>> storageProfilesByRegionId = this.restClient.getAllStorageProfilesByRegion();

		cloudAccounts.forEach(cloudAccount -> {
			List<String> regionsInCloudAccount = cloudAccount.getRegionIds()
				.stream()
				.filter(storageProfilesByRegionId::containsKey)
				.collect(Collectors.toList());

			logger.debug("Exporting storage profiles from cloud account {}", cloudAccount.getName());

			regionsInCloudAccount.forEach(regionId -> {
				// create region directory
				String profileDirName = cloudAccount.getName() + "~" + regionId;
				File sourceDir = new File(vraNgPackage.getFilesystemPath());
               
				VraNgRegionalContentUtils.createCloudRegionProfileFile(cloudAccount, regionId, sourceDir, profileDirName);

				List<VraNgStorageProfile> storageProfiles = storageProfilesByRegionId.get(regionId)
					.stream()
					.map(profile -> convertToSpecificProfile(profile, cloudAccount))
					.collect(Collectors.toList());
                    
				logger.info("Storage profiles to export: {}", 
					storageProfiles.stream().map(VraNgStorageProfile::getName).collect(Collectors.toList()));

				storageProfiles.forEach(sp -> this.exportToFileSystem(sourceDir, profileDirName, sp));
			});
		});
	}

	/**
	 * Called when the List returned from getItemListFromDescriptor is not empty
	 *
	 * @param	cloudAccounts list of cloud accounts
	 * @param	storageProfilesToExport list of storage profiles
	 */
	@Override
	protected void exportStoreContent( List<VraNgCloudAccount> cloudAccounts, List<String> storageProfilesToExport ) {
		Map<String, List<VraNgStorageProfile>> storageProfilesByRegionId = this.restClient.getAllStorageProfilesByRegion();

		cloudAccounts.forEach(cloudAccount -> {
			List<String> regionsInCloudAccount = cloudAccount.getRegionIds()
				.stream()
				.filter(storageProfilesByRegionId::containsKey)
				.collect(Collectors.toList());

			logger.debug("Exporting storage profiles from cloud account {}", cloudAccount.getName());

			regionsInCloudAccount.forEach(regionId -> {
				// create region directory
				String profileDirName = cloudAccount.getName() + "~" + regionId;
				File sourceDir = new File(vraNgPackage.getFilesystemPath());
				VraNgRegionalContentUtils.createCloudRegionProfileFile(cloudAccount, regionId, sourceDir, profileDirName);


				List<VraNgStorageProfile> storageProfiles = storageProfilesByRegionId.get(regionId)
					.stream()
					.filter(sp -> storageProfilesToExport.contains(sp.getName()))
					.map(profile -> convertToSpecificProfile(profile, cloudAccount))
					.collect(Collectors.toList());

				logger.info("Storage profiles to export: {}", 
					storageProfiles.stream().map(VraNgStorageProfile::getName).collect(Collectors.toList()));

				storageProfiles.forEach(sp -> this.exportToFileSystem(sourceDir, profileDirName, sp));
			});
		});
	}

	enum ProfileType {
        VSPHERE, AZURE, AWS, UNKNOWN
    }

    /**
     * Save an storage profile to a JSON file
     * @param sourceDir source directory
     * @param profileDirName region directory
     * @param storageProfile storage profile
     */
    private void exportToFileSystem(File sourceDir, String profileDirName, VraNgStorageProfile storageProfile) {
        File storageProfileFile =
                Paths.get(sourceDir.getPath(), DIR_REGIONS, profileDirName, DIR_STORAGE_PROFILES, storageProfile.getName() + ".json").toFile();

        storageProfileFile.getParentFile().mkdirs();

        try {
            Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
            String profileJson = gson.toJson(gson.fromJson(storageProfile.getJson(), JsonObject.class));
            logger.info("Created file {}", Files.write(Paths.get(storageProfileFile.getPath()), profileJson.getBytes(), StandardOpenOption.CREATE));
        } catch (IOException e) {
            logger.error("Unable to store storage profile {} {}", storageProfile.getName(), storageProfileFile.getPath());
            throw new RuntimeException("Unable to store storage profile.", e);
        }
    };

    /**
     * Convert an abstract storage profile to specific storage profile.
     * A specific storage profile is a cloud account-specific profile representation.
     * @param profile storage profile
     * @return storage profile
     */
    private VraNgStorageProfile convertToSpecificProfile(VraNgStorageProfile profile, VraNgCloudAccount cloudAccount) {

        // get specific profile
        JsonElement root = new JsonParser().parse(profile.getJson());
        JsonObject ob = root.getAsJsonObject();
        String profileId = ob.get("id").getAsString();
        VraNgStorageProfile specificProfile = this.restClient.getSpecificStorageProfile(
                String.format("storage-profiles-%s", cloudAccount.getType()), profileId);

        // create cleaned-up specific profile
        JsonElement cleanRoot = new JsonParser().parse(specificProfile.getJson());
        JsonObject cleanOb = cleanRoot.getAsJsonObject();
        Gson gson = new GsonBuilder().setLenient().serializeNulls().create();

        // Check if the disk has diskProperties
        JsonObject diskProperties = ob.has("diskProperties") ? ob.get("diskProperties").getAsJsonObject() : null;

        ProfileType profileType = getProfileType(cloudAccount);
        // First check if the disk is a First Class Disk and based on that omit writing diskMode, so we can execute create/patch commands
        switch (profileType) {
            case VSPHERE:
                if(diskProperties != null && diskProperties.has("diskType") && diskProperties != null && diskProperties.get("diskType").getAsString().equals("firstClass")) {
                    cleanOb = VraNgRegionalContentUtils.cleanJson(cleanOb, Arrays.asList(
                        "supportsEncryption", "sharesLevel", "description", "tags",
                        "shares", "provisioningType", "diskType", "limitIops", "name", "defaultItem"), null);
                } else {
                    cleanOb = VraNgRegionalContentUtils.cleanJson(cleanOb, Arrays.asList(
                        "supportsEncryption", "sharesLevel", "description", "diskMode", "tags",
                        "shares", "provisioningType", "diskType", "limitIops", "name", "defaultItem"), null);
                }

                // create datastore fabric link
                if (ob.has("_links") && ob.get("_links").getAsJsonObject().has("datastore")) {
                    Map<String, Object> datastore = new LinkedHashMap<>();
                    String datastoreHref = ob.get("_links").getAsJsonObject()
                            .get("datastore").getAsJsonObject()
                            .get("href").getAsString();
                    String datastoreName = this.restClient.getFabricEntityName(datastoreHref);
                    datastore.put("name", datastoreName);
                    datastore.put("fabric", datastoreHref.split("/")[3]);
                    cleanOb.add("_datastore", gson.toJsonTree(datastore));
                }

                // create storagePolicy fabric link
                if (ob.has("_links") && ob.get("_links").getAsJsonObject().has("storage-policy")) {
                    Map<String, Object> storagePolicy = new LinkedHashMap<>();
                    String storagePolicyHref = ob.get("_links").getAsJsonObject()
                            .get("storage-policy").getAsJsonObject()
                            .get("href").getAsString();
                    String storagePolicyName = this.restClient.getFabricEntityName(storagePolicyHref);
                    storagePolicy.put("name", storagePolicyName);
                    storagePolicy.put("fabric", storagePolicyHref.split("/")[3]);
                    cleanOb.add("_storagePolicy", gson.toJsonTree(storagePolicy));
                }
                break;

            // intentional fallthrough
            case AWS:
            case AZURE:
            case UNKNOWN:
                logger.warn("Unsupported storage profile type '{}'", profileType);
                break;
        }

        cleanOb.addProperty("_type", cloudAccount.getType());

        return new VraNgStorageProfile(profile.getName(), cleanOb.toString());
    }

    // =================================================
    // STORAGE PROFILES IMPORT
    // =================================================

    /**
     * Import all storage profiles from a package
     * @param sourceDirectory temporary directory containing the files
	 * @param importTags list of tags
     */
    public void importContent(File sourceDirectory, List<String> importTags) {
        File regionsFolder = Paths.get(sourceDirectory.getPath(), DIR_REGIONS).toFile();
        if (!regionsFolder.exists()) {
            logger.debug("Regions directory does not exist. Skipping import of storage profiles...");
            return;
        }

        List<VraNgCloudAccount> cloudAccounts = this.restClient.getCloudAccounts();

        Map<String, List<VraNgStorageProfile>> storageProfilesByRegion = this.restClient.getAllStorageProfilesByRegion();

        // list all directories in the regions folder
        Arrays.asList(regionsFolder.listFiles(File::isDirectory)).forEach(regionProfileDir -> {
            try {
                VraNgCloudRegionProfile cloudRegionProfile = VraNgRegionalContentUtils.getCloudRegionProfile(regionProfileDir);
                cloudAccounts
                        .stream()
                        .filter(cloudAccount -> VraNgRegionalContentUtils.isIntersecting(cloudAccount.getTags(), importTags))
                        .filter(cloudAccount -> cloudAccount.getType().equals(cloudRegionProfile.getRegionType()))
                        .forEach(cloudAccount -> cloudAccount.getRegionIds()
                                .forEach(regionId -> this.importInRegion(
                                        regionId,
                                        regionProfileDir,
                                        storageProfilesByRegion)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * Import storage profiles in a cloud region (cloud zone).
     * @param regionId region id
     * @param regionProfileDir region directory
     * @param remoteProfiles list of existing storage profiles on server grouped by region
     */
    private void importInRegion(String regionId, File regionProfileDir, Map<String, List<VraNgStorageProfile>> remoteProfiles) {

        File storageProfilesDir = Paths.get(regionProfileDir.getPath(), DIR_STORAGE_PROFILES).toFile();
        if (!storageProfilesDir.exists()) {
            logger.debug("Storage profiles directory {} does not exist in region {}. Skipping...", DIR_STORAGE_PROFILES, regionId);
            return;
        }

        // collect local profiles
        List<VraNgStorageProfile> localProfiles = this.getStorageProfilesFromFileSystem(storageProfilesDir);

        // collect remote profiles if there are profiles in the region
        Map<String, VraNgStorageProfile> regionProfiles;
        if (remoteProfiles.containsKey(regionId)) {
            regionProfiles = remoteProfiles.get(regionId).stream()
                    .collect(Collectors.toMap(VraNgStorageProfile::getName, profile -> profile));
        } else {
            regionProfiles = new HashMap<>();
        }

        logger.info("Creating/updating {} storage profiles: {}",
			localProfiles.size(), localProfiles.stream().map(VraNgStorageProfile::getName).collect(Collectors.toList()));


        localProfiles.forEach(localProfile -> {

            // for each storage profile check if such profile exists remotely
            String profileId;
            if (regionProfiles.containsKey(localProfile.getName())) {
                logger.debug("Storage profile {} already exists. Recreating...", localProfile.getName());
                profileId = getProfileId(regionProfiles.get(localProfile.getName()));
                this.restClient.updateStorageProfile(profileId, getUpsertPayload(regionId, localProfile));
            } else {
                logger.debug("Creating storage profile {} ...", localProfile.getName());
                profileId = this.restClient.createStorageProfile(getUpsertPayload(regionId, localProfile));
            }

            // Update specific profile. A specific profile is a storage profile representation
            // under a specific cloud account type.
            Map.Entry<String, VraNgStorageProfile> patchProfile = getPatchPayload(regionId, localProfile);
            String patchTarget = patchProfile.getKey();
            VraNgStorageProfile specificProfile = patchProfile.getValue();
            this.restClient.updateSpecificProfile(patchTarget, profileId, specificProfile);

        });

    }

    /**
     * Create a list of storage profiles from JSON file representation
     * @param storageProfilesDir directory containing the image mappings for the region
     * @return list of storage profiles
     */
    private List<VraNgStorageProfile> getStorageProfilesFromFileSystem(File storageProfilesDir) {
        List<VraNgStorageProfile> storageProfiles = new ArrayList<>();
        FileUtils.listFiles(storageProfilesDir, new String[] { "json" }, false).forEach(im -> {
            try {
                File spFile = (File) im;
                String storageProfileName = FilenameUtils.removeExtension(spFile.getName());
                String storageProfileContent = FileUtils.readFileToString(spFile, "UTF-8");
                storageProfiles.add(new VraNgStorageProfile(storageProfileName, storageProfileContent));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return storageProfiles;
    }

    /**
     * Extract profile id
     * @param profile storage profile
     * @return id
     */
    private String getProfileId(VraNgStorageProfile profile) {
        JsonElement root = new JsonParser().parse(profile.getJson());
        return root.getAsJsonObject().get("id").getAsString();
    }

    /**
     * Create API-compliant JSON payload from local storage profile
     * for creating/updating storage profiles.
     * @param regiondId region id
     * @param profile local storage profile
     * @return storage profile with updated payload
     */
    private VraNgStorageProfile getUpsertPayload(String regiondId, VraNgStorageProfile profile) {
        JsonElement root = new JsonParser().parse(profile.getJson());
        JsonObject ob = root.getAsJsonObject();

        // create simple storage profile which will be updated later with cloud account-specific properties
        JsonObject cleanedOb = VraNgRegionalContentUtils.cleanJson(ob, Arrays.asList("name", "description", "tags"), null);
        cleanedOb.addProperty("regionId", regiondId);

        Gson gson = new GsonBuilder().setLenient().serializeNulls().create();
        VraNgStorageProfile storageProfile = new VraNgStorageProfile(profile.getName(), gson.toJson(cleanedOb));
        logger.debug("Upsert JSON: {}", storageProfile.getJson());
        return storageProfile;
    }

    /**
     * Create API-compliant JSON payload from local storage profile
     * for patching cloud-specific storage profiles.
     * @param profile local storage profile
     * @return storage profile with updated payload
     */
    private Map.Entry<String, VraNgStorageProfile> getPatchPayload(String regionId, VraNgStorageProfile profile) {
        JsonElement root = new JsonParser().parse(profile.getJson());
        JsonObject ob = root.getAsJsonObject();

        ob.addProperty("regionId", regionId);

        String patchTarget =  "storage-profiles";
        ProfileType profileType = getProfileType(profile);
        switch (profileType) {
            case VSPHERE:
                patchTarget = "storage-profiles-vsphere";
                // resolve datastore link
                if (ob.has("_datastore")) {
                    String datastoreId = resolveFabricId(ob.get("_datastore").getAsJsonObject());
                    if (datastoreId != null) {
                        ob.addProperty("datastoreId", datastoreId);
                    }
                }
                // resolve storage-policy link
                if (ob.has("_storagePolicy")) {
                    String storagePolicyId = resolveFabricId(ob.get("_storagePolicy").getAsJsonObject());
                    if (storagePolicyId != null) {
                        ob.addProperty("storagePolicyId", storagePolicyId);
                    }
                }
                break;

            // intentional fallthrough
            case AWS:
            case AZURE:
            case UNKNOWN:
                logger.warn("Unsupported storage profile type '{}'", profileType);
                break;
        }

        JsonObject cleanedOb = VraNgRegionalContentUtils.cleanJson(ob, null,
                Arrays.asList("_type", "_datastore", "_storagePolicy"));

        VraNgStorageProfile storageProfile = new VraNgStorageProfile(profile.getName(), cleanedOb.toString());
        logger.debug("Patch JSON: {}", storageProfile.getJson());
        return new AbstractMap.SimpleEntry<>(patchTarget, storageProfile);
    }

    // =================================================
    // UTILITY METHODS
    // =================================================

    /**
     * Resolve fabric entity id from JSON object
     * @param ob JSON object
     * @return id
     */
    private String resolveFabricId(JsonObject ob) {
        String fabric = ob.get("fabric").getAsString();
        String name = ob.get("name").getAsString();
        String entityId = this.restClient.getFabricEntityId(fabric, name);
        logger.debug("{} '{}': {}", fabric, name, entityId);
        return entityId;
    }

    /**
     * Determine profile type from storage profile
     * @param profile storage profile
     * @return profile type
     */
    ProfileType getProfileType(VraNgStorageProfile profile) {

        JsonElement root = new JsonParser().parse(profile.getJson());
        JsonObject ob = root.getAsJsonObject();

        String type = ob.get("_type").getAsString();
        if (type.equals("vsphere")) {
            return ProfileType.VSPHERE;
        } else if (type.equals("aws")) {
            return ProfileType.AWS;
        } else if (type.equals("azure")) {
            return ProfileType.AZURE;
        }

        return ProfileType.UNKNOWN;
    }

    /**
     * Determine profile type from cloud account
     * @param cloudAccount cloud account
     * @return profile type
     */
    ProfileType getProfileType(VraNgCloudAccount cloudAccount) {
        String type = cloudAccount.getType();
        if (type.equals("vsphere")) {
            return ProfileType.VSPHERE;
        } else if (type.equals("aws")) {
            return ProfileType.AWS;
        } else if (type.equals("azure")) {
            return ProfileType.AZURE;
        }

        return ProfileType.UNKNOWN;
    }

    /**
     * Determine profile type from cloud region
     * @param cloudRegion cloud region
     * @return profile type
     */
    ProfileType getProfileType(VraNgCloudRegionProfile cloudRegion) {
        String type = cloudRegion.getRegionType();
        if (type.equals("vsphere")) {
            return ProfileType.VSPHERE;
        } else if (type.equals("aws")) {
            return ProfileType.AWS;
        } else if (type.equals("azure")) {
            return ProfileType.AZURE;
        }

        return ProfileType.UNKNOWN;
    }

}
