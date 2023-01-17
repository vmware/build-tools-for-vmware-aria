package com.vmware.pscoe.iac.artifact.store.vrang;

import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPackageContent;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPackageDescriptor;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;
import static com.vmware.pscoe.iac.artifact.model.vrang.VraNgPackageContent.ContentType.*;

/**
    Factory to select and setup the store (handler) and determine the order of execution.
 */
public class VraNgTypeStoreFactory {

	public static VraNgPackageContent.ContentType[] IMPORT_ORDER = {
		PROPERTY_GROUP,
		CONTENT_SOURCE,
		CUSTOM_RESOURCE,
		RESOURCE_ACTION,
		BLUEPRINT,
		SUBSCRIPTION,
		REGION_MAPPING,
		CATALOG_ENTITLEMENT,
		CATALOG_ITEM
	};

	public static VraNgPackageContent.ContentType[] EXPORT_ORDER = {
		PROPERTY_GROUP,
		CONTENT_SOURCE,
		CUSTOM_RESOURCE,
		RESOURCE_ACTION,
		BLUEPRINT,
		SUBSCRIPTION,
		REGION_MAPPING,
		CATALOG_ENTITLEMENT,
		CATALOG_ITEM
	};

    protected final RestClientVraNg restClient;
    protected final Package vraNgPackage;
    protected final ConfigurationVraNg config;
    protected final VraNgPackageDescriptor descriptor;

    protected VraNgTypeStoreFactory(RestClientVraNg restClient, Package vraNgPackage,
            ConfigurationVraNg config, VraNgPackageDescriptor descriptor) {
        this.restClient = restClient;
        this.vraNgPackage = vraNgPackage;
        this.config = config;
        this.descriptor = descriptor;
    }

    public static VraNgTypeStoreFactory withConfig(
            RestClientVraNg restClient, Package vraNgPackage, ConfigurationVraNg config,
            VraNgPackageDescriptor descriptor) {
        return new VraNgTypeStoreFactory(restClient, vraNgPackage, config, descriptor);

    }

    public IVraNgStore getStoreForType(VraNgPackageContent.ContentType type) {
        AbstractVraNgStore store = selectStore(type);
        store.init(restClient, vraNgPackage, config, descriptor);
        return store;
    }

    private static AbstractVraNgStore selectStore(VraNgPackageContent.ContentType type) {
        switch (type) {
			case CATALOG_ITEM:
                return new VraNgCatalogItemStore();
            case CONTENT_SOURCE:
                return new VraNgContentSourceStore();
            case PROPERTY_GROUP:
                return new VraNgPropertyGroupStore();
            case BLUEPRINT:
                return new VraNgBlueprintStore();
            case SUBSCRIPTION:
                return new VraNgSubscriptionStore();
            case REGION_MAPPING:
                return new VraNgRegionalContentStore();
            case CATALOG_ENTITLEMENT:
                return new VraNgEntitlementStore();
            case CUSTOM_RESOURCE:
                return new VraNgCustomResourceStore();
            case RESOURCE_ACTION:
                return new VraNgResourceActionStore();
            default:
                throw new RuntimeException("unknown type: " + type);
        }
    }
}
