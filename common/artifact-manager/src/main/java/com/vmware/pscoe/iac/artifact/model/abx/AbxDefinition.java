package com.vmware.pscoe.iac.artifact.model.abx;

import java.util.Map;

public class AbxDefinition {
    public boolean shared;              // optional
    public Map<String, String> inputs;  // optional
    public String[] inputSecrets;       // optional
    public String[] inputConstants;     // optional
}
