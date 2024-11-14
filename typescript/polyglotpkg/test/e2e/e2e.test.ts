import path from 'path';
import { run } from "../../src/lib/utils";
import { existsSync } from 'fs';

describe('E2E Tests', () => {

    [
        'abxpython',
        'abx_all',
        'nodejs',
        'python',
        'powershell',
        "node:12",
        "node:14",
        "powershell:7.4",
        "powercli:13-powershell-7.4",
        "powercli:12-powershell-7.4",
        "powercli:12-powershell-7.1",
        "powercli:11-powershell-6.2"
    ]
    .map(runtime => runtime.replace(/[:\-.]/g, "_"))
    .filter(runtime => existsSync(path.join('test', 'e2e', runtime)))
    .forEach(runtime => {

        describe(`Packaging ${runtime}`, () => {
            const processCwd = process.cwd();

            beforeEach(`Cleaning up ${runtime}`, async () => {
                process.chdir(processCwd);
                await run('npm', ['run', 'clean'], path.join('test', 'e2e', runtime));
            })

            it(`Packaging ${runtime}`, async () => {
                const projectDir = path.resolve('test', 'e2e', runtime);
                process.chdir(projectDir);
                const environment = ["abxpython", "abx_all", "nodejs", "python", "powershell"].indexOf(runtime) != -1 ? "abx": "vro";
                await run('../../../bin/polyglotpkg', ['-e', environment]);
            })
        })

    })

})
