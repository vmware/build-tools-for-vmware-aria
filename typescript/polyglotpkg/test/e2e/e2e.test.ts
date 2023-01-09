import fs from 'fs-extra';
import path from 'path';
import { run } from "../../src/lib/utils";

describe('E2E Tests', () => {

    ['nodejs', 'python', 'powershell']
    .filter(runtime => fs.existsSync(path.join('test', 'e2e', runtime)))
    .forEach(runtime => {
        describe(`Packaging ${runtime}`, () => {

            beforeEach(`Cleaning up ${runtime}`, async () => {
                await run('npm', ['run', 'clean'], path.join('test', 'e2e', runtime))
            })

            it(`Packaging ${runtime}`, async () => {
                const projectDir = path.join('test', 'e2e', runtime);
                const outDir = path.join('test', 'e2e', runtime, 'out');
                const bundleName = path.join('test', 'e2e', runtime, 'dist', 'bundle.zip');
                await run('./bin/polyglotpkg', ['-p', projectDir, '-o', outDir, '-b', bundleName]);
            })

        })
    })

});
