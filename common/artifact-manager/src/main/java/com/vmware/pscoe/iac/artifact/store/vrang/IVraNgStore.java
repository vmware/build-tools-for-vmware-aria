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
package com.vmware.pscoe.iac.artifact.store.vrang;

import java.io.File;

/**
 * Interface for VRA NG Store
 */
public interface IVraNgStore {
	/**
	 * Import content from source directory.
	 *
	 * @param sourceDirectory
	 */
	void importContent(File sourceDirectory);

	/**
	 * Export content to `src/...`
	 */
	void exportContent();

	/**
	 * Delete content from the server based on the `content.yaml`
	 */
	void deleteContent();
}
