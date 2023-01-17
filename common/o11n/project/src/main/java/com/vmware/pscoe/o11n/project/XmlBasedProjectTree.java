package com.vmware.pscoe.o11n.project;

import static com.google.common.io.Files.getFileExtension;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.StringJoiner;

import com.vmware.pscoe.o11n.project.BaseProjectFileVisitor.WrappedException;

public class XmlBasedProjectTree implements ProjectTree {
    private final Path src;
    private final Path pom;
    private final Path root;
    private String productVersion;

    public XmlBasedProjectTree(Path root) {
        this.root = root;
        src = Paths.get(root.toString(), "src");
        pom = Paths.get(root.toString(), "pom.xml");
    }

    @Override
    public void walk(ProjectTreeVisitor visitor) throws Exception {
        if (!Files.exists(src)) {
            throw new ProjectTreeMismatchException(root + " is not a valid XML project root.");
        }

        XmlBasedFileVisitor fileVisitor = new XmlBasedFileVisitor(visitor);
        try {
            Files.walkFileTree(src, fileVisitor);
        } catch (WrappedException wrappedEx) {
            throw wrappedEx.getWrappedException();
        }
    }

    @Override
    public File workflow(String name, String categoryPath) {
        return pathToElement(name, categoryPath, "Workflow", false);
    }

    @Override
    public File action(String name, String actionNamespace) {
        return pathToElement(name, actionNamespace, "ScriptModule", false);
    }

    @Override
    public File configuration(String name, String categoryPath) {
        return pathToElement(name, categoryPath, "ConfigurationElement", false);
    }

    @Override
    public File resource(String name, String categoryPath) {
        return pathToElement(name, categoryPath, "ResourceElement", true);
    }

    @Override
    public File policy(String name, String categoryPath) {
        return pathToElement(name, categoryPath, "PolicyTemplate", false);
    }
   
    public String getProductVersion() {
        return productVersion;
    }

    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    private File pathToElement(String name, String category, String kind, boolean skipFileExtension) {
        String[] categoryPath = category.split("\\.");
        StringJoiner pathJoiner = new StringJoiner(File.separator);
        pathJoiner.add("main");
        pathJoiner.add("resources");
        pathJoiner.add(kind);
        Arrays.stream(categoryPath).forEach(pathJoiner::add);
        String filename = String.format("%s%s", toValidFileName(name), skipFileExtension ? "" : ".xml");
        
        return Paths.get(src.toString(), pathJoiner.toString(), filename).toFile();
    }
    private String toValidFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9_\\-\\s\\.]", "").trim();
    }

    private boolean isScript(Path current) {
        return isChild("main/resources/ScriptModule", current);
    }

    private boolean isWorkflow(Path current) {
        return isChild("main/resources/Workflow", current);
    }

    private boolean isResource(Path current) {
        return isChild("main/resources/ResourceElement", current);
    }

    private boolean isConfiguration(Path current) {
        return isChild("main/resources/ConfigurationElement", current);
    }

    private boolean isPolicy(Path current) {
        return isChild("main/resources/PolicyTemplate", current);
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

    private class XmlBasedFileVisitor extends BaseProjectFileVisitor {

        public XmlBasedFileVisitor(ProjectTreeVisitor visitor) {
            super(visitor);
        }

        @Override
        public boolean haveToVisitDirectory(Path dir) {
            return isScript(dir) || isWorkflow(dir) || isResource(dir) || isConfiguration(dir) || isPolicy(dir);
        }

        @Override
        public void visitFile(Path filePath, String category) throws Exception {
            final File file = filePath.toFile();
            if  (isElementInfo(file)) {
                return;
            }            
            if (isWorkflow(filePath) && isXml(file)) {
                visitor.visitWorkflow(file, category);
            } else if (isScript(filePath) && isXml(file)) {
                visitor.visitScriptModule(file, category);
            } else if (isConfiguration(filePath) && isXml(file)) {
                visitor.visitConfigurationElement(file, category);
            } else if (isResource(filePath)) {
                visitor.visitResourceElement(file, category);
            } else if (isPolicy(filePath) && isXml(file)) {
                visitor.visitPolicyTemplate(file, category);
            }
        }
    }
}
