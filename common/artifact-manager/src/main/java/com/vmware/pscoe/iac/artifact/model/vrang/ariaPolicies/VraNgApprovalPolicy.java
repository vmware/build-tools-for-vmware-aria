package com.vmware.pscoe.iac.artifact.model.vrang.ariaPolicies;

/*-
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

//{
//	"id": "c20d7ac6-0a7d-4bdc-9537-311f6279fd26",
//	"name": "sagfsdfg",
//	"typeId": "com.vmware.policy.approval",
//	"enforcementType": "HARD",
//	"orgId": "80813752-da65-423d-8e84-92c4c472284d",

//	"definition": {
//		"level": 1,
//		"actions": [
//			"Deployment.Create"
//		],
//		"approvers": [
//			"USER:vriccio@vmware.com"
//		],
//		"approvalMode": "ANY_OF",
//		"autoApprovalExpiry": 5,
//		"autoApprovalDecision": "APPROVE"
//	},
//	"criteria": {
//		"matchExpression": [
//			{
//				"key": "catalogItemId",
//				"operator": "eq",
//				"value": "90a8f966-ae7a-3ff2-9389-cd35ec10f8ff"
//			}
//		]
//	},
//	"createdAt": "2020-07-13T14:29:02.860810Z",
//	"createdBy": "vriccio@vmware.com",
//	"lastUpdatedAt": "2020-07-13T14:29:02.860810Z",
//	"lastUpdatedBy": "vriccio@vmware.com"
//}

import com.google.gson.JsonElement;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgDefinition;

public class VraNgApprovalPolicy extends VraNgPolicyBase {
	/**
		* Definition of the Content Sharing Policy.
	*/
	private ApprovalPolicyDefinition definition;

	private JsonElement criteria;

	/**
	 * Constructor VraNgContentSharingPolicy.
	 */
	public VraNgApprovalPolicy() {
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
	public VraNgApprovalPolicy(final String idIn, final String nameIn, final String typeIdIn,
							   final String projectIdIn, final String orgIdIn,
							   final String enforcementTypeIn, final String descriptionIn,
							   final ApprovalPolicyDefinition definitionIn,
							   final JsonElement criteria) {
		super(idIn, typeIdIn, nameIn, enforcementTypeIn, orgIdIn, projectIdIn, descriptionIn);
		this.definition = definitionIn;
		this.criteria = criteria;
	}

	/**
	 * Get the definition of the content sharing policy.
	 *
	 * @return content sharing policy definition
	 */
	public ApprovalPolicyDefinition getDefinition() {
		return definition;
	}

	/**
	 * Set the definition of the content sharing policy.
	 *
	 * @param definitionIn - definition of the content sharing policy
	 */
	public void setDefinition(final ApprovalPolicyDefinition definitionIn) {
		this.definition = definitionIn;
	}

	public JsonElement getCriteria() {
		return this.criteria;
	}

	public void setCriteria(JsonElement criteria) {
		this.criteria = criteria;
	}
}
