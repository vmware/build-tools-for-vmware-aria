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
package com.vmware.pscoe.iac.artifact.helpers.stubs;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.vmware.pscoe.iac.artifact.aria.automation.models.abx.Abx;
import com.vmware.pscoe.iac.artifact.aria.automation.models.abx.AbxAction;
import com.vmware.pscoe.iac.artifact.aria.automation.models.abx.Platform;

public final class AbxActionMockBuilder {
	public static final String FAAS_PROVIDER = "aws";
	public static final Integer MEMORY_LIMIT = 150;
	public static final Integer TIMEOUT = 350;

	private static final String DESCRIPTION = "Find Tags Action";
	private static final String RUNTIME = "python";
	private static final String NAME = "nic.abx";
	private static final String ENTRYPOINT = "handler.handler";
	private static final Boolean SHARED = true;

	private AbxActionMockBuilder() {
	}

	public static AbxAction buildAbxAction() throws IOException {
		AbxAction abxAction = new AbxAction();

		abxAction.setName(NAME);
		abxAction.setDescription(DESCRIPTION);

		Platform platform = new Platform();
		platform.setRuntime(RUNTIME);
		platform.setEntrypoint(ENTRYPOINT);
		platform.setRuntimeVersion(null);
		abxAction.setPlatform(platform);

		Abx abx = new Abx();
		abx.setInputs(new LinkedHashMap<>());
		abx.setShared(SHARED);
		abxAction.setAbx(abx);

		abxAction.setBundle(new byte[] {});

		return abxAction;
	}

	public static Map<String, Object> buildAbxActionMap() {
		Map<String, Object> result = new LinkedHashMap<>();
		result.put("actionType", "SCRIPT");
		result.put("name", NAME);
		result.put("description", DESCRIPTION);
		result.put("projectId", "");
		result.put("runtime", RUNTIME);
		result.put("entrypoint", ENTRYPOINT);
		result.put("inputs", new LinkedHashMap<>());
		result.put("compressedContent", "");
		result.put("shared", SHARED);
		result.put("runtimeVersion", null);

		return result;
	}
}
