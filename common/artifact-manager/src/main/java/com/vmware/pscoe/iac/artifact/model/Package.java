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

public final class Package implements Comparable<Package> {
	/**
	 * The package type.
	 */
    private final PackageType type;

	/**
	 * The package id.
	 */
    private final String id;

	/**
	 * The package name.
	 */
    private final String name;

	/**
	 * The package version.
	 */
    private final String version;

	/**
	 * The package file system path.
	 */
    private String filesystemPath;

	/**
	 *
	 * @param packageType The package type
	 * @param packageId The package id
	 * @param packageName The package name
	 * @param packageVersion The package version
	 * @param packageFilesystemPath The package file system path
	 */
    protected Package(final PackageType packageType, final String packageId, final String packageName, final String packageVersion, final String packageFilesystemPath) {
        this.type = packageType;
        this.id = packageId;
        this.name = packageName;
        this.version = packageVersion;
        this.filesystemPath = packageFilesystemPath;
    }

	/**
	 *
	 * @return returns the package type
	 */
    public PackageType getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getFilesystemPath() {
        return filesystemPath;
    }

	public void setFilesystemPath(String filesystemPath) {
		this.filesystemPath = filesystemPath;
	}

    public boolean hasVersionQualifier() {
        return this.version != null && this.version.length() > 0;
    }

    public boolean isSnapshot() {
        return this.hasVersionQualifier() && this.version.toUpperCase().endsWith("-SNAPSHOT");
    }

    public String getFQName() {
        return name + (hasVersionQualifier() ? "-" + version : "");
    }

    @Override
    public int compareTo(Package b) {
        Package a = this;

        if (b == null) {
            throw new NullPointerException("Object to be compared is Null.");
        }

        if (!a.getClass().equals(b.getClass()) || !a.name.equals(b.name)) {
            throw new ClassCastException("Cannot compare the versions of packages with different names.");
        }

        if (a.version == null && b.version == null) {
            return 0;
        }

        if (a.version == null) {
            return -1;
        }

        if (b.version == null) {
            return 1;
        }
        
        return new Version(a.version).compareTo(new Version(b.version));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !this.getClass().equals(obj.getClass())) {
            return false;
        }
        return this.toString().equals(obj.toString());
    }

    @Override
    public String toString() {
        if (hasVersionQualifier()) {
            return getName() + "-" + getVersion() + "." + type.getPackageExtention();
        } else {
            return getName() + "." + type.getPackageExtention();
        }
    }

}
