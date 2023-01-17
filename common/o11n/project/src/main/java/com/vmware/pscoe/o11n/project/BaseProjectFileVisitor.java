package com.vmware.pscoe.o11n.project;

/*
 * #%L
 * o11n-project
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
