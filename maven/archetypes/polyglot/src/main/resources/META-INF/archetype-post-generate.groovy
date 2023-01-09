import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.DirectoryFileFilter
import org.apache.commons.io.filefilter.NotFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter

import java.nio.file.Path
import java.nio.file.Paths

// the path where the project got generated
Path projectPath = Paths.get(request.outputDirectory, request.artifactId)
// the properties available to the archetype
Properties properties = request.properties
String runtime = properties.get("runtime")

String licenseTechnicalPreview = request.properties.get("licenseTechnicalPreview")
String licenseUrl = request.properties.get("licenseUrl")
String licenseHeader = request.properties.get("licenseHeader")

if (licenseTechnicalPreview == "true" || licenseTechnicalPreview == "yes" || licenseTechnicalPreview == "y") {
    // Generating VMware Technical Preview licence
} else {
    File header = projectPath.resolve("license_data/_license/template_header.txt.ftl").toFile()
    File license = projectPath.resolve("license_data/_license/template_license.txt.ftl").toFile()

    String headerText = null
    String licenseText = null

    try {
        headerText = licenseHeader.toURL().getText()
    } catch (Exception ignored) {
        headerText = licenseHeader.toString()
    }

    try {
        licenseText = licenseUrl.toURL().getText()
    } catch (Exception ignored) {
    }

    header.write(headerText == 'null' ? "TODO: Define header text" : headerText)
    license.write(licenseText ?: "TODO: Define license text")
}

// gather all directories in src
def files = FileUtils.listFilesAndDirs(
        projectPath.resolve("src").toFile(),
        new NotFileFilter(TrueFileFilter.INSTANCE),
        DirectoryFileFilter.DIRECTORY)

// delete everything in src except the runtime directory
for (f in files) {
    if (f.name != "src" && f.name != runtime) {
        FileUtils.deleteDirectory(f)
    }
}

// keep node wrapper only for nodejs runtime
if (runtime != "nodejs") {
    FileUtils.deleteQuietly(projectPath.resolve("tsconfig.json").toFile())
}

// keep python dependency manifest for python runtime
if (runtime != "python") {
    FileUtils.deleteQuietly(projectPath.resolve("requirements.txt").toFile())
}

// copy everything from the runtime directory into src
FileUtils.copyDirectory(projectPath.resolve("src/" + runtime).toFile(), projectPath.resolve("src").toFile())

// delete the runtime directory
FileUtils.deleteDirectory(projectPath.resolve("src/" + runtime).toFile())
