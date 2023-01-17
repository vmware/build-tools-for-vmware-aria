package com.vmware.pscoe.iac.artifact;

import com.vmware.pscoe.iac.artifact.VraNgReleaseManager;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgBlueprint;
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
