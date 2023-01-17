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
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vmware.pscoe.iac.artifact.PackageManager;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.rest.RestClientVra;
import com.vmware.pscoe.iac.artifact.model.vra.VraPackageDescriptor;

public class VraSubscriptionPackageStoreExtention implements PackageStoreExtention<VraPackageDescriptor> {

    private final Logger logger = LoggerFactory.getLogger(VraSubscriptionPackageStoreExtention.class);
    private final RestClientVra restClient;

    public VraSubscriptionPackageStoreExtention(RestClientVra restClient) {
        this.restClient = restClient;
    }

    @Override
    public Package exportPackage(Package serverPackage, VraPackageDescriptor vraPackageDescriptor, boolean dryrun) {
        logger.debug("vRA Workflow Subscription export extention is enabled.");
        if (vraPackageDescriptor == null) {
            logger.debug("vRA Workflow Subscription package export requires vRA package descriptor.");
            return serverPackage;
        }
        
        List<String> workflowSubscriptionNames = vraPackageDescriptor.getWorkflowSubscription();
        if (workflowSubscriptionNames == null) {
            logger.debug("No Workflow Subscriptions defined for export.");
            return serverPackage;
        }

        List<File> workflowSubscriptionFiles = new ArrayList<>();
        List<Map<String, Object>> subscriptions = restClient.getWorkflowSubscriptions();

        for (Map<String, Object> subscription : subscriptions) {
            String subscriptionName = (String)subscription.get("name");
            // Check if subscription name is listed in "content.yaml". If not - continue.
            if (!workflowSubscriptionNames.contains(subscriptionName)) { continue; }

            // Remove the subscription ID and tenant ID fields since they are environment specific
		    subscription.remove("id");
            subscription.remove("tenantId");

            // PrettyPrint JSON. Setting "Lenient" is important as vRA returns the JSON non-compliant form.  
            Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
            String subscriptionJson = gson.toJson(subscription);
                        
            if (dryrun) {
                logger.info(subscriptionJson);
                continue;
            }

            workflowSubscriptionFiles
                    .add(storeWorkflowSubscriptionOnFileSystem(serverPackage, subscriptionName, subscriptionJson));
        }

        try {
            new PackageManager(serverPackage).addToExistingZip(workflowSubscriptionFiles);
        } catch (IOException e) {
            throw new RuntimeException("Error adding files to zip", e);
        }

        return serverPackage;
    }

    @Override
    public Package importPackage(Package pkg, boolean dryrun) {
        logger.debug("vRA Workflow Subscription import extention is enabled.");
        if (dryrun) {
            logger.debug("vRA does not support Workflow Subscription validation through the REST API.");
            return pkg;
        }

        File tmp;
        try {
            tmp = Files.createTempDirectory("iac-workflow-subscriptions").toFile();
            new PackageManager(pkg).unpack(tmp);
        } catch (IOException e) {
            logger.error("Unable to extract package '{}' in temporary directory.", pkg.getFQName());
            throw new RuntimeException("Unable to extract pacakge.", e);
        }

        File subscriptionsFolder = Paths.get(tmp.getPath(), "workflow-subscription").toFile();
        if (subscriptionsFolder.exists()) {
            FileUtils.listFiles(subscriptionsFolder, new String[] { "json" }, false).stream()
                    .forEach(subscription -> storeSubscriptionOnServer(subscription));
        }
        return pkg;
    }

    private File storeWorkflowSubscriptionOnFileSystem(Package serverPackage, String subscriptionName,
            String subscriptionJson) {
        File store = new File(serverPackage.getFilesystemPath()).getParentFile();
        File subscription = Paths.get(store.getPath(), "workflow-subscription", subscriptionName + ".json").toFile();
        subscription.getParentFile().mkdirs();

        try {
            Files.write(Paths.get(subscription.getPath()), subscriptionJson.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            logger.error("Unable to store Workflow Subscription", subscriptionName, subscription.getPath());
            throw new RuntimeException("Unable to store Workflow Subscription.", e);
        }
        return subscription;
    }

    private void storeSubscriptionOnServer(File jsonFile) {
        try {
            String subscriptionName = FilenameUtils.removeExtension(jsonFile.getName());
            String subscriptionJson = FileUtils.readFileToString(jsonFile, "UTF-8");

            restClient.importSubscription(subscriptionName, subscriptionJson);
        } catch (IOException e) {
            throw new RuntimeException("Error reading from file: " + jsonFile.getPath(), e);
        }
    }
}
