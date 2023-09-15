package com.vmware.pscoe.iac.artifact.model.vrang.ariaPolicies;

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

import com.vmware.pscoe.iac.artifact.model.vrang.VraNgDefinition;

/**

*/

//{
//	"id": "91e1465d-6415-498f-bd55-2d4a45ba9f80",
//	"name": "new-policy-test",
//	"typeId": "com.vmware.policy.catalog.entitlement",
//	"enforcementType": "HARD",
//	"orgId": "80813752-da65-423d-8e84-92c4c472284d",
//	"projectId": "61c1698f-b755-48fb-b026-7ea6f9b032a2",
//
//	"definition": {
//	"entitledUsers": [
//		{
//			"items": [
//				{
//					"id": "2b21bfab-9d36-4ca0-9b5c-197995f865de",
//					"type": "CATALOG_SOURCE_IDENTIFIER"
//				},
//				{
//					"id": "924bb4b6-b3f6-4d4c-9560-146cba8eb03b",
//					"type": "CATALOG_SOURCE_IDENTIFIER"
//				},
//			],
//			"userType": "USER",
//			"principals": [
//				{
//					"type": "PROJECT",
//					"referenceId": ""
//				}
//			]
//		}
//	  ]
//	},

//	"createdAt": "2022-07-18T08:40:57.939782Z",
//	"createdBy": "mtopchieva@vmware.com",
//	"lastUpdatedAt": "2022-07-18T08:54:53.446772Z",
//	"lastUpdatedBy": "mtopchieva@vmware.com"
//}

public class VraNgContentSharingPolicy extends VraNgPolicyBase {
	/**
	 * Definition of the Content Sharing Policy.
	 */
	private VraNgDefinition definition;

	/**
	 * Constructor VraNgContentSharingPolicy.
	 */
	public VraNgContentSharingPolicy() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param idIn              Id
	 * @param nameIn            Name
	 * @param typeIdIn          Typeid
	 * @param projectIdIn       ProjectId
	 * @param orgIdIn           OrgId
	 * @param enforcementTypeIn enforcementType
	 * @param descriptionIn     description
	 * @param definitionIn      definition
	 */
	public VraNgContentSharingPolicy(final String idIn, final String nameIn, final String typeIdIn,
			final String projectIdIn, final String orgIdIn,
			final String enforcementTypeIn, final String descriptionIn,
			final VraNgDefinition definitionIn) {
		super(idIn, typeIdIn, nameIn, enforcementTypeIn, orgIdIn, projectIdIn, descriptionIn);
		this.definition = definitionIn;
	}

	/**
	 * Get the definition of the content sharing policy.
	 * 
	 * @return content sharing policy definition
	 */
	public VraNgDefinition getDefinition() {
		return definition;
	}

	/**
	 * Set the definition of the content sharing policy.
	 * 
	 * @param definitionIn - definition of the content sharing policy
	 */
	public void setDefinition(final VraNgDefinition definitionIn) {
		this.definition = definitionIn;
	}
}
