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

import com.vmware.pscoe.iac.artifact.VraNgReleaseManager;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;
import com.vmware.pscoe.iac.artifact.aria.model.VraNgBlueprint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class VraNgReleaseManagerTest {

	private VraNgReleaseManager vraNgReleaseManager;
	private RestClientVraNg restClientVrang;
	private VraNgBlueprint bp;

	@BeforeEach
	void init() {
		restClientVrang = Mockito.mock(RestClientVraNg.class);
		vraNgReleaseManager = new VraNgReleaseManager(restClientVrang);
		bp = new VraNgBlueprint(
			"blueprint_id_1",
			"blueprint_name_1",
			"blueprint_content_1",
			"blueprint_desc_1",
			false
		);
	}

	@Test
	public void testWhenNextVersionReleaseFailsThenReleaseIsCalledExactlyTwoTimes() {
		// Given
		String latestVersion = "1";
		String newVersion = "2";
		String latestContent = "Latest content";
		when(restClientVrang.getBlueprintLastUpdatedVersion(bp.getId())).thenReturn(latestVersion);
		when(restClientVrang.getBlueprintVersionContent(bp.getId(), latestVersion)).thenReturn(latestContent);
		doThrow(new RuntimeException()).when(restClientVrang).releaseBlueprintVersion(bp.getId(), newVersion);

		// When
		vraNgReleaseManager.releaseNextVersion(bp);

		// Then
		verify(restClientVrang, Mockito.times(2)).releaseBlueprintVersion(Mockito.anyString(), Mockito.anyString());
	}
}
