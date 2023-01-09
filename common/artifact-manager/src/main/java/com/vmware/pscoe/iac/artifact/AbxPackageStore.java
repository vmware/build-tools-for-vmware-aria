package com.vmware.pscoe.iac.artifact;

import com.vmware.pscoe.iac.artifact.configuration.ConfigurationAbx;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageContent.Content;
import com.vmware.pscoe.iac.artifact.model.abx.AbxAction;
import com.vmware.pscoe.iac.artifact.model.abx.AbxConstant;
import com.vmware.pscoe.iac.artifact.model.abx.AbxPackageContent;
import com.vmware.pscoe.iac.artifact.model.abx.AbxPackageDescriptor;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgSecret;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AbxPackageStore extends GenericPackageStore<AbxPackageDescriptor> {

	private final Logger logger = LoggerFactory.getLogger(AbxPackageStore.class);

	private final String CUSTOM_FORM_SUFFIX = "_form.json";

	private final RestClientVraNg restClient;
	private final ConfigurationAbx config;

	protected AbxPackageStore(RestClientVraNg restClient, ConfigurationAbx config) {
		this.restClient = restClient;
		this.config = config;
	}

    @Override
    protected AbxPackageContent getPackageContent(Package pkg) {
        throw new UnsupportedOperationException(
                "Cloud Automation Services does not provide native support for packages.");
    }

    @Override
    public List<Package> getPackages() {
        throw new UnsupportedOperationException(
                "Cloud Automation Services does not provide native support for packages.");
    }

    @Override
    protected Package deletePackage(Package pkg, boolean withContent, boolean dryrun) {
        throw new NotImplementedException("Delete package is not implemented.");
    }

    @Override
    protected void deleteContent(Content content, boolean dryrun) {
        throw new NotImplementedException("Delete content is not implemented.");
    }

	@Override
	public List<Package> exportAllPackages(List<Package> abxPackages, boolean dryrun) {
		this.vlidateServer(abxPackages);

		List<Package> sourceEndpointPackages = abxPackages;

		if (sourceEndpointPackages.isEmpty()) {
			return new ArrayList<>();
		}

		List<Package> exportedPackages = new ArrayList<>();
		for (Package pkg : abxPackages) {
			AbxPackageDescriptor abxPackageDescriptor = AbxPackageDescriptor
					.getInstance(new File(pkg.getFilesystemPath()).getParentFile());
			exportedPackages.add(this.exportPackage(pkg, abxPackageDescriptor, dryrun));
		}

		return exportedPackages;
	}

	@Override
	public List<Package> importAllPackages(List<Package> pkg, boolean dryrun) {
		return this.importAllPackages(pkg,dryrun,false);
	}

	@Override
	public List<Package> importAllPackages(List<Package> abxPackages, boolean dryrun, boolean mergePackages) {
		this.validateFilesystem(abxPackages);

		List<Package> sourceEndpointPackages = abxPackages;
		if (sourceEndpointPackages.isEmpty()) {
			return new ArrayList<>();
		}

		List<Package> importedPackages = new ArrayList<>();
		for (Package pkg : sourceEndpointPackages) {
			importedPackages.add(this.importPackage(pkg, dryrun, mergePackages));
		}

		return importedPackages;
	}

    @Override
	public Package exportPackage(Package abxPackage, boolean dryrun) {
	    File abxPackageFile = new File(abxPackage.getFilesystemPath());
		return this.exportPackage(abxPackage, abxPackageFile, dryrun);
	}

    @Override
	public Package exportPackage(Package abxPackage, File abxPackageDescriptorParent, boolean dryrun) {
		AbxPackageDescriptor abxPackageDescriptor = AbxPackageDescriptor.getInstance(abxPackageDescriptorParent);
        return exportPackage(abxPackage, abxPackageDescriptor, dryrun);
	}

    /**
     * Main handler for exporting abx package based on package.json file
     * @param pkg abx package
     * @param packageDescriptor abx package descriptor file
     * @param dryrun
     * @return
     */
    @Override
    public Package exportPackage(Package pkg, AbxPackageDescriptor packageDescriptor, boolean dryrun) {
        logger.info(String.format(PackageStore.PACKAGE_EXPORT, pkg));

        // TODO: export ABX action
		logger.warn("ABX content pull is not supported yet");

        return pkg;
    }

	/**
	 * Main handler for importing abx package.
	 * @param abxPackage ABX package
	 * @param dryrun
	 * @return
	 */
	@Override
	public Package importPackage(Package abxPackage, boolean dryrun, boolean mergePackages) {
		logger.info(String.format(PackageStore.PACKAGE_IMPORT, abxPackage));

		File tmp;
		try {
			tmp = Files.createTempDirectory("iac-package-import").toFile();
            logger.info("Created temp dir {}", tmp.getAbsolutePath());
			new PackageManager(abxPackage).unpack(tmp);
		} catch (IOException e) {
			logger.error("Unable to extract package '{}' in temporary directory.", abxPackage.getFQName());
			throw new RuntimeException("Unable to extract package.", e);
		}

		// build package descriptor and use it to import the action
		AbxPackageDescriptor pkgDescriptor = AbxPackageDescriptor.getInstance(tmp);
		importAction(pkgDescriptor, dryrun);

		return abxPackage;
	}

	/**
	 * Add ABX action constants to the payload
	 * @param actionToImport ABX action to import
	 */
	protected void addActionConstantsToPayload(AbxAction actionToImport) {
		if(actionToImport.abx.inputConstants != null && actionToImport.abx.inputConstants.length > 0) {
			logger.debug("Number of definied constants: " + actionToImport.abx.inputConstants.length);
			for (String name : actionToImport.abx.inputConstants) {
				AbxConstant abxConstant = this.restClient.getAbxConstant(name);
				if(abxConstant == null) {
					throw new RuntimeException("Unable to find action constant with name: " + name);
				}
				actionToImport.abx.inputs.put(String.format("secret:%s", abxConstant.id), "");
			}
		}
	}

	/**
	 * Add secrets to the payload
	 * @param actionToImport ABX action to import
	 */
	protected void addSecretsToPayload(AbxAction actionToImport) {
		if(actionToImport.abx.inputSecrets != null && actionToImport.abx.inputSecrets.length > 0) {
			logger.debug("Number of definied secrets: " + actionToImport.abx.inputSecrets.length);
			for (String name : actionToImport.abx.inputSecrets) {
				VraNgSecret secret = this.restClient.getSecret(name);
				if(secret == null) {
					throw new RuntimeException("Unable to find secret with name: " + name);
				}
				actionToImport.abx.inputs.put(String.format("psecret:%s", secret.id), "");
			}
		}
	}

	private void importAction(AbxPackageDescriptor pkgDescriptor, boolean dryrun) {

		// Get existing actions from server
		List<AbxAction> abxActionsOnServer = this.restClient.getAllAbxActions();
		Map<String, AbxAction> abxActionsOnServerByName = abxActionsOnServer.stream()
				.collect(Collectors.toMap(AbxAction::getName, item -> item));

		// Build payload
		AbxAction actionToImport = pkgDescriptor.getAction();

		if(actionToImport.abx.inputs == null) {
			actionToImport.abx.inputs = new HashMap<>();
		};

		addSecretsToPayload(actionToImport);
		addActionConstantsToPayload(actionToImport);

		// Issue REST request
		if (abxActionsOnServerByName.containsKey(actionToImport.getName())) {
			AbxAction actionToUpdate = abxActionsOnServerByName.get(actionToImport.getName());
			logger.info("Updating action: {} ({})", actionToImport.getName(), actionToUpdate.id);
			if (!dryrun) {
				this.restClient.updateAbxAction(actionToUpdate.id, actionToImport);
			} else {
				logger.info("Dryrun has been set to 'true'. Skipping actual update...");
			}

		} else {
			logger.info("Creating action: " + actionToImport.getName());
			if (!dryrun) {
				this.restClient.createAbxAction(actionToImport);
			} else {
				logger.info("Dryrun has been set to 'true'. Skipping actual create...");
			}
		}
	}
}
