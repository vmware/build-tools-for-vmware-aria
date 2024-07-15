[//]: # (VERSION_PLACEHOLDER DO NOT DELETE)
[//]: # (Used when working on a new release. Placed together with the Version.md)
[//]: # (Nothing here is optional. If a step must not be performed, it must be said so)
[//]: # (Do not fill the version, it will be done automatically)
[//]: # (Quick Intro to what is the focus of this release)

## Breaking Changes

[//]: # (### *Breaking Change*)
[//]: # (Describe the breaking change AND explain how to resolve it)
[//]: # (You can utilize internal links /e.g. link to the upgrade procedure, link to the improvement|deprecation that introduced this/)

## Deprecations

[//]: # (### *Deprecation*)
[//]: # (Explain what is deprecated and suggest alternatives)

[//]: # (Features -> New Functionality)

## Features

[//]: # (### *Feature Name*)
[//]: # (Describe the feature)
[//]: # (Optional But higlhy recommended Specify *NONE* if missing)
[//]: # (#### Relevant Documentation:)

[//]: # (Improvements -> Bugfixes/hotfixes or general improvements)

## Improvements

[//]: # (### *Improvement Name* )
[//]: # (Talk ONLY regarding the improvement)
[//]: # (Optional But higlhy recommended)
[//]: # (#### Previous Behavior)
[//]: # (Explain how it used to behave, regarding to the change)
[//]: # (Optional But higlhy recommended)
[//]: # (#### New Behavior)
[//]: # (Explain how it behaves now, regarding to the change)
[//]: # (Optional But higlhy recommended Specify *NONE* if missing)
[//]: # (#### Relevant Documentation:)

### *ABX archetype build issue, cannot compile*

Fixed an issue where the ABX archetype could not compile due to an old version of the `xmlbuilder2` package.

#### Previous Behavior

We were getting a build error when trying to compile the ABX archetype:

```
info:    Error ts(1110) /root/vro/polyglot_test_project/node_modules/@types/node/crypto.d.ts (3569,17): Type expected.
info:    Error ts(1005) /root/vro/polyglot_test_project/node_modules/@types/node/events.d.ts (105,28): ',' expected.
...
info:    Error ts(1005) /root/vro/polyglot_test_project/node_modules/@types/node/util.d.ts (1763,26): ';' expected.
info:    Error ts(1128) /root/vro/polyglot_test_project/node_modules/@types/node/util.d.ts (1765,1): Declaration or statement expected.
info: Exit status: 1
info: Compilation complete
/root/vro/polyglot_test_project/node_modules/@vmware-pscoe/polyglotpkg/dist/strategies/nodejs.js:123
            throw new Error('Found compilation errors');
                  ^

Error: Found compilation errors
    at NodejsStrategy.compile (/root/vro/polyglot_test_project/node_modules/@vmware-pscoe/polyglotpkg/dist/strategies/nodejs.js:123:19)
    at NodejsStrategy.<anonymous> (/root/vro/polyglot_test_project/node_modules/@vmware-pscoe/polyglotpkg/dist/strategies/nodejs.js:52:18)
    at Generator.next (<anonymous>)
    at fulfilled (/root/vro/polyglot_test_project/node_modules/@vmware-pscoe/polyglotpkg/dist/strategies/nodejs.js:5:58)
[ERROR] Command execution failed.
org.apache.commons.exec.ExecuteException: Process exited with an error: 1 (Exit value: 1)
    at org.apache.commons.exec.DefaultExecutor.executeInternal (DefaultExecutor.java:404)
    at org.apache.commons.exec.DefaultExecutor.execute (DefaultExecutor.java:166)
...
    at java.util.concurrent.ThreadPoolExecutor.runWorker (ThreadPoolExecutor.java:1136)
    at java.util.concurrent.ThreadPoolExecutor$Worker.run (ThreadPoolExecutor.java:635)
    at java.lang.Thread.run (Thread.java:840)
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
```

#### New Behavior

The ABX archetype now compiles successfully.

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
