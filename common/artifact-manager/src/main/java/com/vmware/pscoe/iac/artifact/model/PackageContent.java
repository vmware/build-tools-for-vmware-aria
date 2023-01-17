package com.vmware.pscoe.iac.artifact.model;

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

import java.util.Collections;
import java.util.List;

public abstract class PackageContent<T extends PackageContent.ContentType> {
    
    public interface ContentType {}

    public static class Content<T> {
        
        private final T type;
        private final String id, name;
        
        public Content(T type, String id, String name) {
            this.type = type;
            this.id = id;
            this.name = name;
        }
        
        public T getType() {
            return type;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
        
        @SuppressWarnings("rawtypes")
        @Override
        public boolean equals(Object obj) {
            if (obj == null || !this.getClass().equals(obj.getClass())) {
                return false;
            }
            Content a = this, b = (Content)obj;
            return a.type.equals(b.type) && a.id.equalsIgnoreCase(b.id) && a.name.equalsIgnoreCase(b.name);
        }
        
        @Override
        public String toString() {
            return String.format("Type: %s Id: %s Name: %s", this.type, this.id, this.name);
        }
    }
    
    private final List<Content<T>> content;

    protected PackageContent(List<Content<T>> content) {
        this.content = Collections.unmodifiableList(content);
    }

    public List<Content<T>> getContent() {
        return content;
    }
    
}
