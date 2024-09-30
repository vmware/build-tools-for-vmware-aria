package com.vmware.pscoe.iac.artifact.model.vrang;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.vmware.pscoe.iac.artifact.model.Package;

/**
 * An adapter used to extract information from a package
 */
public class VraNgPackageAdapter {
	private Package pkg;

	public VraNgPackageAdapter(final Package pkg) {
		this.pkg = pkg;
	}

	/**
	 * This will return a descriptor for the given package
	 *
	 * The descriptor class is used to extract what kind of data is stored in the
	 * `content.yaml`
	 *
	 * @return the descriptor
	 * @throws IOException if the content.yaml file is not found or not readable
	 */
	public VraNgPackageDescriptor getDescriptor() throws IOException {
		Path packagePath = Paths.get(this.pkg.getFilesystemPath());
		Path parentDir = packagePath.getParent();
		Path contentYamlPath = parentDir.resolve("content.yaml");

		if (!Files.exists(contentYamlPath) || !Files.isReadable(contentYamlPath)) {
			throw new IOException("content.yaml file is not found or not readable at " + contentYamlPath);
		}

		return VraNgPackageDescriptor.getInstance(contentYamlPath.toFile());
	}
}
