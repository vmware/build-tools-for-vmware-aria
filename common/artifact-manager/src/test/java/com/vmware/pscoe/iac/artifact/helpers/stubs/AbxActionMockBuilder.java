package com.vmware.pscoe.iac.artifact.helpers.stubs;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.mockito.Mockito;

import com.vmware.pscoe.iac.artifact.model.abx.AbxAction;
import com.vmware.pscoe.iac.artifact.model.abx.AbxDefinition;
import com.vmware.pscoe.iac.artifact.model.abx.Platform;

import static org.mockito.Mockito.when;
import java.util.Map;

public class AbxActionMockBuilder {

    private static final String DESCRIPTION = "Find Tags Action";
	private static final String RUNTIME = "python";
	private static final String NAME = "nic.abx";	
	private static final String ENTRYPOINT = "handler.handler";
	public static final String FAAS_PROVIDER = "aws";
	public static final Integer MEMORY_LIMIT = 150;
	public static final Integer TIMEOUT = 350;
	private static final Boolean SHARED = true;

    public static AbxAction buildAbxAction() throws IOException {

        AbxAction abxAction = Mockito.mock(AbxAction.class);
		abxAction.description = DESCRIPTION;

		abxAction.platform = new Platform();
		abxAction.platform.runtime = RUNTIME;
		abxAction.platform.entrypoint = ENTRYPOINT;

		abxAction.abx = new AbxDefinition();
		abxAction.abx.inputs = new LinkedHashMap<>();
		abxAction.abx.shared = SHARED;

		when(abxAction.getName()).thenReturn(NAME);
		when(abxAction.getBundleAsB64()).thenReturn(null);		

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
		result.put("compressedContent", null);
		result.put("shared", SHARED);

		return result;
	}
    
}
