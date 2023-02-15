# v2.29.1

## Breaking Changes



## Deprecations



## Features



## Improvements

### Enabled Unit testing for npm lib based projects

#### Previous Behavior

npm test was not part of maven lifecycle for npm based projects

#### New Behavior

npm test is now triggered during maven package goal for npm based projects

#### Relevant Documentation

None

### Fix polyglotpkg/abx backward compatibility

#### Previous Behavior

If you upgrade to latest Build Tools for VMware Aria version the previously created ABX and polyglot actions will stop working

#### New Behavior

With small adjustments, the old ABX and polyglot actions should be upgradable

#### Relevant Documentation

None

### Fix polyglotpkg dependencies

#### Previous Behavior

If you build project with polyglot + typescript-project-all the build will fail with dependency resolution error

#### New Behavior

If you build project with polyglot + typescript-project-all the build will succeed

#### Relevant Documentation

None

### Installer v 2.27.0 Doesn't Ask for All Required Information about importing vRА
Installer script doesn't show a prompt to ask for additional information for importing vRA - project name and ID and Organization name and ID

#### Previous Behavior
There was condition which let only Code Stream to prompt for vRA questions

#### New Behavior
The prevous condition was modified to be able to ask questions according to presense of vRA project.
Unnecessary suppression of questions regarding embedded vRO had been removed

#### Relevant Documentation
**NONE**


## Upgrade procedure:

