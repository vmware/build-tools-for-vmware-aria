package com.vmware.pscoe.maven.plugins;

/*
 * #%L
 * vrops-package-maven-plugin
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

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.vmware.pscoe.iac.artifact.PackageManager;
import com.vmware.pscoe.iac.artifact.VropsPackageStore;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationException;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import com.vmware.pscoe.iac.artifact.model.vrops.VropsPackageDescriptor;

import edu.emory.mathcs.backport.java.util.Arrays;

@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE)
public class PackageMojo extends AbstractMojo {
	/**
	 * Policy metadata file name.
	 */
    private static final String POLICY_METADATA_FILE_NAME = "policiesMetadata.vrops.json";
	/**
	 * Dashboard sharing metadata file name.
	 */
    private static final String DASHBOARD_SHARE_METADATA_FILENAME = "metadata/dashboardSharingMetadata.vrops.json";
	/**
	 * Dashboard user activation metadata file name.
	 */
    private static final String DASHBOARD_USER_ACTIVATE_METADATA_FILENAME = "metadata/dashboardUserActivationMetadata.vrops.json";
	/**
	 * Dashboard group activation metadata file name.
	 */
    private static final String DASHBOARD_GROUP_ACTIVATE_METADATA_FILENAME = "metadata/dashboardGroupActivationMetadata.vrops.json";
	/**
	 * Zip file type.
	 */
    private static final String ZIP_FILE_TYPE = "zip";
	/**
	 * XML file type.
	 */
    private static final String XML_FILE_TYPE = "xml";
	/**
	 * JSON file type.
	 */
    private static final String JSON_FILE_TYPE = "json";
    /**
	 * Content yaml file name.
	 */
    private static final String CONTENT_YAML_FILE_NAME = "content.yaml";
	/**
	 * Project build directory.
	 */
    @Parameter(defaultValue = "${project.build.directory}", readonly = true)
    private File directory;
	/**
	 * Project handle.
	 */
    @Parameter(defaultValue = "${project}")
    private MavenProject project;

	/**
	 * Execute the vROPs package MoJo, that will generate the target bundle with the assets defined in the content.yaml file.
	 *
	 * @throws MojoExecutionException MojoFailureException if package creation fails.
	 */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        MavenProjectPackageInfoProvider pkgInfoProvider = new MavenProjectPackageInfoProvider(project);
        File contentsYamlFile = new File(project.getBasedir(), CONTENT_YAML_FILE_NAME);
        VropsPackageDescriptor contentsYaml = VropsPackageDescriptor.getInstance(contentsYamlFile);

        getLog().info("vROps Package Plugin: Executing in Project Base: " + project.getBasedir());
        File pkgFile = new File(directory, pkgInfoProvider.getPackageName() + "." + PackageType.VROPS.getPackageExtention());
        getLog().info("vROps Package Plugin: Target Package File:      \"" + pkgFile.getAbsolutePath() + "\"");

        File filteredDir = new File(pkgInfoProvider.getTargetDirectory(), "vrops");
        try {
            filterSourcesByContentYaml(pkgInfoProvider.getSourceDirectory(), contentsYaml, filteredDir);
            Package pkg = PackageFactory.getInstance(PackageType.VROPS, pkgFile);
            // add content.yaml to the target package.
            addContentYamlFile(contentsYamlFile, filteredDir);
            getLog().info("vROps Package Plugin: Packaging bundle from: \"" + filteredDir + "\"");
            new PackageManager(pkg).pack(filteredDir);
            project.getArtifact().setFile(pkgFile);
            getLog().info("vROps Package Plugin: Artifact:              \"" + pkgFile.getAbsolutePath() + "\"");
         } catch (IOException e) {
            String message = "Error creating vROps bundle. (" + e.getClass().getName() + " : " + e.getLocalizedMessage() + ").";
            throw new MojoExecutionException(e, message, message);
        }
    }

	/**
	 * Filter the assets based on the definitions in the content.yaml file.
	 *
	 * @param sources source directory.
	 * @param contentYaml package descriptor based on the content.yaml file.
	 * @param assetDir directory that contains all assets (dashboards, views, supermetrics, etc).
	 * @return directory with filtered resources.
	 * @throws IOException if the copying files fails.
	 */
    private File filterSourcesByContentYaml(final File sources, final VropsPackageDescriptor contentYaml, final File assetDir) throws IOException {
        if (assetDir.exists()) {
            FileUtils.deleteDirectory(assetDir);
        }
        if (!assetDir.exists() && !assetDir.mkdirs()) {
            throw new IOException("Cannot create directory \"" + assetDir + "\" or some of its parents. Please check file system permisions.");
        }

        final File srcDashboardsDir = new File(sources, "dashboards");
        final File destDashboardsDir = new File(assetDir, "dashboards");
        final File srcViewsDir = new File(sources, "views");
        final File destViewsDir = new File(assetDir, "views");
        final File srcReportsDir = new File(sources, "reports");
        final File destReportsDir = new File(assetDir, "reports");
        final File srcAlertDefsDir = new File(sources, "alert_definitions");
        final File destAlertDefsDir = new File(assetDir, "alert_definitions");
        final File srcSymptomDefsDir = new File(sources, "symptom_definitions");
        final File destSymptomDefsDir = new File(assetDir, "symptom_definitions");
        final File srcPoliciesDir = new File(sources, "policies");
        final File destPoliciesDir = new File(assetDir, "policies");
        final File srcSuperMetricsDir = new File(sources, "supermetrics");
        final File destSuperMetricsDir = new File(assetDir, "supermetrics");
        final File srcRecommendationsDir = new File(sources, "recommendations");
        final File destRecommendationsDir = new File(assetDir, "recommendations");
        final File srcMetricConfigsDir = new File(sources, "metricconfigs");
        final File destMetricConfigsDir = new File(assetDir, "metricconfigs");
        final File srcCustomGroupDir = new File(sources, "custom_groups");
        final File destCustomGroupDir = new File(assetDir, "custom_groups");

        destDashboardsDir.mkdirs();
        destViewsDir.mkdirs();
        destReportsDir.mkdirs();
        destPoliciesDir.mkdirs();
        destRecommendationsDir.mkdir();
        destSuperMetricsDir.mkdirs();
        destMetricConfigsDir.mkdirs();
        destCustomGroupDir.mkdirs();

        final List<String> dashboardsToPackage = contentYaml.getDashboard() == null ? Collections.emptyList() : contentYaml.getDashboard();
        final List<String> viewsToPackage = contentYaml.getView() == null ? Collections.emptyList() : contentYaml.getView();
        final List<String> reportsToPackage = contentYaml.getReport() == null ? Collections.emptyList() : contentYaml.getReport();
        final List<String> policiesToPackage = contentYaml.getPolicy() == null ? Collections.emptyList() : contentYaml.getPolicy();
        final List<String> alertDefinitionsToPackage = contentYaml.getAlertDefinition() == null ? Collections.emptyList() : contentYaml.getAlertDefinition();
        final List<String> symptomDefinitionsToPackage = contentYaml.getSymptomDefinition() == null ? Collections.emptyList() : contentYaml.getSymptomDefinition();
        final List<String> recommendationsToPackage = contentYaml.getRecommendation() == null ? Collections.emptyList() : contentYaml.getRecommendation();
        final List<String> superMetricsToPackage = contentYaml.getSuperMetric() == null ? Collections.emptyList() : contentYaml.getSuperMetric();
        final List<String> metricConfigsToPackage = contentYaml.getMetricConfig() == null ? Collections.emptyList() : contentYaml.getMetricConfig();
        final List<String> customGroupsToPackage = contentYaml.getCustomGroup() == null ? Collections.emptyList() : contentYaml.getCustomGroup();

        filterDashboards(srcDashboardsDir, destDashboardsDir, dashboardsToPackage);
        filterViews(srcViewsDir, destViewsDir, viewsToPackage);
        filterReports(srcReportsDir, destReportsDir, reportsToPackage);
        filterAlertDefinitions(srcAlertDefsDir, destAlertDefsDir, alertDefinitionsToPackage);
        filterSymptomDefinitions(srcSymptomDefsDir, destSymptomDefsDir, symptomDefinitionsToPackage);
        filterPolicies(srcPoliciesDir, destPoliciesDir, policiesToPackage);
        filterRecommendation(srcRecommendationsDir, destRecommendationsDir, recommendationsToPackage);
        filterSuperMetrics(srcSuperMetricsDir, destSuperMetricsDir, superMetricsToPackage);
        filterMetricConfigs(srcMetricConfigsDir, destMetricConfigsDir, metricConfigsToPackage);
        filterCustomGroups(srcCustomGroupDir, destCustomGroupDir, customGroupsToPackage);

        return assetDir;
    }

	/**
	 * Filter and copy the dashboards based on the content.yaml dashboard definitions.
	 *
	 * @param srcDashboardsDir source dashboard directory.
	 * @param destDashboardsDir destination dashboard directory.
	 * @param dashboardsToPackage list of dashboard names to be packaged.
	 * @throws IOException if the copying files fails.
	 */
    private void filterDashboards(final File srcDashboardsDir, final File destDashboardsDir, final List<String> dashboardsToPackage) throws IOException {
        if (dashboardsToPackage == null || dashboardsToPackage.isEmpty()) {
            return;
        }

        StringBuilder messages = new StringBuilder();
        for (String dashboard : dashboardsToPackage) {
            List<File> fileList = getAssetFiles(srcDashboardsDir, dashboard, JSON_FILE_TYPE);
            for (File file : fileList) {
                try {
                    FileUtils.copyFile(file, new File(destDashboardsDir, file.getName()));
                    filterResources(srcDashboardsDir, dashboard, destDashboardsDir);
                } catch (IOException e) {
                    messages.append(String.format("Unable to copy dashboard file '%s', error: '%s' %n", file.getName(), e.getMessage()));
                }
            }
        }
        // copy dashboard sharing metadata file for dashboards
        try {
            this.copyDashboardSharingMetadataFile(srcDashboardsDir, destDashboardsDir);
        } catch (IOException e) {
            File metadataFile = new File(srcDashboardsDir, DASHBOARD_SHARE_METADATA_FILENAME);
            messages.append(String.format("Unable to copy dashboard sharing metadata file '%s' : '%s'", metadataFile.getName(), e.getMessage()));
        }
        // copy dashboard activation metadata files for the dashboards (for users and groups)
        try {
            this.copyDashboardActivationMetadataFiles(srcDashboardsDir, destDashboardsDir);
        } catch (IOException e) {
            messages.append(String.format("Unable to copy dashboard activation metadata files: %s",  e.getMessage()));
        }
        if (messages.length() > 0) {
            throw new IOException(messages.toString());
        }
    }

	/**
	 * Filter and copy the super metrics based on the content.yaml super metrics definitions.
	 *
	 * @param srcSuperMetricsDir source super metrics directory.
	 * @param destSuperMetricsDir destination super metrics directory.
	 * @param superMetricsToPackage list of super metrics names to be packaged.
	 * @throws IOException if the copying files fails.
	 */
    private void filterSuperMetrics(final File srcSuperMetricsDir, final File destSuperMetricsDir, final List<String> superMetricsToPackage) throws IOException {
        if (superMetricsToPackage == null || superMetricsToPackage.isEmpty()) {
            return;
        }

        StringBuilder messages = new StringBuilder();
        for (String superMetric : superMetricsToPackage) {
            List<File> fileList = getAssetFiles(srcSuperMetricsDir, superMetric, JSON_FILE_TYPE);
            for (File file : fileList) {
                try {
                    FileUtils.copyFile(file, new File(destSuperMetricsDir, file.getName()));
                    filterResources(srcSuperMetricsDir, file.getName(), destSuperMetricsDir);
                } catch (IOException e) {
                    messages.append(String.format("Unable to copy supermetric file '%s', error: '%s' %n", file.getName(), e.getMessage()));
                }
            }
        }

        if (messages.length() > 0) {
            throw new IOException(messages.toString());
        }
    }

	/**
	 * Filter and copy the metric configs based on the content.yaml metric config definitions.
	 *
	 * @param srcMetricConfigsDir source metric configs directory.
	 * @param destMetricConfigsDir destination metric configs directory.
	 * @param metricConfigsToPackage list of super metric names to be packaged.
	 * @throws IOException if the copying files fails.
	 */
    private void filterMetricConfigs(File srcMetricConfigsDir, File destMetricConfigsDir, List<String> metricConfigsToPackage) throws IOException {
        if (metricConfigsToPackage == null || metricConfigsToPackage.isEmpty()) {
            return;
        }

        StringBuilder messages = new StringBuilder();
        for (String metricConfig : metricConfigsToPackage) {
            List<File> fileList = getAssetFiles(srcMetricConfigsDir, metricConfig, XML_FILE_TYPE);
            fileList = fileList.isEmpty() ? getAssetFiles(srcMetricConfigsDir, metricConfig, null) : fileList;
            for (File file : fileList) {
                try {
                    FileUtils.copyFile(file, new File(destMetricConfigsDir, file.getName()));
                    filterResources(srcMetricConfigsDir, file.getName(), destMetricConfigsDir);
                } catch (IOException e) {
                    messages.append(String.format("Unable to copy metric config file '%s', error: '%s' %n", file.getName(), e.getMessage()));
                }
            }
        }

        if (messages.length() > 0) {
            throw new IOException(messages.toString());
        }
    }

	/**
	 * Filter and copy the views based on the content.yaml view definitions.
	 *
	 * @param srcViewsDir source views directory.
	 * @param destViewsDir destination views directory.
	 * @param viewsToPackage list of view names to be packaged.
	 * @throws IOException if the copying files fails.
	 */
    private void filterViews(File srcViewsDir, File destViewsDir, List<String> viewsToPackage) throws IOException {
        if (viewsToPackage == null || viewsToPackage.isEmpty()) {
            return;
        }

        StringBuilder messages = new StringBuilder();
        for (String view : viewsToPackage) {
            List<File> fileList = getAssetFiles(srcViewsDir, view, XML_FILE_TYPE);
            for (File file : fileList) {
                try {
                    FileUtils.copyFile(file, new File(destViewsDir, file.getName()));
                } catch (IOException e) {
                    messages.append(String.format("Unable to copy view file '%s', error: '%s' %n", file.getName(), e.getMessage()));
                    continue;
                }
                String viewId;
                try {
                    viewId = VropsPackageStore.getViewId(file);
                    filterResources(srcViewsDir, "view." + viewId, destViewsDir);
                } catch (ConfigurationException | IOException e) {
                    messages.append(String.format("Unable to extract view id from file '%s', error: '%s' %n", file.getName(), e.getMessage()));
                }
            }
        }

        if (messages.length() > 0) {
            throw new IOException(messages.toString());
        }
    }

	/**
	 * Gets asset files from a specific directory based on their extension.
	 *
	 * @param dir directory to fetch files from.
	 * @param fileName name of the file.
	 * @param fileExtension extension of the file.
	 * @return List<File> list of fetched files.
	 */
    @SuppressWarnings("unchecked")
    private List<File> getAssetFiles(File dir, String fileName, String fileExtension) {
        List<File> retVal = new ArrayList<>();

        FileFilter fileFilter = StringUtils.isEmpty(fileExtension) ? new WildcardFileFilter(fileName) : new WildcardFileFilter(fileName + "." + fileExtension);
        File[] fileList;
        try {
            fileList = dir.listFiles(fileFilter);
        } catch (Exception e) {
            getLog().warn(String.format("Error when retrieving file listing for directory '%s' with file pattern '%s' : '%s'", directory.getName(), fileName,
                    e.getMessage()));

            return retVal;
        }
        if (fileList == null) {
            getLog().warn(String.format("No files were listed in directory '%s' matching file pattern '%s'", directory.getName(), fileName));

            return retVal;
        }
        retVal.addAll(Arrays.asList(fileList));

        return retVal;
    }

	/**
	 * Filter and copy the reports based on the content.yaml view definitions.
	 *
	 * @param srcReportsDir source reports directory.
	 * @param destReportsDir destination reports directory.
	 * @param reportsToPackage list of reports to be packaged.
	 * @throws IOException if the copying files fails.
	 */
    private void filterReports(File srcReportsDir, File destReportsDir, List<String> reportsToPackage) throws IOException {
        if (reportsToPackage == null || reportsToPackage.isEmpty()) {
            return;
        }

        StringBuilder messages = new StringBuilder();
        for (String report : reportsToPackage) {
            List<File> reportFiles = getAssetFiles(srcReportsDir, report, null);
            for (File reportFile : reportFiles) {
                File srcFile = new File(srcReportsDir, reportFile.getName());
                File destFile = new File(destReportsDir, reportFile.getName());
                try {
                    FileUtils.copyDirectory(srcFile, destFile);
                } catch (IOException e) {
                    messages.append(String.format("Unable to copy file for report '%s', error: '%s' %n", reportFile.getName(), e.getMessage()));
                }
            }
        }

        if (messages.length() > 0) {
            throw new IOException(messages.toString());
        }
    }

	/**
	 * Filter and copy the alert definitions based on the content.yaml view definitions.
	 *
	 * @param srcAlertDefsDir source alert definitions directory.
	 * @param destAlertDefsDir destination alert definitions directory.
	 * @param alertDefinitionsToPackage list of alert definitions to be packaged.
	 * @throws IOException if the copying files fails.
	 */
    private void filterAlertDefinitions(File srcAlertDefsDir, File destAlertDefsDir, List<String> alertDefinitionsToPackage) throws IOException {
        if (alertDefinitionsToPackage == null || alertDefinitionsToPackage.isEmpty()) {
            return;
        }

        StringBuilder messages = new StringBuilder();
        for (String alertDefinitionId : alertDefinitionsToPackage) {
            List<File> fileList = getAssetFiles(srcAlertDefsDir, alertDefinitionId, JSON_FILE_TYPE);
            for (File file : fileList) {
                try {
                    FileUtils.copyFile(file, new File(destAlertDefsDir, file.getName()));
                } catch (IOException e) {
                    messages.append(String.format("Unable to copy file for alert definition '%s', error: '%s' %n", file.getName(), e.getMessage()));
                }
            }
        }

        if (messages.length() > 0) {
            throw new IOException(messages.toString());
        }
    }

	/**
	 * Filter and copy the symptom definitions based on the content.yaml view definitions.
	 *
	 * @param srcSymptomDefsDir source symptom definitions directory.
	 * @param destSymptomDefsDir destination symptom definitions directory.
	 * @param symptomDefinitionsToPackage list of symptom definitions to be packaged.
	 * @throws IOException if the copying files fails.
	 */
    private void filterSymptomDefinitions(File srcSymptomDefsDir, File destSymptomDefsDir, List<String> symptomDefinitionsToPackage) throws IOException {
        if (symptomDefinitionsToPackage == null || symptomDefinitionsToPackage.isEmpty()) {
            return;
        }

        StringBuilder messages = new StringBuilder();
        for (String symptomDefinitionId : symptomDefinitionsToPackage) {
            List<File> fileList = getAssetFiles(srcSymptomDefsDir, symptomDefinitionId, JSON_FILE_TYPE);
            for (File file : fileList) {
                try {
                    FileUtils.copyFile(new File(file.getAbsolutePath()), new File(destSymptomDefsDir, file.getName()));
                } catch (IOException e) {
                    messages.append(String.format("Unable to copy file for symptom definition '%s', error: '%s' %n", file.getName(), e.getMessage()));
                }
            }
        }

        if (messages.length() > 0) {
            throw new IOException(messages.toString());
        }
    }

	/**
	 * Filter and copy the recommendations based on the content.yaml view definitions.
	 *
	 * @param srcRecommendationsDir source recommendations directory.
	 * @param destRecommendationsDir destination recommendations directory.
	 * @param recommendationsToPackage list of recommendations to be packaged.
	 * @throws IOException if the copying files fails.
	 */
    private void filterRecommendation(File srcRecommendationsDir, File destRecommendationsDir, List<String> recommendationsToPackage) throws IOException {
        if (recommendationsToPackage == null || recommendationsToPackage.isEmpty()) {
            return;
        }

        StringBuilder messages = new StringBuilder();
        for (String recommendationId : recommendationsToPackage) {
            List<File> fileList = getAssetFiles(srcRecommendationsDir, recommendationId, JSON_FILE_TYPE);
            for (File file : fileList) {
                try {
                    FileUtils.copyFile(file, new File(destRecommendationsDir, file.getName()));
                } catch (IOException e) {
                    messages.append(String.format("Unable to copy file for recommendation '%s', error: '%s' %n", file.getName(), e.getMessage()));
                }
            }
        }

        if (messages.length() > 0) {
            throw new IOException(messages.toString());
        }
    }

	/**
	 * Filter and copy the resources based on a prefix.
	 *
	 * @param srcDir source directory.
	 * @param prefix prefix of the file.
	 * @param destDir destination destination directory.
	 * @throws IOException if the copying files fails.
	 */
    private void filterResources(File srcDir, String prefix, File destDir) throws IOException {
        File resourcesDir = new File(srcDir, "resources");
        File[] files = resourcesDir.listFiles(); // One level deep

        if (files == null) {
            return;
        }

        for (File srcResourceFile : files) {
            if (!srcResourceFile.getName().endsWith(".properties")) {
                continue;
            }
            File destResourceDir = new File(destDir, "resources");
            if (!destResourceDir.exists()) {
                destResourceDir.mkdirs();
            }
            File destResourceFile = new File(destResourceDir, srcResourceFile.getName());
            try (FileOutputStream outputStream = new FileOutputStream(destResourceFile, true);) {
                VropsPackageStore.fileCopyFiltering(srcResourceFile, prefix, outputStream);
            }
        }
    }

	/**
	 * Filter and copy the custom groups based on the content.yaml view definitions.
	 *
	 * @param srcCustomGroupsDir source custom groups directory.
	 * @param destCustomGroupsDir destination custom groups directory.
	 * @param customGroupsToPackage list of custom groups to be packaged.
	 * @throws IOException if the copying files fails.
	 */
    private void filterCustomGroups(File srcCustomGroupsDir, File destCustomGroupsDir, List<String> customGroupsToPackage) throws IOException {
        File[] files = srcCustomGroupsDir.listFiles();
        if (files == null || files.length == 0) {
            return;
        }

        StringBuilder messages = new StringBuilder();
        for (String customGroup : customGroupsToPackage) {
            List<File> fileList = getAssetFiles(srcCustomGroupsDir, customGroup, JSON_FILE_TYPE);
            for (File file : fileList) {
                try {
                    FileUtils.copyFile(file, new File(destCustomGroupsDir, file.getName()));
                } catch (IOException e) {
                    messages.append(String.format("Unable to copy file for custom group '%s', error: '%s' %n", file.getName(), e.getMessage()));
                }
            }
        }

        if (messages.length() > 0) {
            throw new IOException(messages.toString());
        }
    }

	/**
	 * Filter and copy the policies based on the content.yaml view definitions.
	 *
	 * @param srcPoliciesDir source policies directory.
	 * @param destPoliciesDir destination policies directory.
	 * @param policiesToPackage list of policies to be packaged.
	 * @throws IOException if the copying files fails.
	 */
    private void filterPolicies(File srcPoliciesDir, File destPoliciesDir, List<String> policiesToPackage) throws IOException {
        if (policiesToPackage == null || policiesToPackage.isEmpty()) {
            return;
        }

        StringBuilder messages = new StringBuilder();
        for (String policy : policiesToPackage) {
            List<File> fileList = getAssetFiles(srcPoliciesDir, policy, ZIP_FILE_TYPE);
            for (File file : fileList) {
                try {
                    FileUtils.copyFile(new File(srcPoliciesDir, file.getName()), new File(destPoliciesDir, file.getName()));
                } catch (IOException e) {
                    messages.append(String.format("Unable to copy file for policy '%s', error: '%s' %n", file.getName(), e.getMessage()));
                }
            }
        }

        // copy policy metadata file for all policies
        try {
            copyPolicyMetadataFile(srcPoliciesDir, destPoliciesDir);
        } catch (IOException e) {
            File metadataFile = new File(srcPoliciesDir, POLICY_METADATA_FILE_NAME);
            messages.append(String.format("Unable to copy policiy metadata file '%s' : '%s'", metadataFile.getName(), e.getMessage()));
        }

        if (messages.length() > 0) {
            throw new IOException(messages.toString());
        }
    }

	/**
	 * Copy the policy metadata json file to the target directory.
	 *
	 * @param srcPoliciesDir source policies directory.
	 * @param destPoliciesDir destination policies directory.
	 * @throws IOException if the copying files fails.
	 */
    private void copyPolicyMetadataFile(File srcPoliciesDir, File destPoliciesDir) throws IOException {
        File policyMetadataFile = new File(srcPoliciesDir, POLICY_METADATA_FILE_NAME);
        FileUtils.copyFile(policyMetadataFile, new File(destPoliciesDir, POLICY_METADATA_FILE_NAME));
    }

	/**
	 * Copy the dashboard sharing metadata json file to the target directory.
	 *
	 * @param srcDashboardDir source dashboards directory.
	 * @param destDashboardDir destination dashboards directory.
	 * @throws IOException if the copying files fails.
	 */
    private void copyDashboardSharingMetadataFile(File srcDashboardDir, File destDashboardDir) throws IOException {
        File dashboardSharingMetadataFile = new File(srcDashboardDir, DASHBOARD_SHARE_METADATA_FILENAME);
        if (dashboardSharingMetadataFile.exists()) {
            FileUtils.copyFile(dashboardSharingMetadataFile, new File(destDashboardDir, DASHBOARD_SHARE_METADATA_FILENAME));
        }
    }
    
	/**
	 * Copy the dashboard activation metadata json file to the target directory.
	 *
	 * @param srcDashboardDir source dashboards directory.
	 * @param destDashboardDir destination dashboards directory.
	 * @throws IOException if the copying files fails.
	 */
    private void copyDashboardActivationMetadataFiles(File srcDashboardDir, File destDashboardDir) throws IOException {
        // users activation metadata file
    	File dashboardUserActivateFile = new File(srcDashboardDir, DASHBOARD_USER_ACTIVATE_METADATA_FILENAME);
        if (dashboardUserActivateFile.exists()) {
            FileUtils.copyFile(dashboardUserActivateFile, new File(destDashboardDir, DASHBOARD_USER_ACTIVATE_METADATA_FILENAME));
        }
        // groups activation metadata file
        File dashboardGroupActivateFile = new File(srcDashboardDir, DASHBOARD_GROUP_ACTIVATE_METADATA_FILENAME);
        if (dashboardGroupActivateFile.exists()) {
            FileUtils.copyFile(dashboardGroupActivateFile, new File(destDashboardDir, DASHBOARD_GROUP_ACTIVATE_METADATA_FILENAME));
        }        
    }

	/**
	 * Copy the content.yaml file itself to the target directory in order to be part of the target package.
	 *
	 * @param contentYamlFile content yaml file handle.
	 * @param targetDirectory target directory to be copied to.
	 * @throws IOException if the copying files fails.
	 */
    private void addContentYamlFile(File contentYamlFile, File targetDirectory) throws IOException {
		if (contentYamlFile.exists()) {
            FileUtils.copyFile(contentYamlFile, new File(targetDirectory, contentYamlFile.getName()));
		}
    }
}
