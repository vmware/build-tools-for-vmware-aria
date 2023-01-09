package com.vmware.pscoe.o11n.project;

import java.io.File;

public interface ProjectTree {
    /**
     * Walks through the whole project tree structure, passing files containing components (e.g. actions, workflows, configuration elements, resource elements).
     * @param visitor
     * @throws Exception
     */
    void walk(ProjectTreeVisitor visitor) throws Exception;

    File workflow(String name, String categoryPath);

    File action(String name, String actionNamespace);

    File configuration(String name, String categoryPath);

    File resource(String name, String categoryPath);

    File policy(String name, String categoryPath);
}
