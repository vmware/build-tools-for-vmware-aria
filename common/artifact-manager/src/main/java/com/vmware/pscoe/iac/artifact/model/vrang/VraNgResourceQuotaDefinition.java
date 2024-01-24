package com.vmware.pscoe.iac.artifact.model.vrang;

/*-
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 - 2024 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */

public class VraNgResourceQuotaDefinition {

	public VraNgLimits getProjectLevel() {
		return projectLevel;
	}

	public void setProjectLevel(VraNgLimits projectLevel) {
		this.projectLevel = projectLevel;
	}

	public VraNgLimits getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(VraNgLimits userLevel) {
		this.userLevel = userLevel;
	}

	/**
	 * "definition": {
	 *                 "projectLevel": {
	 *                     "limits": {
	 *                         "cpu": {
	 *                             "value": 234
	 *                         },
	 *                         "memory": {
	 *                             "unit": "GB",
	 *                             "value": 10.5
	 *                         },
	 *                         "storage": {
	 *                             "unit": "GB",
	 *                             "value": 111111
	 *                         },
	 *                         "instances": {
	 *                             "value": 22
	 *                         }
	 *                     },
	 *                     "userLevel": {
	 *                         "limits": {
	 *                             "cpu": {
	 *                                 "value": 22.4
	 *                             },
	 *                             "memory": {
	 *                                 "unit": "GB",
	 *                                 "value": 4342.2
	 *                             },
	 *                             "storage": {
	 *                                 "unit": "GB",
	 *                                 "value": 20.4
	 *                             },
	 *                             "instances": {
	 *                                 "value": 1.3
	 *                             }
	 *                         }
	 *                     }
	 *                 }
	 *             },
	 */
private VraNgLimits projectLevel;
private VraNgLimits userLevel;

}
