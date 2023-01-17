import * as fs from "fs-extra";
import * as gulp from "gulp";
import * as tsc from "gulp-typescript";

gulp.task("clean", done => {
	fs.remove("dist");
	done();
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

gulp.task("build-prod", gulp.series(["package-prod"]));

gulp.task("default", gulp.series(["clean", "build-prod"]));

function compile(settings: tsc.Settings): NodeJS.ReadWriteStream {
	let project = tsc.createProject("tsconfig.json", settings);
	let stream = project.src();

	return stream.pipe(project()).pipe(gulp.dest("dist"));
}
