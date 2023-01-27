# ${artifactId}
This project allows development and upload of ${type} actions.
It supports multiple actions of nodejs, powershell, and python types.

# Usage
Create a project from polyglot archetype.
The src folder will contain template actions for nodejs, powershell, and python.
Create new actions by copying templates and editing the code.
Build and push the actions

# Development
There are three templates:
* src/template-nodejs
* src/template-powershell
* src/template-python

Create new actions by copying a proper template. The name of the new folder will be the default action name.
Edit files of the new action:
* handler.ts or handler.ps1 or handler.py - implement the code for the new action here
* package.json (nodejs only) - Add dependencies using the example format `"ping": "^0.4.2"`. 
* polyglot.json - change action configuration options
  - Define list of input parameters and output type by editing vro/abx nodes
  - Keep the default values of the remaining options
    - You can set action name different from directory name by changing platform/action value
* requirements.txt (python only) - add dependencies using example format `requests==2.23.0`
* tsconfig.js (nodejs only) - keep default

Note: you do not need to delete template folders. Any folder starting with "template-" is ignored

## Development lifecycle (using Maven)
* Run `mvn clean package vrealize:push -Penvironment_profile`

# Development lifecycle (using NPM)
* Install development (and prod) dependencies: `npm install`
* Create bundle: `npm run build`
