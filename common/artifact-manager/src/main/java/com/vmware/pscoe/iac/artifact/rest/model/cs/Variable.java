package com.vmware.pscoe.iac.artifact.rest.model.cs;


public class Variable {

    String project;
    String name;
    String type;
    String description;
    String value;
    // String version;
    // String kind;



    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (name == null ? 0 : name.hashCode());
        hash = 31 * hash + (project == null ? 0 : project.hashCode());
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (this.getClass() != o.getClass())
            return false;
        Variable var = (Variable) o;

        return (name.equals(var.name)
                && project.equals(var.project));
    }

    @Override
    public String toString() {
        return String.format("Variable: name=%s, project=%s, type=%s", name, project, type);
    }
}
