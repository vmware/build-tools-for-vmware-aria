package com.vmware.pscoe.iac.artifact.extentions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.jsonpath.JsonPath;
import com.vmware.pscoe.iac.artifact.PackageManager;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.rest.RestClientVra;
import com.vmware.pscoe.iac.artifact.rest.helpers.JsonHelper;
import com.vmware.pscoe.iac.artifact.model.vra.VraPackageDescriptor;
import com.vmware.pscoe.iac.artifact.model.vra.VraPackageMemberType;

public class VraGlobalPropertyGroupPackageStoreExtention implements PackageStoreExtention<VraPackageDescriptor> {

    private final Logger logger = LoggerFactory.getLogger(VraGlobalPropertyGroupPackageStoreExtention.class);
    private final RestClientVra restClient;

    public VraGlobalPropertyGroupPackageStoreExtention(RestClientVra restClient) {
        this.restClient = restClient;
    }

    @Override
    public Package exportPackage(Package serverPackage, VraPackageDescriptor vraPackageDescriptor, boolean dryrun) {
        logger.debug("vRA Global Property Group export extention is enabled.");
        if (vraPackageDescriptor == null) {
            logger.debug("vRA Global Property Group package export requires vRA package descriptor.");
            return serverPackage;
        }

        List<String> propertyGroupNames = vraPackageDescriptor.getGlobalPropertyGroup();
        if (propertyGroupNames == null) {
            return serverPackage;
        }

        List<File> propertyGroupFiles = new ArrayList<>();
        List<Map<String, Object>> propertyGroups = restClient.getGlobalPropertyGroups();

        for (String propertyGroupName : propertyGroupNames) {
            Map<String, Object> propertyGroup = propertyGroups.stream()
                .filter(pg -> pg.get("label").equals(propertyGroupName))
                .findFirst()
                .orElse(null);

            if (propertyGroup == null) {
                logger.warn("Content with Type[" + VraPackageMemberType.GLOBAL_PROPERTY_GROUP.toString() + "], " + 
                    "Name[" + propertyGroupName + "] and supplied credentials cannot be found on the sever. Note that name is case sensitive.");
                continue;                
            }

            propertyGroup.remove("tenantId");

            String propertyGroupJson = JsonHelper.toSortedJson(propertyGroup);

            if (dryrun) {
                logger.info(propertyGroupJson);
                continue;
            }

            propertyGroupFiles.add(storePropertyGroupOnFileSystem(serverPackage, propertyGroupName, propertyGroupJson));            
        }

        try {
            new PackageManager(serverPackage).addToExistingZip(propertyGroupFiles);
        } catch (IOException e) {
            throw new RuntimeException("Error adding files to zip", e);
        }

        return serverPackage;
    }

    @Override
    public Package importPackage(Package pkg, boolean dryrun) {
        logger.debug("vRA Global Property Group import extention is enabled.");
        if (dryrun) {
            return pkg;
        }

        File tmp;
        try {
            tmp = Files.createTempDirectory("iac-global-property-groups").toFile();
            new PackageManager(pkg).unpack(tmp);
        } catch (IOException e) {
            logger.error("Unable to extract package '{}' in temporary directory.", pkg.getFQName());
            throw new RuntimeException("Unable to extract pacakge.", e);
        }

        File propertyGroupsFolder = Paths.get(tmp.getPath(), VraPackageMemberType.GLOBAL_PROPERTY_GROUP.toString()).toFile();
        if (propertyGroupsFolder.exists()) {
            FileUtils.listFiles(propertyGroupsFolder, new String[] { "json" }, false).stream()
                    .forEach(propertyGroup -> storePropertyGroupOnServer(propertyGroup));
        }
        return pkg;
    }

    private File storePropertyGroupOnFileSystem(Package serverPackage, String propertyGroupName,
            String propertyGroupJson) {
        File store = new File(serverPackage.getFilesystemPath()).getParentFile();
        File propertyGroup = Paths.get(store.getPath(), VraPackageMemberType.GLOBAL_PROPERTY_GROUP.toString(),
                propertyGroupName.replaceAll("[\\*|:\"'\\/\\\\]", "") + ".json").toFile();
        propertyGroup.getParentFile().mkdirs();

        try {
            Files.write(Paths.get(propertyGroup.getPath()), propertyGroupJson.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            logger.error("Unable to store Global Property Group", propertyGroupName, propertyGroup.getPath());
            throw new RuntimeException("Unable to store Global Property Group.", e);
        }
        return propertyGroup;
    }

    private void storePropertyGroupOnServer(File jsonFile) {
        try {
            String propertyGroupJson = FileUtils.readFileToString(jsonFile, "UTF-8");
            String propertyGroupName = JsonPath.parse(propertyGroupJson).read("$.label");
            restClient.importGlobalPropertyGroup(propertyGroupName, propertyGroupJson);
        } catch (IOException e) {
            throw new RuntimeException("Error reading from file: " + jsonFile.getPath(), e);
        }
    }
}
