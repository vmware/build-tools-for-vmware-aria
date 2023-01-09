
package com.vmware.pscoe.iac.artifact.model.vrang;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "workflows" })
public class VraNgConfig implements Serializable {
    private static final long serialVersionUID = -346336798765081423L;

    @JsonProperty("workflows")
    private List<VraNgWorkflow> workflows = new ArrayList<>();

    @JsonProperty("workflows")
    public List<VraNgWorkflow> getWorkflows() {
        return workflows;
    }

    @JsonProperty("workflows")
    public void setWorkflows(List<VraNgWorkflow> workflows) {
        this.workflows = workflows;
    }
}
