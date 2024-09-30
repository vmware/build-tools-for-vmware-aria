package com.vmware.pscoe.iac.artifact.store.vrang;

import java.util.List;
import org.slf4j.Logger;

import com.vmware.pscoe.iac.artifact.model.vrang.Identifiable;

public abstract class AbstractVraNgDeleteStore {
	protected Logger logger;

	/**
	 * This will delete all of the approvalPolicies that are present in the
	 * `content.yaml`
	 *
	 * If the policy does not exist on the server, then nothing will happen.
	 * 
	 * @TODO: Make it so no definition means delete everything if a flag is set
	 */
	public void deleteContent() {
		List<Identifiable> serverResources = this.getAllServerContents();
		List<String> items = this.getItemListFromDescriptor();

		if (items == null) {
			logger.info("No items found in descriptor. Skipping deletion.");
			return;
		}

		for (Identifiable resource : serverResources) {
			if (items.contains(resource.getName())) {
				logger.info("Deleting resource '{}'", resource.getName());
				this.deleteResourceById(resource.getId());
			}
		}
	}

	/**
	 * Used to fetch the store's data from the package descriptor
	 *
	 * @return list of items
	 */
	protected abstract List<String> getItemListFromDescriptor();

	protected abstract <T extends Identifiable> List<T> getAllServerContents();

	protected abstract void deleteResourceById(String resId);
}
