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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

import static com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_IMAGE_MAPPINGS;
import static com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_REGIONS;

public class VraNgImageMappingStore extends AbstractVraNgRegionalStore {

    private final Logger logger = LoggerFactory.getLogger(VraNgImageMappingStore.class);

    // =================================================
    // IMAGE MAPPINGS EXPORT
    // =================================================

	/**
	 * Used to fetch the store's data from the package descriptor
	 *
	 * @return list of image mappings
	 */
	@Override
	protected List<String> getItemListFromDescriptor() {
		return this.vraNgPackageDescriptor.getImageMapping();
	}

	/**
	 * Called when the List returned from getItemListFromDescriptor is empty
	 *
	 * @param cloudAccounts list of cloud accounts
	 */
	@Override
	protected void exportStoreContent(List<VraNgCloudAccount> cloudAccounts) {
		Map<String, List<VraNgImageMapping>> imagesByRegionId = this.restClient.getAllImageMappingsByRegion();

		cloudAccounts.forEach(cloudAccount -> {

			List<String> regionsInCloudAccount = cloudAccount.getRegionIds()
				.stream()
				.filter(imagesByRegionId::containsKey)
				.collect(Collectors.toList());

			logger.debug("Exporting image mappings from cloud account {}", cloudAccount.getName());

			regionsInCloudAccount.forEach(regionId -> {

				String profileDirName = cloudAccount.getName() + "~" + regionId;

				File sourceDir = new File(vraNgPackage.getFilesystemPath());
				VraNgRegionalContentUtils.createCloudRegionProfileFile(cloudAccount, regionId, sourceDir, profileDirName);

				List<VraNgImageMapping> imageMappings = imagesByRegionId.get(regionId)
					.stream()
					.map(this::prepareMappingSerialization)
					.collect(Collectors.toList());

					logger.info("Image mappings to export: {}", 
					imageMappings.stream().map(VraNgImageMapping::getName).collect(Collectors.toList()));

				imageMappings.forEach(imageMapping ->
					this.exportToFileSystem(sourceDir, profileDirName, imageMapping));
			});
		});
	}

	/**
	 * Called when the List returned from getItemListFromDescriptor is not empty
	 *
	 * @param cloudAccounts list of cloud accounts
	 * @param imageMappingsToExport list of image mappings
	 */
	@Override
	protected void exportStoreContent(List<VraNgCloudAccount> cloudAccounts, List<String> imageMappingsToExport) {
		Map<String, List<VraNgImageMapping>> imagesByRegionId = this.restClient.getAllImageMappingsByRegion();

		cloudAccounts.forEach(cloudAccount -> {

			List<String> regionsInCloudAccount = cloudAccount.getRegionIds()
				.stream()
				.filter(imagesByRegionId::containsKey)
				.collect(Collectors.toList());

			logger.debug("Exporting image mappings from cloud account {}", cloudAccount.getName());

			regionsInCloudAccount.forEach(regionId -> {

				String profileDirName = cloudAccount.getName() + "~" + regionId;

				File sourceDir = new File(vraNgPackage.getFilesystemPath());
				VraNgRegionalContentUtils.createCloudRegionProfileFile(cloudAccount, regionId, sourceDir, profileDirName);

				List<VraNgImageMapping> imageMappings = imagesByRegionId.get(regionId)
					.stream()
					.filter(im -> imageMappingsToExport.contains(im.getName()))
					.map(this::prepareMappingSerialization)
					.collect(Collectors.toList());

				logger.info("Image mappings to export: {}", 
					imageMappings.stream().map(VraNgImageMapping::getName).collect(Collectors.toList()));

				imageMappings.forEach(imageMapping ->
					this.exportToFileSystem(sourceDir, profileDirName, imageMapping));
			});
		});
	}

    /**
     * Save an image mapping to a JSON file
     * @param sourceDir source directory
     * @param profileDirName region directory
     * @param imageMapping image mapping
     */
    private void exportToFileSystem(File sourceDir, String profileDirName, VraNgImageMapping imageMapping) {
        File imageMappingFile =
                Paths.get(sourceDir.getPath(), DIR_REGIONS, profileDirName, DIR_IMAGE_MAPPINGS, imageMapping.getName() + ".json").toFile();

        imageMappingFile.getParentFile().mkdirs();

        try {
            Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
            String imageJson = gson.toJson(gson.fromJson(imageMapping.getJson(), JsonObject.class));
            logger.info("Created file {}", Files.write(Paths.get(imageMappingFile.getPath()), imageJson.getBytes(), StandardOpenOption.CREATE));
        } catch (IOException e) {
            logger.error("Unable to store image mapping {} {}", imageMapping.getName(), imageMappingFile.getPath());
            throw new RuntimeException("Unable to store image mapping.", e);
        }
    }

    /**
     * Create a new VraNgImageMapping with serializable payload
     * @param mapping image mapping
     * @return new VraNgStorageProfile
     */
    private VraNgImageMapping prepareMappingSerialization(VraNgImageMapping mapping) {

        JsonElement root = new JsonParser().parse(mapping.getJson());
        JsonObject ob = root.getAsJsonObject();

        JsonObject cleanedOb = VraNgRegionalContentUtils.cleanJson(ob, null, Arrays.asList(
                "_links", "id", "externalRegionId"
        ));

        return new VraNgImageMapping(mapping.getName(), cleanedOb.toString());

    }

