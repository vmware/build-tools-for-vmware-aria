package com.vmware.pscoe.iac.artifact.vcf.automation.models;

/*-
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 - 2026 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true) // Crucial: prevents crashes if VMware adds fields in future updates
public class VcfaSubscription implements Identifiable {
    private String id;
    private String name;
    private String type;
    private String eventTopicId;
    private String orgId;
    private String ownerId;
    private String subscriberId;
    private Boolean blocking;
    private String description;
    private String criteria;
    private Map<String, Object> constraints;
    private Integer timeout;
    private Boolean broadcast;
    private Integer priority;
    private Boolean disabled;
    private Boolean system;
    private Boolean contextual;
    private String runnableType;
    private String runnableId;
    private Map<String, Object> config; // Retained for backward compatibility

    public VcfaSubscription() {
        
    }

    // --- Standard Getters and Setters ---

    public String getId() { 
        return id; 
    }
    public void setId(String id) { 
        this.id = id; 
    }

    public String getName() { 
        return name; 
    }
    public void setName(String name) { 
        this.name = name; 
    }

    public String getType() { 
        return type; 
    }
    public void setType(String type) { 
        this.type = type; 
    }

    public String getEventTopicId() { 
        return eventTopicId; 
    }
    public void setEventTopicId(String eventTopicId) { 
        this.eventTopicId = eventTopicId; 
    }

    public String getOrgId() { 
        return orgId; 
    }
    public void setOrgId(String orgId) { 
        this.orgId = orgId; 
    }

    public String getOwnerId() { 
        return ownerId; 
    }
    public void setOwnerId(String ownerId) { 
        this.ownerId = ownerId; 
    }

    public String getSubscriberId() { 
        return subscriberId; 
    }
    public void setSubscriberId(String subscriberId) { 
        this.subscriberId = subscriberId; 
    }

    public Boolean getBlocking() { 
        return blocking; 
    }
    public void setBlocking(Boolean blocking) { 
        this.blocking = blocking; 
    }

    public String getDescription() { 
        return description; 
    }
    public void setDescription(String description) { 
        this.description = description; 
    }

    public String getCriteria() { 
        return criteria; 
    }
    public void setCriteria(String criteria) { 
        this.criteria = criteria; 
    }

    public Map<String, Object> getConstraints() { 
        return constraints; 
    }
    public void setConstraints(Map<String, Object> constraints) { 
        this.constraints = constraints; 
    }

    public Integer getTimeout() { 
        return timeout; 
    }
    public void setTimeout(Integer timeout) { 
        this.timeout = timeout; 
    }

    public Boolean getBroadcast() { 
        return broadcast; 
    }
    public void setBroadcast(Boolean broadcast) { 
        this.broadcast = broadcast; 
    }

    public Integer getPriority() { 
        return priority; 
    }
    public void setPriority(Integer priority) { 
        this.priority = priority; 
    }

    public Boolean getDisabled() { 
        return disabled; 
    }
    public void setDisabled(Boolean disabled) { 
        this.disabled = disabled; 
    }

    public Boolean getSystem() { 
        return system; 
    }
    public void setSystem(Boolean system) { 
        this.system = system; 
    }

    public Boolean getContextual() { 
        return contextual; 
    }
    public void setContextual(Boolean contextual) { 
        this.contextual = contextual; 
    }

    public String getRunnableType() { 
        return runnableType; 
    }
    public void setRunnableType(String runnableType) { 
        this.runnableType = runnableType; 
    }

    public String getRunnableId() { 
        return runnableId; 
    }
    public void setRunnableId(String runnableId) { 
        this.runnableId = runnableId; 
    }

    public Map<String, Object> getConfig() { 
        return config; 
    }
    public void setConfig(Map<String, Object> config) { 
        this.config = config; 
    }
}
