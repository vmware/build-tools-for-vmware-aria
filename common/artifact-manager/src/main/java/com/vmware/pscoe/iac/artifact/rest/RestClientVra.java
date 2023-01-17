package com.vmware.pscoe.iac.artifact.rest;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVra;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageContent.Content;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import com.vmware.pscoe.iac.artifact.model.vra.VraPackageContent;
import com.vmware.pscoe.iac.artifact.model.vra.VraPackageDescriptor;
import com.vmware.pscoe.iac.artifact.model.vra.VraPackageMemberType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

public class RestClientVra extends RestClientVraPrimitive {

	private static final PackageType packageType = PackageType.VRA;

	private final Logger logger = LoggerFactory.getLogger(RestClientVro.class);

	protected RestClientVra(ConfigurationVra configuration, RestTemplate restTemplate) {
		super(configuration, restTemplate);
	}

	public List<Package> getPackages() {
		try {
			return this.getPackagesPrimitive().stream().map(packageObject -> {
				File pkgFile = new File(packageObject.get("name") + "." + packageType.getPackageExtention());
				Package pkg = PackageFactory.getInstance(packageType, packageObject.get("id"), pkgFile);
				logger.debug("Found " + pkg + " on server");
				return pkg;
			}).collect(Collectors.toList());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private Package findPackage(Package vraPackage) {
		List<Package> srvPackages = findAllPacakges(Arrays.asList(vraPackage));
		if (!srvPackages.isEmpty()) {
			return PackageFactory.getInstance(packageType, srvPackages.get(0).getId(),
					new File(vraPackage.getFilesystemPath()));
		}
		return null;
	}

	private List<Package> findAllPacakges(List<Package> filesystemPackage) {
		return getPackages().stream().filter(srvPackage -> filesystemPackage.contains(srvPackage))
				.collect(Collectors.toList());
	}

	public Package exportPackage(Package filesystemPackage, boolean dryRun) {
		Package serverVraPackage = findPackage(filesystemPackage);
		if (serverVraPackage == null) {
			throw new RuntimeException(String.format("Package %s not found on server.", filesystemPackage));
		}

		try {
			this.exportPackagePrimitive(serverVraPackage, dryRun);
			return serverVraPackage;
		} catch (NumberFormatException | URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public void setBlueprintCustomForm(String bpId, String jsonBody) {
		try {
			setBlueprintCustomFormPrimitive(bpId, jsonBody);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not update Custom Form for Blueprint with ID '%s'.", bpId),
					e);
		}
	}

	public void activateBlueprintCustomForm(String bpId) {
		try {
			activateBlueprintCustomFormPrimitive(bpId);
		} catch (Exception e) {
			throw new RuntimeException(
					String.format("Could not activate Custom Form for Blueprint with ID '%s'.", bpId), e);
		}
	}

	public Package importPackage(Package filesystemVroPackage, boolean dryRun) {
	    VraPackageDescriptor descriptor = null;
	    try {
		    VraPackageContent content = this.importPackagePrimitive(filesystemVroPackage, dryRun);
		    descriptor = VraPackageDescriptor.getInstance(content);
		    // Required for package clean up procedures
		    // vRA does not create package when importing it
		    return this.createPackage(filesystemVroPackage, descriptor);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Package> importAllPackages(List<Package> vraPackages, boolean dryRun) {
		return vraPackages.stream().map(vraPackage -> this.importPackage(vraPackage, dryRun))
				.collect(Collectors.toList());
	}

	public List<Package> exportAllPackage(List<Package> vraPackages, boolean dryRun) {
		return vraPackages.stream().map(pkg -> exportPackage(pkg, dryRun)).collect(Collectors.toList());
	}

	public Package createPackage(Package vraPackage, VraPackageDescriptor vraPackageDescriptor)
			throws URISyntaxException {
		Package serverVraPackage = findPackage(vraPackage);
		if (serverVraPackage != null) {
			logger.debug(String.format("Package | %s found on server. Will recreate it.", serverVraPackage));
			deletePackagePrimitive(serverVraPackage);
		}

		List<String> vraPackageContentIds = new ArrayList<>();

		// Get IDs corresponding to the vraPackageDescriptor content
		for (VraPackageMemberType type : VraPackageMemberType.values()) {
			if (!type.isNativeContent()) {
				continue;
			}

			List<String> memberNames = vraPackageDescriptor.getMembersForType(type);
			if (memberNames == null || memberNames.isEmpty()) {
				continue;
			}

			// [contentObject{propertyName:propertyValue}, ...]
			List<Map<String, String>> content = this.getContentPrimitive(type.toString(), null);

			// {contentObjectType-contentObjectName : contentObjectId, ...}
			Map<String, String> contentMap = new HashMap<>();
			for (Map<String, String> contentObj : content) {
				String typeName = type + contentObj.get("name");
				String id = contentObj.get("id");
				contentMap.put(typeName, id);
			}

			for (String memberName : memberNames) {
				String id = contentMap.get(type + memberName);
				if (id == null) {
					logger.warn("Content with Type[" + type + "], Name[" + memberName + "] "
					+ "and supplied credentials cannot be found on the sever. Note that name is case sensitive.");
					continue;
				}
				vraPackageContentIds.add(id);
			}
		}

		this.createPackagePrimitive(vraPackage, vraPackageContentIds);
		return vraPackage;
	}
	
	public Package deletePackage(Package pkg, boolean withContent, boolean dryrun){
	    if(withContent) {
	        VraPackageContent pkgContents = super.getPackageContentPrimitive(pkg);
	        pkgContents.getContent().stream().forEach(c -> super.deleteContentPrimitive(c, dryrun));
	    }
	    if(!dryrun) {
	        super.deletePackagePrimitive(pkg);
	    }
        return pkg;
    }
	
	public void deleteContentPrimitive(Content content, boolean dryrun) {
	    super.deleteContentPrimitive(content, dryrun);
	}
  
    public VraPackageContent getPackageContentPrimitive(Package pkg) {
        return super.getPackageContentPrimitive(pkg);
	}
	
	public String getBlueprintCustomForm(String bpId) {
        return super.getBlueprintCustomFormPrimitive(bpId);
    }
    
    public List<Map<String, String>> getPackageContents(String pkgId) {
        return super.getPackageContentsPrimitive(pkgId);
	}
	
	public List<Map<String, Object>> getWorkflowSubscriptions() {
        return super.getWorkflowSubscriptionsPrimitive();
	}
	
	public void importSubscription(String subscriptionName, String jsonBody) {
		try {
			importSubscriptionPrimitive(subscriptionName, jsonBody);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not create or update Workflow Subscription with name '%s'.", subscriptionName),
					e);
		}
	}

	public List<Map<String, Object>> getGlobalPropertyDefinitions() {
		return super.getGlobalPropertyDefinitionsPrimitive();
	}

	public void importGlobalPropertyDefinition(String propertyDefinitionName, String jsonBody) {
		try {
			importGlobalPropertyDefinitionPrimitive(propertyDefinitionName, jsonBody);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not create or update Global Property Definition with name '%s'.", propertyDefinitionName), e);
		}
	}

	public List<Map<String, Object>> getGlobalPropertyGroups() {
        return super.getGlobalPropertyGroupsPrimitive();
	}

	public void importGlobalPropertyGroup(String propertyGroupName, String jsonBody) {
		try {
			importGlobalPropertyGroupPrimitive(propertyGroupName, jsonBody);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not create or update Global Property Group with name '%s'.", propertyGroupName), e);
		}
	}

    public Map<String, Object> getCatalogItemByName(String catalogItemName) {
        return getCatalogItemByNamePrimitive(catalogItemName);
	}
	
	public void setCatalogItem(Map<String, Object> catalogItem) {
		try {
			setCatalogItemPrimitive(catalogItem);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not create or update Catalog Item with name '%s'.", catalogItem.get("name")), e);
		}
	}

	public Map<String, Object> getCatalogServiceByName(String serviceName) {
        return getCatalogServiceByNamePrimitive(serviceName);
	}

	public Map<String, Object> getIcon(String iconId) {
        return getIconPrimitive(iconId);
	}
	
	public void setIcon(Map<String, Object> icon) {
		try {
			setIconPrimitive(icon);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not create or update Icon with ID '%s'.", icon.get("id")), e);
		}
	}
}
