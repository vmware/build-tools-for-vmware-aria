

const path = require('path');
const fs = require('fs-extra');
const gulp = require('gulp');
const yaml = require('js-yaml');
const Zip = require('adm-zip');
const uuid = require('uuid');
const which = require('which');
const { spawn } = require('child_process');
const argv = require('minimist')(process.argv.slice(2));

const rootPath = __dirname;
const buildPath = path.resolve(rootPath, 'build');
const distPath = path.resolve(rootPath, 'dist');
const coveragePath = path.resolve(rootPath, 'coverage');
const srcPath = path.resolve(rootPath, 'src');
const libPath = path.resolve(srcPath, 'lib');

const ID_NAMESPACE = '5cfde456-6086-4874-bea4-445967d9f345';

gulp.task('clean', async () => {
  fs.removeSync(buildPath);
  fs.removeSync(distPath);
  fs.removeSync(coveragePath);
});

gulp.task('cache-deps', async () => {
  fs.ensureDirSync(buildPath);
  fs.copySync(libPath, path.join(buildPath, path.basename(libPath)));
  fs.copyFileSync('package.json', path.join(buildPath, 'package.json'));
  if (!argv.nodeps) {
    await run('npm', ['install', '--production'], buildPath);
  }
});

gulp.task('build-actions', async () => {
  fs.ensureDirSync(buildPath);
  fs.ensureDirSync(distPath);

  const actions = fs
    .readdirSync(srcPath)
    .filter((f) => f.endsWith('.yaml'))
    .map((f) => path.join(srcPath, f));
  actions
    .map((action) => {
      const actionDefinition = {
        exportVersion: '1',
        exportId: '',
        name: path.basename(action, '.yaml'),
        runtime: 'nodejs',
        entrypoint: 'handler.handler',
        timeoutSeconds: 600,
        deploymentTimeoutSeconds: 900,
        actionType: 'SCRIPT',
        configuration: null,
        memoryInMB: 300,
        ...yaml.load(fs.readFileSync(action, 'utf-8')),
      };

      actionDefinition.exportId = uuid.v5(actionDefinition.name, ID_NAMESPACE);

      // copy main action file
      const actionEntryFile = `${actionDefinition.entrypoint.split('.')[0]}.js`;
      fs.copyFileSync(path.join(srcPath, actionEntryFile), path.join(buildPath, actionEntryFile));

      return actionDefinition;
    })
    .filter((actionDefinition) => {
      return argv.action ? actionDefinition.name === argv.action : true;
    })
    .forEach((actionDefinition) => {
      // copy meta file
      const metaFile = path.join(buildPath, `${actionDefinition.name}.abx`);
      fs.writeFileSync(metaFile, yaml.dump(actionDefinition));

      const bundleZip = new Zip();
      const bundleFile = path.join(buildPath, `${actionDefinition.name}.zip`);
      bundleZip.addLocalFolder(buildPath);
      bundleZip.writeZip(bundleFile);

      // compile final action
      const abxZip = new Zip();
      const abxFile = path.join(distPath, `${actionDefinition.name}.abx.zip`);
      abxZip.addLocalFile(metaFile);
      abxZip.addLocalFile(bundleFile);
      abxZip.writeZip(abxFile);

      fs.removeSync(bundleFile);
      fs.removeSync(metaFile);
    });
});

gulp.task('build', gulp.series('clean', 'cache-deps', 'build-actions'));

function run(cmd, args, cwd = process.cwd()) {
  return new Promise((resolve, reject) => {
    console.log(`Running: ${cmd} ${args.join(' ')}`);
    console.log(`PWD: ${cwd}`);
    which(cmd, (err, commandPath) => {
      if (err) {
        return reject(new Error(`Cannot find "${cmd}"`));
      }
      let log = '';
      const proc = spawn(quoteString(commandPath || cmd), args, {
        cwd,
        shell: true,
        // stdio: 'inherit'
      });
      proc.stdout.setEncoding('utf8');
      proc.stdout.on('data', (data) => (log += data.toString()));
      proc.stderr.setEncoding('utf8');
      proc.stderr.on('data', (data) => (log += data.toString()));
      proc.on('close', (exitCode) => {
        if (exitCode !== 0) {
          const err = new Error(`Exit code for ${cmd}: ${exitCode}`);
          err.exitCode = exitCode;
          err.log = log;
          return reject(err);
        }
        resolve({ code: exitCode, log });
      });
    });
  });
}

function quoteString(str) {
  return /\s+/.test(str) ? `"${str}"` : str;
}
