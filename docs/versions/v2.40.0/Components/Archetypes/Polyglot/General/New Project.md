# New Project

## Table Of Contents

- [Generate A New Project](#generate-a-new-project)

### Generate A New Project

- Replace `archetypeVersion` with the latest toolchain version
- Specify `type` to be either `abx` (vRA ABX Actions) or `vro` (vRO Polyglot Actions).

```bash
#vRO:  
mvn archetype:generate \ 
  -DinteractiveMode=false \
  -DarchetypeGroupId=com.vmware.pscoe.polyglot.archetypes \
  -DarchetypeArtifactId=package-polyglot-archetype \
  -DarchetypeVersion={LATEST_VERSION} \ 
  -DgroupId=com.vmware.pscoe \
  -DartifactId=testvronew \
  -Dtype=vro  

#ABX:  
mvn archetype:generate \
  -DinteractiveMode=false \
  -DarchetypeGroupId=com.vmware.pscoe.polyglot.archetypes \
  -DarchetypeArtifactId=package-polyglot-archetype \
  -DarchetypeVersion={LATEST_VERSION} \
  -DgroupId=com.vmware.pscoe \
  -DartifactId=testabxnew \
  -Dtype=abx
```
