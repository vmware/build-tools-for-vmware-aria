# Content Policies
There are 6 types of content policies:
  Approval, Content Sharing, Day 2 Actions, Deployment Limit, Lease and Resource Quota policies.
## Table Of Contents
1. [Structure](#structure)
2. [Operations](#operations)
   1. [Exporting](#exporting) - how are policies exported from a vRa
   2. [Importing](#importing) importing policies to vRA
3. [Known Issues](#known-issues)

### Structure
Below is an example structure of a blueprint export.

Example `content.yaml`
```yaml
policy:
  approval:
    - example policy
  content-sharing:
    - example policy
  day2-actions:
    - example policy
  deployment-limit:
    - example policy
  lease:
    - example policy
  resource-quota: 
    - example policy

# ...
```

Structure
```ascii
src/
├─ main/
│  ├─ resources/
│  │  ├─ policies/
│  │  │  ├─ approval/
│  │  │  │  ├─ examplePolicy.json
│  │  │  ├─ content-sharing/
│  │  │  │  ├─ examplePolicy.json
│  │  │  ├─ day2-actions/
│  │  │  │  ├─ examplePolicy.json
│  │  │  ├─ deployment-limit/
│  │  │  │  ├─ examplePolicy.json
│  │  │  ├─ lease/
│  │  │  │  ├─ examplePolicy.json
│  │  │  ├─ resource-quota/
│  │  │  │  ├─ examplePolicy.json
```
### Operations

Operations are invoked on policies based on filtering from content.yml file, according to the following rules:

- Empty array [] - nothing is imported/exported.
- List of items - the given items are imported/exported. If they are not present on the server an Exception is thrown.  
- Null (nothing given) - everything is being imported/exported.


#### Importing
When importing a policy, it is matched by its `id`. If there is a policy with the same id on the server,
an update will be performed. Otherwise, the policy will be created instead.
On import the organization will be changed to match the receiving organization.
Project Id will also be changed , but only if present, and if the organization has also been changed.


#### Exporting
When exporting a policy, a json file will be created on the filesystem. The filename will be the policyName[-index].json.
Index will be added only if there are multiple policies with the same name.

### Known Issues
 * Multitenancy Currently not supported.

