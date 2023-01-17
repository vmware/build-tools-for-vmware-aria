package com.vmware.pscoe.iac.artifact;

import com.vmware.pscoe.iac.artifact.model.abx.AbxAction;
import com.vmware.pscoe.iac.artifact.model.abx.AbxActionVersion;
import com.vmware.pscoe.iac.artifact.model.abx.AbxPackageDescriptor;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgBlueprint;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AbxReleaseManager {

    RestClientVraNg restClient;

    private final Logger logger = LoggerFactory.getLogger(AbxReleaseManager.class);

    public AbxReleaseManager(RestClientVraNg restClient) {
        this.restClient = restClient;
    }

    public void releaseContent(String version, File baseDir) {

        logger.info("Creating package descriptor from: {}", baseDir.getAbsolutePath());
        AbxPackageDescriptor abxDescriptor = AbxPackageDescriptor.getInstance(baseDir);

        // Get existing actions from server
        List<AbxAction> abxActionsOnServer = this.restClient.getAllAbxActions();
        Map<String, AbxAction> abxActionsOnServerByName = abxActionsOnServer.stream()
                .collect(Collectors.toMap(AbxAction::getName, item -> item));

        AbxAction existingAction = abxActionsOnServerByName.get(abxDescriptor.getAction().getName());

        if (existingAction == null) {
            logger.error("Action {} does not exist on server. Cannot release!", abxDescriptor.getAction().getName());
            return;
        }

        // determine release version
        if (version.equals("auto")) {
            releaseNextVersion(existingAction);
        } else if (version.equals("project")) {
            releaseVersion(existingAction, abxDescriptor.getAction().version);
        } else {
            releaseVersion(existingAction, version);
        }
    }

    /**
     * Attempt to generate a next version and release it.
     * @param actionOnServer ABX action
     */
    protected void releaseNextVersion(AbxAction actionOnServer) {
        AbxActionVersion latestVersion = this.restClient.getAbxLastUpdatedVersion(actionOnServer);
        String nextVersion;
        if (latestVersion != null) {
            logger.debug("Latest version: {}", latestVersion.name);
            logger.debug("Latest version id: {}", latestVersion.id);
            nextVersion = this.getNextVersion(latestVersion.name);
        } else {
            logger.info("No previous version found. Creating initial version");
            nextVersion = this.getNextVersion(null);
        }

        logger.debug("Next version of action {}: {}", actionOnServer.getName(), nextVersion);
        this.releaseVersion(actionOnServer, nextVersion);
    }

    /**
     * Release a new version of the abx action.
     * @param actionOnServer ABX action
     * @param version new version
     */
    protected void releaseVersion(AbxAction actionOnServer, String version) {

        logger.info("Creating abx action version {}", version);
        AbxActionVersion newVersion = this.restClient.createAbxVersion(actionOnServer, version);

        logger.info("Releasing abx action version {}", newVersion.name);
        AbxActionVersion releasedVersion = this.restClient.releaseAbxVersion(actionOnServer, newVersion.id);

        logger.info("Version successfully released");
    }

    /**
     * Generate next version based on the previous version format.
     * Supported version formats are:
     * * MAJOR
     * * MAJOR.MINOR
     * * MAJOR.MINOR.PATCH
     * A datetime-based version will be returned if the previous version format does not match
     * any of the supported formats.
     * @param version previous version
     * @return next version
     */
    private String getNextVersion(String version) {

        if (version == null) {
            // create a version based on the date and time
            return getDateVersion();
        }

        Matcher major = Pattern.compile("([0-9]+)").matcher(version);
        Matcher majorMinor = Pattern.compile("([0-9]+)\\.([0-9]+)").matcher(version);
        Matcher majorMinorPatch = Pattern.compile("([0-9]+)\\.([0-9]+)\\.([0-9]+)").matcher(version);

        if (majorMinorPatch.matches()) {
            logger.debug("Detected version pattern MAJOR.MINOR.PATCH from {} with incrementable segment '{}'", version, majorMinorPatch.group(3));
            // increment the patch segment
            return majorMinorPatch.group(1) + "." + majorMinorPatch.group(2) + "." + (Integer.parseInt(majorMinorPatch.group(3)) + 1);
        } else if (majorMinor.matches()) {
            logger.debug("Detected version pattern MAJOR.MINOR from '{}' with incrementable segment '{}'", version, majorMinor.group(2));
            // increment the minor segment
            return majorMinor.group(1) + "." + (Integer.parseInt(majorMinor.group(2)) + 1);
        } else if (major.matches()) {
            logger.debug("Detected version pattern MAJOR from '{}' with incrementable segment '{}'", version, major.group(1));
            // increment the major segment
            return Integer.toString(Integer.parseInt(major.group(1)) + 1);
        } else {
            logger.debug("Could not determine version pattern from {}", version);
            return getDateVersion();
        }

    }

    /**
     * Create a version based on the current date and time.
     * @return datetime-based version
     */
    private String getDateVersion() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return dateFormat.format(date);
    }

}
