package com.vmware.pscoe.o11n.project;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Stack;

abstract class BaseProjectFileVisitor implements FileVisitor<Path> {
    private final Stack<String> sofar = new Stack<>();
    protected final ProjectTreeVisitor visitor;

    public BaseProjectFileVisitor(ProjectTreeVisitor visitor) {
        this.visitor = visitor;
    }

    public abstract boolean haveToVisitDirectory(Path dir);

    public abstract void visitFile(Path filePath, String category) throws Exception;

    @Override
    public final FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if (haveToVisitDirectory(dir)) {
            sofar.push(dir.getFileName().toString());
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public final FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
        final String category = String.join(".", sofar);

        try {
            visitFile(filePath, category);
        } catch (Exception e) {
            throw new WrappedException(e);
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public final FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public final FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (haveToVisitDirectory(dir) && !sofar.empty()) {
            sofar.pop();
        }

        return FileVisitResult.CONTINUE;
    }

    static class WrappedException extends IOException {
        private final Exception wrappedEx;

        public WrappedException(Exception wrappedEx) {
            super(wrappedEx.getMessage(), wrappedEx.getCause());
            this.wrappedEx = wrappedEx;
        }

        public Exception getWrappedException() {
            return wrappedEx;
        }
    }
}
