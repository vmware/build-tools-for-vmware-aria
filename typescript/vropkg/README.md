# Project Structures

## XML Tree (Expanded Package Folder Structure)

```text
├── pom.xml
└── src
    └── main
        └── resources
            ├── ScriptModule
            ├── Workflow
            ├── PolicyTemplate
            ├── ResourceElement
            └── ConfigurationElement
```

## Flat Package (Exported Archived Package Folder Structure)

```text
├── dunes-meta-inf
├── certificates
│   └── O=VMware,OU=WWPS,CN=WWCE.cer
├── elements
│   └── 00000000-0000-0000-0000-000000000000 (element ID)
│       ├── categories
│       ├── content-signature
│       ├── data
│       │   └── VSO-RESOURCE-INF (if ResourceElement the data is ZIP)
│       │       ├── attribute_allowedOperations
│       │       ├── attribute_description
│       │       ├── attribute_id
│       │       ├── attribute_mimetype
│       │       ├── attribute_name
│       │       └── attribute_version
│       ├── info
│       └── tags
|       └── input_form_
|       └── input_form_itemN
└── signatures (signatures of the parent structure)
    ├── certificates
    │   └── O=VMware,OU=WWPS,CN=WWCE.cer
    ├── dunes-meta-inf
    └── elements
        └── 00000000-0000-0000-0000-000000000000
            ├── categories
            ├── content-signature
            ├── data
            ├── info
            └── tags
```

## JS Folder Structure

```text
├── pom.xml
└── src
    └── main
        └── resources
            └── com
                └── company
                    └── package
                        └── actions
                            ├── ActionOne.js
                            ├── ActionTwo.js
                            └── ActionTree.js
```

## Prerequisites

## Certificates Folder Structure

```text
├── certificates.PEM
├── privateKey.PEM
└── privateKeyPassphrase.TXT
```

## Operations
It can convert from any to any of the following:
- Flat Package
- XML Tree
- JS Folder

## Additional Features

When converting a flat package to a XML tree structure that has spefic vRO custom forms (for example user interaction components forms) they will be be unwrapped in the same directory with the .form.json suffix.
When converting a XML tree structure to a flat package when there are .form.json suffixed files they will be part of the flat package.

Example XML Tree structure of a project that contains 2 custom interaction components and their forms:

```text
├── pom.xml
└── src
    └── main
        └── resources
            ├── Workflow
                └── Workflow1.xml
                └── Workflow1.form.json
                └── Workflow1_input_form_item_2.form.json
                └── Workflow1_input_form_item_3.form.json
```
The Workflow1_input_form_item_2.form.json and Workflow1_input_form_item_3.form.json contain JSON representation of the forms for custom interaction components item2 and item3 respectively.

The naming convention for the form files is the following:
```text
<Workflow_Name>_input_form_<Form_Id>.form.json 
```

An example of a flat structure of a project tree that has 2 custom interaction components and their forms:

```text
├── dunes-meta-inf
├── certificates
│   └── O=VMware,OU=WWPS,CN=WWCE.cer
├── elements
│   └── 00000000-0000-0000-0000-000000000000 (element ID)
│       ├── categories
│       ├── content-signature
│       ├── data
│       │   └── VSO-RESOURCE-INF (if ResourceElement the data is ZIP)
│       │       ├── attribute_allowedOperations
│       │       ├── attribute_description
│       │       ├── attribute_id
│       │       ├── attribute_mimetype
│       │       ├── attribute_name
│       │       └── attribute_version
│       ├── info
│       └── tags
|       └── input_form_
|       └── input_form_item2
|       └── input_form_item3
```

The input_form_ contains a JSON representaton of vRO form, the input_form_item2 and input_form_item3 contain the JSON representation for custom interaction components forms for item2 and item3 respectively in the flat package.