    // =================================================
    // IMAGE MAPPINGS IMPORT
    // =================================================

    /**
     * Import all image profiles from a package.
     * @param sourceDirectory temporary directory containing the files
	 * @param importTags list of tags
     */
    public void importContent(File sourceDirectory, List<String> importTags) {
        File regionsFolder = Paths.get(sourceDirectory.getPath(), DIR_REGIONS).toFile();
        if (!regionsFolder.exists()) {
            logger.debug("Regions directory does not exist. Skipping import of image profiles...");
            return;
        }

        List<VraNgCloudAccount> cloudAccounts = this.restClient.getCloudAccounts();

        Map<String, List<String>> imageProfilesByRegion = this.restClient.getAllImageProfilesByRegion();

        logger.debug("Image profiles by region: {}", imageProfilesByRegion);

        // list all directories in the regions folder
        Arrays.asList(regionsFolder.listFiles(File::isDirectory)).forEach(regionProfileDir -> {
            try {
                VraNgCloudRegionProfile cloudRegionProfile = VraNgRegionalContentUtils.getCloudRegionProfile(regionProfileDir);
                cloudAccounts
                        .stream()
                        .filter(cloudAccount -> VraNgRegionalContentUtils.isIntersecting(cloudAccount.getTags(), importTags))
                        .filter(cloudAccount -> cloudAccount.getType().equals(cloudRegionProfile.getRegionType()))
                        .forEach(cloudAccount -> cloudAccount.getRegionIds()
                                .forEach(regionId -> this.importImageProfilesInRegion(
                                        regionId,
                                        regionProfileDir,
                                        imageProfilesByRegion)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create a list of image mappings from JSON file representation
     * @param imageMappingsDir directory containing the image mappings for the region
     * @return list of image mappings
     */
    private List<VraNgImageMapping> getImageMappingsFromFileSystem(File imageMappingsDir) {
        List<VraNgImageMapping> imageMappings = new ArrayList<>();
        FileUtils.listFiles(imageMappingsDir, new String[] { "json" }, false).forEach(im -> {
            try {
                File imFile = (File) im;
                String imageMappingName = FilenameUtils.removeExtension(imFile.getName());
                String imageMappingContent = FileUtils.readFileToString(imFile, "UTF-8");
                imageMappings.add(new VraNgImageMapping(imageMappingName, imageMappingContent));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return imageMappings;
    }

    /**
     * Import image mappings in a cloud region (cloud zone). If an image profile already exists,
     * update it with a new mapping, otherwise create an image profile with a single mapping inside.
     * @param regionId region id
     * @param regionProfileDir region directory
     * @param imageProfilesByRegion list of existing image profiles on server
     */
    private void importImageProfilesInRegion(
            String regionId, File regionProfileDir, Map<String, List<String>> imageProfilesByRegion) {

        File imageMappingsDir = Paths.get(regionProfileDir.getPath(), DIR_IMAGE_MAPPINGS).toFile();
        if (!imageMappingsDir.exists()) {
            logger.debug("Image mappings directory {} does not exist in region {}. Skipping...", DIR_IMAGE_MAPPINGS, regionId);
            return;
        }
        List<VraNgImageMapping> imageMappings = this.getImageMappingsFromFileSystem(imageMappingsDir);

        logger.info("Creating/updating {} image mappings: {}",
			imageMappings.size(), imageMappings.stream().map(VraNgImageMapping::getName).collect(Collectors.toList()));

        if (imageProfilesByRegion.containsKey(regionId)) {
            this.updateImageProfilesWithMappings(imageProfilesByRegion.get(regionId), imageMappings);
        } else {
            this.createImageProfilesWithMappings(regionId, imageMappings);
        }
    }

    private void updateImageProfilesWithMappings(
            List<String> imageProfilesToPatch, List<VraNgImageMapping> imageMappings) {

        List<VraNgImageMapping> mappingList = imageMappings.stream()
                .map(this::getUpsertPayload)
                .collect(Collectors.toList());

        imageProfilesToPatch.forEach(imageProfileId -> {
            this.restClient.updateImageProfile(imageProfileId, mappingList);
        });
    }

    private void createImageProfilesWithMappings(String regionId, List<VraNgImageMapping> imageMappings) {
        List<VraNgImageMapping> mappingList = imageMappings.stream()
                .map(this::getUpsertPayload)
                .collect(Collectors.toList());

        this.restClient.createImageProfile(regionId, "Auto Generated Profile from Import", mappingList);
    }

    /**
     * Create API-compliant JSON payload from local image mapping
     * for creating/updating image profiles.
     * @param mapping local image mapping
     * @return image mapping with updated payload
     */
    private VraNgImageMapping getUpsertPayload(VraNgImageMapping mapping) {
        JsonElement root = new JsonParser().parse(mapping.getJson());
        JsonObject ob = root.getAsJsonObject();

        String fabricId = this.restClient.getFabricEntityId("fabric-images", ob.get("name").getAsString());
        if (fabricId != null) {

            // add fabric entity id
            ob.addProperty("id", fabricId);
            Map<String, String> payload = new HashMap<>();
            payload.put("id", fabricId);

            Gson gson = new GsonBuilder().setLenient().serializeNulls().create();
            return new VraNgImageMapping(mapping.getName(), gson.toJson(payload));
        }

        return mapping;
    }
}
