# Content Policies

There are 6 types of content policies:

- Approval
- Content Sharing
- Day 2 Actions
- Deployment Limit
- Lease
- Resource Quota

## Table Of Contents

1. [Structure](#structure)
2. [Operations](#operations)
   1. [Exporting](#exporting) - how are policies exported from a vRa
   2. [Importing](#importing) importing policies to vRA
3. [Known Issues](#known-issues)

### Structure

Below is an example structure of content policies export.

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

When importing policies, files are read form the filesystem, and the content.yml filter is by filename. All non-hidden files are read from the folder, and if the name of the file, without the extension matches the list in content.yml, the policy will be imported. The filename is only important for filtering. Actual policy fields are read from the file contents. If there is a policy with the same id on the server, an update will be performed. Otherwise, the policy will be created instead, using the same id, that is found in the file.

##### Content Sharing Polices Import

If project name is is defined as a *scope* proprty in the content sharing JSON file it will be used as a project scope during push , hence allowing more granular content sharing across different projects. If the *scope* property is not defined then the project id defined in the settings.xml configuration file will be used.

If a organization name is defined as *organization* property in the content sharing JSON file then it will be used as organization of the content sharing policy, otherwise the organization defined in the settings.xml file will be used. The following policy types can be used during import:

1. Content source policies.
2. Catalog item policies.
3. Combined policies (that contain catalog items and content sources).

The name of the content source(s) and catalog item(s) in the file are stored in the JSON file, hence if they are present on the target system the policy can be imported correctly during subsequent push.

#### Exporting

When exporting a policy, a json file will be created on the filesystem. The filename will be the policyName[-index].json.
Index will be added only if there are multiple policies with the same name.

##### Content Sharing Polices Export

The scope and organization of the content sharing policy will be exported as *scope* and *organization* properties in the output JSON file(s). The project name will be used as as *scope* parameter and organization name will be used as a *organization* parameter. The following policy types can be created on the target system and they will be exported as well:

1. Content source policies.
2. Catalog item policies.
3. Combined policies (that contain catalog items and content sources).

The catalog items and content source policies will be stored with their names in the output JSON file.

#### Content Sharing Polices Examples

Example content sharing policy JSON file with scope / organization parameters.

```JSON
{
    "id": "1",
    "name": "contentSharingPolicy",
    "typeId": "com.vmware.policy.catalog.entitlement",
    "projectId": "1",
    "scope": "projectId1",
    "organization": "organization1",
    "enforcementType": "HARD",
    "description": "TEST",
    "definition": {
        "entitledUsers": [
            {
                "items": [
                    {
                        "name": "Content Source 1",
                        "type": "CATALOG_SOURCE_IDENTIFIER"
                    },
                    {
                        "name": "Content Source 2",
                        "type": "CATALOG_SOURCE_IDENTIFIER"
                    },
                    {
                        "name": "Catalog Item 1",
                        "type": "CATALOG_ITEM_IDENTIFIER"
                    },
                ],
                "userType": "USER",
                "principals": [
                    {
                        "type": "PROJECT",
                        "referenceId": ""
                    }
                ]
            }
        ]
    }
}
```

Example content sharing policy JSON file without scope / organization parameters.

```JSON
{
    "id": "1",
    "name": "contentSharingPolicy",
    "typeId": "com.vmware.policy.catalog.entitlement",
    "projectId": "1",
    "orgId": "1",
    "enforcementType": "HARD",
    "description": "TEST",
    "definition": {
       "entitledUsers": [
              {
                  "items": [
                      {
                          "name": "Content Source 2",
                          "type": "CATALOG_SOURCE_IDENTIFIER"
                      },
                      {
                          "name": "Content Source 3",
                          "type": "CATALOG_SOURCE_IDENTIFIER"
                      },
                      {
                          "name": "Catalog Item 4",
                          "type": "CATALOG_ITEM_IDENTIFIER"
                      },
                  ],
                  "userType": "USER",
                  "principals": [
                      {
                          "type": "PROJECT",
                          "referenceId": ""
                      }
                  ]
              }
          ]
    }
}
```

### Known Issues

Re-Creating Deleted policy.
If you delete a policy, and then try to re-import it immediately, the import command will not fail, however, the policy will not be created.
After a certain delay, the policy can be re-created again  via vrealize:push.
"Open bug: [VRAE-61849]"

Here are two approaches to prevent this from happening:

- **Do not delete policies before re-importing them.** This approach will only work if you do not need to change some properties like projectId.

- **Remove policy ids from JSON files.** After successful import, do a export to get the new policyIds in the JSON files again. This approach will only work if you are exporting and importing from a single site. If you are exporting from SiteA to SiteB regularly, this may lead to policy duplicates.
