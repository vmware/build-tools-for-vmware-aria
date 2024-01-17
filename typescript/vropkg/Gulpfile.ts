import * as fs from "fs-extra";
import * as gulp from "gulp";
import * as jasmine from "gulp-jasmine";
import * as tsc from "gulp-typescript";
import * as path from 'path';
import { execSync } from "child_process";

gulp.task("clean", async () => {
	await fs.remove("dist");
	await fs.remove(path.join('test', 'target-flat'))
	await fs.remove(path.join('test', 'target-flat.tmp'))
	await fs.remove(path.join('test', 'target-tree'))
	const certPem		= path.join('test', "cert.pem");
	const privateKeyPem	= path.join('test', "private_key.pem");
	const packageFile	= path.join('test', "com.vmware.pscoe.toolchain.package");
	fs.existsSync( certPem ) && fs.unlinkSync( certPem );
	fs.existsSync( privateKeyPem ) && fs.unlinkSync( privateKeyPem );
	fs.existsSync( packageFile ) && fs.unlinkSync( packageFile );
});

gulp.task("compile-prod", done => {
	return compile({
		declaration: true,
		removeComments: true
	});
});

gulp.task("package-prod", gulp.series(["compile-prod", () => {
	return gulp.src("conf/tsconfig.merge.json")
		.pipe(gulp.dest("dist"));
}]));

gulp.task("compile-e2e", (done) => {
	execSync("openssl req -newkey rsa:2048 -x509 -sha256 -days 3650 -subj \"/O=VMware/OU=PS/CN=CoE\" -out ./test/cert.pem -keyout ./test/private_key.pem -passout pass:VMware1!");

	generateBasePackage();
	generateAdditionalPackage();

	let project = tsc.createProject("conf/tsconfig.e2e.json", {
		declaration: true,
	});

	return project.src()
		.pipe(project())
		.pipe(gulp.dest("build/e2e/"));
});

gulp.task("test-e2e", gulp.series([
	"compile-prod",
	"compile-e2e",
	() => gulp.src("build/e2e/*.js")
		.pipe(jasmine({
			verbose: true,
			includeStackTrace: true
		}))
]));

gulp.task("build-prod", gulp.series(["package-prod", "test-e2e"]));

gulp.task("default", gulp.series(["clean", "build-prod"]));

function compile(settings: tsc.Settings): NodeJS.ReadWriteStream {
	let project = tsc.createProject("conf/tsconfig.json", settings);
	let stream = project.src();

	return stream.pipe(project()).pipe(gulp.dest("dist"));
}

function generateBasePackage() {
	const options = [
		path.join( 'bin', 'vropkg'),
		'--in', 'tree',
		'--out', 'flat',
		'--srcPath', path.join('test', 'com.vmware.pscoe.toolchain-expand'),
		'--destPath', path.join( 'test/tmp' ),
		'--privateKeyPEM', path.join('test', 'private_key.pem'),
		'--certificatesPEM', path.join('test', 'cert.pem'),
		'--version', '1.0.0',
		'--packaging', 'package',
		'--artifactId', 'proj-artifact',
		'--description', '',
		'--groupId', 'test.group',
		'--keyPass', "VMware1!"
	]

	execSync( options.join( " " ), {
		cwd: process.cwd(),
		env: process.env
	});

	fs.moveSync( path.join( 'test', "tmp", "test.group.proj-artifact-1.0.0.package" ), path.join( 'test', "com.vmware.pscoe.toolchain.package" ), { overwrite: true } )
}

function generateAdditionalPackage() {
	const options = [
		path.join( 'bin', 'vropkg'),
		'--in', 'tree',
		'--out', 'flat',
		'--srcPath', path.join('test', 'com.vmware.pscoe.vrbt-forms'),
		'--destPath', path.join( 'test/tmp' ),
		'--privateKeyPEM', path.join('test', 'private_key.pem'),
		'--certificatesPEM', path.join('test', 'cert.pem'),
		'--version', '1.0.0',
		'--packaging', 'package',
		'--artifactId', 'proj-artifact',
		'--description', '',
		'--groupId', 'custom.forms',
		'--keyPass', "VMware1!"
	]

	execSync( options.join( " " ), {
		cwd: process.cwd(),
		env: process.env
	});

	fs.moveSync( path.join( 'test', "tmp", "custom.forms.proj-artifact-1.0.0.package" ), path.join( 'test', "com.vmware.pscoe.vrbt.custom.interaction.package" ), { overwrite: true } )

}
