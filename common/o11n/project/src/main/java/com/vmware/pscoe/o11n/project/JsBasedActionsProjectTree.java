package com.vmware.pscoe.o11n.project;

import com.vmware.pscoe.o11n.project.BaseProjectFileVisitor.WrappedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.Name;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.StringJoiner;

import static com.google.common.io.Files.getFileExtension;

public class JsBasedActionsProjectTree implements ProjectTree {
    private static final Logger logger = LogManager.getLogger(JsBasedActionsProjectTree.class);
    private final Path src;
    private final Path pom;
    private boolean ignoreTests;
    private final Path root;

    public JsBasedActionsProjectTree(Path root) {
        this(root, false);
    }

    public JsBasedActionsProjectTree(Path root, boolean ignoreTests) {
        this.root = root;
        this.src = Paths.get(root.toString(), "src");
        this.pom = Paths.get(root.toString(), "pom.xml");
        this.ignoreTests = ignoreTests;
    }

    @Override
    public void walk(ProjectTreeVisitor visitor) throws Exception {
        if (!Files.exists(src)) {
            throw new ProjectTreeMismatchException(root + " is not a valid actions-only JS-based project root.");
        }

        JsBasedActionsFileVisitor fileVisitor = new JsBasedActionsFileVisitor(visitor);
        try {
            Files.walkFileTree(src, fileVisitor);
        } catch (WrappedException wrappedEx) {
            throw wrappedEx.getWrappedException();
        }
    }

    @Override
    public File workflow(String name, String categoryPath) {
        return null;
    }

    @Override
    public File action(String name, String actionNamespace) {
        String[] categoryPath = actionNamespace.split("\\.");
        StringJoiner pathJoiner = new StringJoiner(File.separator);
        pathJoiner.add("main");
        pathJoiner.add("resources");
        Arrays.stream(categoryPath).forEach(pathJoiner::add);
        String filename = String.format("%s.js", name);
        return Paths.get(src.toString(), pathJoiner.toString(), filename).toFile();
    }

    @Override
    public File configuration(String name, String categoryPath) {
        return null;
    }

    @Override
    public File resource(String name, String categoryPath) {
        return null;
    }

    @Override
    public File policy(String name, String categoryPath) {
        return null;
    }

    private boolean isTest(Path current) {
        return isChild("test/resources", current);
    }

    private boolean isScript(Path current) {
        return isChild("main/resources", current);
    }

    private boolean isJs(File current) {
        return getFileExtension(current.getName()).equalsIgnoreCase("js");
    }

    private boolean isJasmine(File current) throws IOException {
        AstRoot root = new Parser().parse(new FileReader(current), current.getName(), 1);

        Node child = root.getFirstChild();
        if (child instanceof ExpressionStatement) {
            Node expr = ((ExpressionStatement) child).getExpression();
            if (expr instanceof FunctionCall) {
                FunctionCall call = (FunctionCall) expr;
                Node target = call.getTarget();
                return (target instanceof Name) && target.getString().equals("describe");
            }
        }
        return false;
    }

    private boolean isChild(String parent, Path current) {
        Path parentPath = Paths.get(parent);
        Path relativeCurrent = src.relativize(current);
        return relativeCurrent.startsWith(parentPath) && !relativeCurrent.endsWith(parentPath);
    }

    private class JsBasedActionsFileVisitor extends BaseProjectFileVisitor {

        public JsBasedActionsFileVisitor(ProjectTreeVisitor visitor) {
            super(visitor);
        }

        @Override
        public boolean haveToVisitDirectory(Path dir) {
            return isScript(dir) || isTest(dir);
        }

        @Override
        public void visitFile(Path filePath, String category) throws Exception {
            final File file = filePath.toFile();
            if (isScript(filePath) && getFileExtension(file.getName()).equalsIgnoreCase("js")) {
                visitor.visitScriptModule(file, category);
            } else if (isTest(filePath)) {
                if (isJs(file)) {
                    if (isJasmine(file))
                        visitor.visitTest(file, category);
                    else if (!ignoreTests)
                        visitor.visitScriptModule(file, category);
                }
            }
        }
    }
}
