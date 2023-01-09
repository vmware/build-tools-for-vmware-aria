package com.vmware.pscoe.iac.artifact.store.cs;

import com.vmware.pscoe.iac.artifact.configuration.ConfigurationCs;

import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.cs.CsPackageContent;
import com.vmware.pscoe.iac.artifact.model.cs.CsPackageDescriptor;
import com.vmware.pscoe.iac.artifact.rest.RestClientCs;
import static com.vmware.pscoe.iac.artifact.model.cs.CsPackageContent.ContentType.*;

/**
    Factory to select and setup the store (handler) and determine the order of execution.
 */
public class CsTypeStoreFactory {

    public static CsPackageContent.ContentType[] IMPORT_ORDER = {
            VARIABLE,
            CUSTOM_INTEGRATION,
            ENDPOINT,
            PIPELINE,
            GIT_WEBHOOK,
            DOCKER_WEBHOOK,
            GERRIT_LISTENER,
            GERRIT_TRIGGER


    };

    public static CsPackageContent.ContentType[] EXPORT_ORDER = {
            PIPELINE,
            ENDPOINT,
            CUSTOM_INTEGRATION,
            GIT_WEBHOOK,
            DOCKER_WEBHOOK,
            GERRIT_LISTENER,
            GERRIT_TRIGGER,
            VARIABLE
    };

    protected final RestClientCs restClient;
    protected final Package vraNgPackage;
    protected final ConfigurationCs config;
    protected final CsPackageDescriptor descriptor;

    protected CsTypeStoreFactory(RestClientCs restClient, Package vraNgPackage,
            ConfigurationCs config, CsPackageDescriptor descriptor) {
        this.restClient = restClient;
        this.vraNgPackage = vraNgPackage;
        this.config = config;
        this.descriptor = descriptor;
    }

    public static CsTypeStoreFactory withConfig(
            RestClientCs restClient, Package vraNgPackage, ConfigurationCs config,
            CsPackageDescriptor descriptor) {
        return new CsTypeStoreFactory(restClient, vraNgPackage, config, descriptor);

    }

    public ICsStore getStoreForType(CsPackageContent.ContentType type) {
        AbstractCsStore store = selectStore(type);
        store.init(restClient, vraNgPackage, config, descriptor);
        return store;
    }

    private static AbstractCsStore selectStore(CsPackageContent.ContentType type) {
        switch (type) {
            case PIPELINE:
                return new CsPipelineStore();
            case VARIABLE:
                return new CsVariableStore();
            case ENDPOINT:
                return new CsEndpointStore();
            case CUSTOM_INTEGRATION:
                return new CsCustomIntegrationStore();
            case GIT_WEBHOOK:
                return new CsGitWebhookStore();
            case DOCKER_WEBHOOK:
                return new CsDockerWebhookStore();
            case GERRIT_LISTENER:
                return new CsGerritListenerStore();
            case GERRIT_TRIGGER:
                return new CsGerritTriggerStore();
            default:
                throw new RuntimeException("unknown type: " + type);

        }
    }
}
