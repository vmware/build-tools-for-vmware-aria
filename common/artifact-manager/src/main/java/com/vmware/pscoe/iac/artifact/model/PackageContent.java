package com.vmware.pscoe.iac.artifact.model;

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
