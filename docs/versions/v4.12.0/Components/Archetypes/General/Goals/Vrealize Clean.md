# Vrealize Clean

`vrealize:clean` is a goal used to clean up archetype packages on a given environment.

## Overview

`vrealize:clean` gives us a lot of flexibility in what is going to be deleted. You can find more about the different options in the sections below.

## Table Of Contents

1. [Archetype Support for vrealize:clean](#archetype-support-for-vrealizeclean)
2. [Usage](#usage)

### Archetype Support for vrealize:clean

| Archetype | Supported | Comment                                                            |
|-----------|-----------|--------------------------------------------------------------------|
| vra       | Yes       | -                                                                  |
| vro       | Yes       | -                                                                  |
| vcd       | Partial   | It does not support dryRunning                                     |
| abx       | No        | Not implemented                                                    |
| vrops     | No        | Not implemented                                                    |
| vra-ng    | Partial   | Does not support dryRunning and does not work for regional content |
| vrli      | No        | vRLI does not provide native package support                       |
| cs        | No        | Code Stream Services does not provide native support for packages  |

### Usage

```bash
mvn vrealize:clean -DincludeDependencies=true -DcleanUpOldVersions=true -DcleanUpLastVersion=false -Ddryrun=true -P${PROFILE}
```

- includeDependencies - a flag depicting if dependencies should also be deleted
- cleanUpOldVersions - a flag depicting if old versions of the package (and dependencies in case of includeDependencies) should be deleted
- cleanUpLastVersion - a flag depicting if the latest version should also be deleted before importing
- dryrun - Dryrun or not
