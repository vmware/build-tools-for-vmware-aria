import java.nio.file.Path
import java.nio.file.Paths

Path projectPath = Paths.get(request.outputDirectory, request.artifactId)
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