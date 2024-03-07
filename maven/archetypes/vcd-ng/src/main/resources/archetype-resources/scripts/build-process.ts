const fs = require("fs");
const { XMLParser } = require("fast-xml-parser");

const manifestFile = "src/public/manifest.json";
const cycle = process.argv[2];
if (cycle === "prebuild") {
	fs.copyFileSync(manifestFile, `${manifestFile}.temp`);
	const parser = new XMLParser();
	const pomJson = parser.parse(fs.readFileSync("pom.xml").toString().replace(/\r|\n/g, ""));

	let manifestContent = fs.readFileSync(manifestFile).toString();
	manifestContent = manifestContent.replace("VERSION_PLACEHOLDER", pomJson.project.version);
	manifestContent = manifestContent.replace(
		"NAME_PLACEHOLDER",
		`${pomJson.project.groupId}.${pomJson.project.artifactId}`
	);
	manifestContent = manifestContent.replace(
		"URN_PLACEHOLDER",
		`${pomJson.project.groupId}.${pomJson.project.artifactId}`.replace(/\./g, ":")
	);

	fs.writeFileSync(manifestFile, manifestContent);
} else if (cycle === "postbuild") {
	fs.copyFileSync(`${manifestFile}.temp`, manifestFile);
	fs.rmSync(`${manifestFile}.temp`, {
		force: true
	});
} else {
	console.log("Unknown argument");
}
