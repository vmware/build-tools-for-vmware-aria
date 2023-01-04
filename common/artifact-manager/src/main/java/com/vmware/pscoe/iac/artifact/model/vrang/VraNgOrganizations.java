
package com.vmware.pscoe.iac.artifact.model.vrang;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"items"})
public class VraNgOrganizations implements Serializable {
    private static final long serialVersionUID = -3313748896114761975L;

    @JsonProperty("items")
    private List<VraNgOrganization> items;

    @JsonProperty("items")
    public List<VraNgOrganization> getItems() {
        return items;
    }

    @JsonProperty("items")
    public void setItems(List<VraNgOrganization> items) {
        this.items = items;
    }
}
