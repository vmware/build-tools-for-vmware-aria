const Jasmine = require("jasmine");
const ansiColors = require("ansi-colors");
const jasmineReporters = require("jasmine-reporters");
const joinPath = require("path").join;

class ConsoleReporter {
    indentationText = " ".repeat(2);
    totalSpecs = 0;
    failedSpecs = 0;
    indent = 0;

    suiteStarted(result, done) {
        this.log(result.description);
        this.indent++;
        done();
    }

    suiteDone(_, done) {
        this.indent--;
        done();
    }

    specStarted(_, done) {
        this.totalSpecs++;
        this.indent++;
        done();
    }

    specDone(result, done) {
        this.indent--;
        if (result.status === "passed") {
            this.log(ansiColors.green(result.description));
        }
        else {
            this.failedSpecs++;
            this.error(ansiColors.red(result.description));
            this.indent++;
            (result.failedExpectations || []).filter(e => !e.passed).forEach(e => {
                if (e.message) {
                    this.error(e.message);
                }
                if (e.stack) {
                    this.error(e.stack);
                }
            });
            this.indent--;
        }
        done();
    }

    jasmineDone(_, done) {
        this.log(`${this.totalSpecs} specs, ${this.failedSpecs} failures`);
        done();
    }

    log(s) {
        s = s.split("\\n").map(line => `${this.indentationText.repeat(this.indent)}${line}`).join("\\n")
        console.log(s);
    }

    error(s) {
        s = s.split("\\n").map(line => `${this.indentationText.repeat(this.indent)}${line}`).join("\\n")
        console.error(s);
    }
}

(async () => {
    const jasmine = new Jasmine();
    jasmine.clearReporters();
    jasmine.loadConfigFile("./jasmine.config.json");

    jasmine.clearReporters();
    jasmine.addReporter(new ConsoleReporter());
    jasmine.addReporter(new jasmineReporters.JUnitXmlReporter({
        savePath: joinPath(process.cwd(), "{TEST_RESULTS_PATH}"),
        consolidateAll: false,
        filePrefix: "TEST-"
    }));

    await jasmine.execute();
})();
