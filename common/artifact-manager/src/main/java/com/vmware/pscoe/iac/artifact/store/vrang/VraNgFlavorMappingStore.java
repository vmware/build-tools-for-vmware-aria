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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCloudAccount;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgFlavorMapping;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPackageDescriptor;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_FLAVOR_MAPPINGS;
import static com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_REGIONS;

public class VraNgFlavorMappingStore extends AbstractVraNgRegionalStore{

    private final Logger logger = LoggerFactory.getLogger(VraNgFlavorMappingStore.class);

    // =================================================
    // FLAVOR MAPPINGS EXPORT
    // =================================================

	/**
	 * Used to fetch the store's data from the package descriptor
	 *
	 * @return list of flavor mappings
	 */
	@Override
	protected List<String> getItemListFromDescriptor() {
		return this.vraNgPackageDescriptor.getFlavorMapping();
	}

	/**
	 * Called when the List returned from getItemListFromDescriptor is empty
	 */
	@Override
	protected void exportStoreContent( List<VraNgCloudAccount> cloudAccounts ) {
		Map<String, List<VraNgFlavorMapping>> flavorsByRegionId = this.restClient.getAllFlavorMappingsByRegion();

		cloudAccounts.forEach(cloudAccount -> {
			List<String> regionsInCloudAccount = cloudAccount.getRegionIds()
				.stream()
				.filter(flavorsByRegionId::containsKey)
				.collect(Collectors.toList());

			logger.debug("Exporting flavor mappings from cloud account {}", cloudAccount.getName());

			regionsInCloudAccount.forEach(regionId -> {

				String profileDirName = cloudAccount.getName() + "~" + regionId;

				File sourceDir = new File(vraNgPackage.getFilesystemPath());
				VraNgRegionalContentUtils.createCloudRegionProfileFile(cloudAccount, regionId, sourceDir, profileDirName);

				List<VraNgFlavorMapping> flavorMappings = new ArrayList<>( flavorsByRegionId.get( regionId ) );

				logger.info("Flavour mappings to export: {}", 
					flavorMappings.stream().map(VraNgFlavorMapping::getName).collect(Collectors.toList()));

				flavorMappings.forEach(flavorMapping ->
					this.exportFlavorMappingToFileSystem(sourceDir, profileDirName, flavorMapping));
			});
		});
	}

	/**
	 * Called when the List returned from getItemListFromDescriptor is not empty
	 *
	 * @param	cloudAccounts list of cloud accounts
	 * @param	flavorMappingsToExport list of flavor mappings
	 */
	@Override
	protected void exportStoreContent( List<VraNgCloudAccount> cloudAccounts, List<String> flavorMappingsToExport ) {
		Map<String, List<VraNgFlavorMapping>> flavorsByRegionId	= this.restClient.getAllFlavorMappingsByRegion();

		cloudAccounts.forEach(cloudAccount -> {
			List<String> regionsInCloudAccount = cloudAccount.getRegionIds()
				.stream()
				.filter(flavorsByRegionId::containsKey)
				.collect(Collectors.toList());

			logger.debug("Exporting flavor mappings from cloud account {}", cloudAccount.getName());

			regionsInCloudAccount.forEach(regionId -> {
				String profileDirName = cloudAccount.getName() + "~" + regionId;

				File sourceDir = new File(vraNgPackage.getFilesystemPath());
				VraNgRegionalContentUtils.createCloudRegionProfileFile(cloudAccount, regionId, sourceDir, profileDirName);

				List<VraNgFlavorMapping> flavorMappings = flavorsByRegionId.get(regionId)
					.stream()
					.filter(fm -> flavorMappingsToExport.contains(fm.getName()))
					.collect(Collectors.toList());

				logger.info("Flavour mappings to export: {}", 
					flavorMappings.stream().map(VraNgFlavorMapping::getName).collect(Collectors.toList()));

				flavorMappings.forEach(flavorMapping ->
					this.exportFlavorMappingToFileSystem(sourceDir, profileDirName, flavorMapping));
			});
		});
	}

    /**
     * Save a flavor mapping to a JSON file
     * @param sourceDir source directory
     * @param profileDirName region directory
     * @param flavorMapping flavor mapping
     */
    private void exportFlavorMappingToFileSystem(File sourceDir, String profileDirName, VraNgFlavorMapping flavorMapping) {
        File flavorMappingFile =
                Paths.get(sourceDir.getPath(), DIR_REGIONS, profileDirName, DIR_FLAVOR_MAPPINGS, flavorMapping.getName() + ".json").toFile();

        flavorMappingFile.getParentFile().mkdirs();

        try {
            Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
            String flavorJson = gson.toJson(gson.fromJson(flavorMapping.getJson(), JsonObject.class));
            logger.info("Created file {}", Files.write(Paths.get(flavorMappingFile.getPath()), flavorJson.getBytes(), StandardOpenOption.CREATE));
        } catch (IOException e) {
            logger.error("Unable to store flavor mapping {} {}", flavorMapping.getName(), flavorMappingFile.getPath());
            throw new RuntimeException("Unable to store flavor mapping.", e);
        }
    }

