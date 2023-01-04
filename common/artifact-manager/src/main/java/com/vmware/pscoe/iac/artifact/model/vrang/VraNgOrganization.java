
package com.vmware.pscoe.iac.artifact.model.vrang;

import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "name", "refLink"})
public class VraNgOrganization implements Serializable {
    private static final long serialVersionUID = -3313748896114761975L;

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("refLink")
    private String refLink;

    @JsonProperty("id")
    public String getId() {
        if (id == null) {
            this.id = refLink.substring(refLink.lastIndexOf("/") + 1);
        }
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public String getRefLink() {
        return refLink;
    }

    public void setRefLink(String refLink) {
        this.refLink = refLink;
    }
}
