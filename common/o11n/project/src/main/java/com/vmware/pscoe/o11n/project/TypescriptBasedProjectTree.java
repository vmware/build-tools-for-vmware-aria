package com.vmware.pscoe.o11n.project;

import com.vmware.pscoe.o11n.project.BaseProjectFileVisitor.WrappedException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.StringJoiner;

import static com.google.common.io.Files.getFileExtension;

public class TypescriptBasedProjectTree implements ProjectTree {
    private final Path src;
    private final Path root;

    public TypescriptBasedProjectTree(Path root) {
        this.root = root;
        this.src = root;
    }

    @Override
    public void walk(ProjectTreeVisitor visitor) throws Exception {
        if (!Files.exists(src)) {
            throw new ProjectTreeMismatchException(root + " is not a valid project root.");
        }

        TypescriptBasedFileVisitor fileVisitor = new TypescriptBasedFileVisitor(visitor);
        try {
            Files.walkFileTree(src, fileVisitor);
        } catch (WrappedException wrappedEx) {
            throw wrappedEx.getWrappedException();
        }
    }

    @Override
    public File workflow(String name, String categoryPath) {
        return pathToElement(name, categoryPath, "workflows", false);
    }

    @Override
    public File action(String name, String actionNamespace) {
        return pathToElement(name, actionNamespace, "actions", false);
    }

    @Override
    public File configuration(String name, String categoryPath) {
        return pathToElement(name, categoryPath, "configurations", false);
    }

    @Override
    public File resource(String name, String categoryPath) {
        return pathToElement(name, categoryPath, "resources", true);
    }

    @Override
    public File policy(String name, String categoryPath) {
        return pathToElement(name, categoryPath, "policies", false);
    }

    private File pathToElement(String name, String category, String kind, boolean skipFileExtension) {
        String[] categoryPath = category.split("\\.");
        StringJoiner pathJoiner = new StringJoiner(File.separator);
        pathJoiner.add("main");
        pathJoiner.add(kind);
        Arrays.stream(categoryPath).forEach(pathJoiner::add);
        String filename = String.format("%s%s", toValidFileName(name), skipFileExtension ? "" : ".xml");
        return Paths.get(src.toString(), pathJoiner.toString(), filename).toFile();
    }

    private String toValidFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9_\\-\\s\\.]", "").trim();
    }


    private boolean isTest(Path current) {
        return isChild("test", current);
    }

    private boolean isScript(Path current) {
        return isChild("js/src/main/resources", current);
    }

    private boolean isWorkflow(Path current) {
        return isChild("xml/src/main/resources/Workflow", current);
    }

    private boolean isResource(Path current) {
        return isChild("xml/src/main/resources/ResourceElement", current);
    }

    private boolean isConfiguration(Path current) {
        return isChild("xml/src/main/resources/ConfigurationElement", current);
    }

    private boolean isPolicy(Path current) {
        return isChild("xml/src/main/resources/PolicyTemplate", current);
    }

    private boolean isChild(String parent, Path current) {
        Path parentPath = Paths.get(parent);
        Path relativeCurrent = src.relativize(current);
        return relativeCurrent.startsWith(parentPath) && !relativeCurrent.endsWith(parentPath);
    }

    private boolean isElementInfo(File file) {
        return file.getName().toLowerCase().endsWith(".element_info.xml");
    }

    private boolean isXml(File file) {
        return getFileExtension(file.getName()).equalsIgnoreCase("xml");
    }

    private class TypescriptBasedFileVisitor extends BaseProjectFileVisitor {

        public TypescriptBasedFileVisitor(ProjectTreeVisitor visitor) {
            super(visitor);
        }

        @Override
        public boolean haveToVisitDirectory(Path dir) {
            return isScript(dir) || isWorkflow(dir) || isResource(dir) || isConfiguration(dir) || isPolicy(dir) || isTest(dir);
        }

        @Override
        public void visitFile(Path filePath, String category) throws Exception {
            final File file = filePath.toFile();
            if (!isElementInfo(file)) {
                if (isWorkflow(filePath) && isXml(file)) {
                    visitor.visitWorkflow(file, category);
                } else if (isScript(filePath)) {
                    visitor.visitScriptModule(file, category);
                } else if (isConfiguration(filePath) && isXml(file)) {
                    visitor.visitConfigurationElement(file, category);
                } else if (isResource(filePath)) {
                    visitor.visitResourceElement(file, category);
                } else if (isPolicy(filePath) && isXml(file)) {
                    visitor.visitPolicyTemplate(file, category);
                } else if (isTest(filePath)) {
                    visitor.visitTest(file, category);
                }
            }
        }
    }
}