    // =================================================
    // FLAVOR MAPPINGS IMPORT
    // =================================================

    /**
     * Import all flavor profiles from a package.
     * @param sourceDirectory temporary directory containing the files
	 * @param importTags list of tags
     */
    public void importContent(File sourceDirectory, List<String> importTags) {
        File regionsFolder = Paths.get(sourceDirectory.getPath(), DIR_REGIONS).toFile();
        if (!regionsFolder.exists()) {
            logger.debug("Regions directory does not exist. Skipping import of flavor profiles...");
            return;
        }

        List<VraNgCloudAccount> cloudAccounts = this.restClient.getCloudAccounts();

        Map<String, List<String>> flavorProfilesByRegion = this.restClient.getAllFlavorProfilesByRegion();

        logger.debug("Flavor profiles by region: {}", flavorProfilesByRegion);

        // list all directories in the regions folder
        Arrays.asList(regionsFolder.listFiles(File::isDirectory)).forEach(regionProfileDir -> {
            try {
                VraNgCloudRegionProfile cloudRegionProfile = VraNgRegionalContentUtils.getCloudRegionProfile(regionProfileDir);
                cloudAccounts
                        .stream()
                        .filter(cloudAccount -> VraNgRegionalContentUtils.isIntersecting(cloudAccount.getTags(), importTags))
                        .filter(cloudAccount -> cloudAccount.getType().equals(cloudRegionProfile.getRegionType()))
                        .forEach(cloudAccount -> cloudAccount.getRegionIds()
                                .forEach(regionId -> this.importFlavorProfilesInRegion(
                                        regionId,
                                        regionProfileDir,
                                        flavorProfilesByRegion)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create a list of flavor mappings from JSON file representation
     * @param flavorMappingsDir directory containing the flavor mappings for the region
     * @return list of flavor mappings
     */
    private List<VraNgFlavorMapping> getFlavorMappingsFromFileSystem(File flavorMappingsDir) {
        List<VraNgFlavorMapping> flavorMappings = new ArrayList<>();
        FileUtils.listFiles(flavorMappingsDir, new String[] { "json" }, false).forEach(fm -> {
            try {
                File fmFile = (File) fm;
                String flavorMappingName = FilenameUtils.removeExtension(fmFile.getName());
                String flavorMappingContent = FileUtils.readFileToString(fmFile, "UTF-8");
                flavorMappings.add(new VraNgFlavorMapping(flavorMappingName, flavorMappingContent));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return flavorMappings;
    }

    /**
     * Import flavor mappings in a cloud region (cloud zone). If a flavor profile already exists,
     * update it with a new mapping, otherwise create a flavor profile with a single mapping inside.
     * @param regionId region id
     * @param regionProfileDir region directory
     * @param flavorProfilesByRegion list of existing flavor profiles on server
     */
    private void importFlavorProfilesInRegion(
            String regionId, File regionProfileDir, Map<String, List<String>> flavorProfilesByRegion) {

        File flavorMappingsDir = Paths.get(regionProfileDir.getPath(), DIR_FLAVOR_MAPPINGS).toFile();
        if (!flavorMappingsDir.exists()) {
            logger.debug("Flavor mappings directory {} does not exist in region {}. Skipping...", DIR_FLAVOR_MAPPINGS, regionId);
            return;
        }
        List<VraNgFlavorMapping> flavorMappings = this.getFlavorMappingsFromFileSystem(flavorMappingsDir);

        logger.info("Creating/updating {} flavor mappings: {}",
			flavorMappings.size(), flavorMappings.stream().map(VraNgFlavorMapping::getName).collect(Collectors.toList()));

        if (flavorProfilesByRegion.containsKey(regionId)) {
            this.updateFlavorProfilesWithMappings(flavorProfilesByRegion.get(regionId), flavorMappings);
        } else {
            this.createFlavorProfilesWithMappings(regionId, flavorMappings);
        }
    }

    private void updateFlavorProfilesWithMappings(
            List<String> flavorProfilesToPatch, List<VraNgFlavorMapping> flavorMappings) {

        flavorProfilesToPatch.forEach(flavorProfileId -> {
            this.restClient.updateFlavor(flavorProfileId, flavorMappings);
        });
    }

    private void createFlavorProfilesWithMappings(String regionId, List<VraNgFlavorMapping> flavorMappings) {
        this.restClient.createFlavor(regionId, "Auto Generated Profile from Import", flavorMappings);
    }
}
