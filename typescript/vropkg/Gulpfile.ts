import * as fs from "fs-extra";
import * as gulp from "gulp";
import * as tsc from "gulp-typescript";
import * as path from 'path';
import { execSync } from "child_process";
import PluginError from "plugin-error";
import through from "through2";

/** Recursively delete cache for object with given ID and its children */
function deleteRequireCache(id) {
	if (!id?.includes('node_modules')) {
		return;
	}

	const files = require.cache[id];

	if (files !== undefined) {
		for (const file of Object.keys(files.children)) {
			deleteRequireCache(files.children[file].id);
		}

		delete require.cache[id];
	}
}

/** Fixed (no onComplete) and simplified gulp-jasmine */
function gulpJasmineFixed() {
	const Jasmine = require("jasmine");
	const jasmineRunner = new Jasmine();
	jasmineRunner.exitOnCompletion = false;
	if (jasmineRunner.env.clearReporters) {
		jasmineRunner.env.clearReporters();
	}
	const jasmineReporters = require("jasmine-reporters");
	jasmineRunner.addReporter(new jasmineReporters.TerminalReporter({
		verbosity: 3,
		color: process.argv.indexOf('--no-color') === -1,
		showStack: true
	}));

	return through.obj((file, enc, cb) => {
		if (file.isNull()) {
			cb(null, file);
			return;
		}

		if (file.isStream()) {
			cb(new PluginError('vropkg-gulpfile', 'Streaming not supported'));
			return;
		}
		const resolvedPath = path.resolve(file.path);
		const modId = require.resolve(resolvedPath);
		deleteRequireCache(modId);

		jasmineRunner.addSpecFile(resolvedPath);

		cb(null, file);
	}, function (cb) {
		const self = this;

		try {
			if (jasmineRunner.helperFiles) {
				for (const helper of jasmineRunner.helperFiles) {
					const resolvedPath = path.resolve(helper);
					const modId = require.resolve(resolvedPath);
					deleteRequireCache(modId);
				}
			}

			const result: Promise<any> = jasmineRunner.execute();
			result.then(result => {
				if (result.overallStatus === 'passed') {
					console.log('All specs have passed');
					self.emit('jasmineDone', true);
					cb();
				} else {
					console.log('At least one spec has failed');
					cb(new PluginError('vropkg-gulpfile', 'Tests failed', { showStack: false }));
				}
			})
		} catch (err) {
			cb(new PluginError('vropkg-gulpfile', err, { showStack: true }));
		}
	});
};


function toPathArg(...args: string[]) {
	const res = args.length == 1 ? args[0] : path.join(...args).replace(/[\\/]+/, path.posix.sep);
	return !res ? '""' : (res.indexOf(" ") >= 0 && res.indexOf('"') < 0 ? `"${res}"` : res);
}

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
		.pipe(gulpJasmineFixed())
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
		toPathArg( path.resolve('bin', 'vropkg')),
		'--in', 'tree',
		'--out', 'flat',
		'--srcPath', toPathArg('test', 'com.vmware.pscoe.toolchain-expand'),
		'--destPath', toPathArg( 'test', 'tmp' ),
		'--privateKeyPEM', toPathArg('test', 'private_key.pem'),
		'--certificatesPEM', toPathArg('test', 'cert.pem'),
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
		toPathArg( path.resolve('bin', 'vropkg')),
		'--in', 'tree',
		'--out', 'flat',
		'--srcPath', toPathArg('test', 'com.vmware.pscoe.vrbt-forms'),
		'--destPath', toPathArg('test', 'tmp' ),
		'--privateKeyPEM', toPathArg('test', 'private_key.pem'),
		'--certificatesPEM', toPathArg('test', 'cert.pem'),
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
