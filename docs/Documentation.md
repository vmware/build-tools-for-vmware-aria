# Documentation

## Overview
The official documentation is stored under `./docs`. The documentation will be browsed in GitHub, so `README.md` files 
must be created as they will be show automatically by GitHub.

## Why not GitHub Pages or GitHub Wiki
We are not going to be using GitHub Wiki due to their poor navigation options. And we want to have multiple versions for different versions.
The GitHub Pages are automatically built, after enabling them in the repository settings, but they also lack a good navigation options 
and rely on third party theming.

## How do we write the documentation
The documentation is developer-centric, so it must be written in Markdown format. Files should be combined with other structurally/functionally 
similar ones for easier readability. Try to separate them as much as possible.

**Note:** Try to write clear and to the point documentation. If we can convey information with one sentence, no need to write two.

## Templates
Provided in the documentation are templates `./docs/Templates` that we should adhere to as much as possible, so we have consistent
documentation. Read the comments provided in the template and update accordingly. 

Introduction to a new template is something that needs to be discussed and triaged before being accepted. Same goes for 
changes to existing ones. If a change is needed, modify all current version templates to follow said change.

## How to find the delta between versions.
Utilize git history for specific documentation files. These can be tracked to specific pull requests if needed.
For other non-documentation changes, read the Release.md for the given version. It MUST have all the relevant information.

## Structure/How to read|write the documentation
The provided documentation follows a hierarchical structure of folders and markdown files. 

Rules:
1. Always update the `latest` version. All other versions are for archival purposes only.
2. Each component should have a `README.md` ( using the `Entry.md` template ) file in the base with external navigation
   to other parts of the component documentation. Make sure to keep this up to date where it makes sense
3. A `General` folder is highly encouraged
   1. It is highly encouraged that each component has a `Core Concepts.md` file explaining what is the idea behind said component, what is it trying to solve/fix/improve/etc.
   2. It is highly encouraged that each component has a `Getting Started.md` file explaining what is needed for others to start working with/using this component
4. The documentation is as much free-form as possible, however we should try to keep a similar structure throughout it.
5. Always use the provided templates as a starting point.
6. No internal links. Links should be one of: 
   1. Inside the file
   2. Pointing to another file/folder in the documentation
   3. Publicly available websites

## Automation
When releasing a new version, the `./docs/versions/latest` is taken and copied over to `./docs/versions/$NEW_VERSION`.
The current version is prepended to the `Version.md` and the `Release.md` files. 

All of the comments from `Release.md` will be automatically deleted. ( Future improvement: do this for all files ).


## FAQ

### Should we modify the old documentation?
No, only the new one. The old one should be removed soon.

### Should we still update the CHANGELOG.md?
We are transitioning away from the changelog, for now we should still update it.

### Who should contribute?
Idea for now is everyone who submits a pull request to update the documentation accordingly ( wherever possible ). New 
functionality must be fully documented no matter what, whereas when it comes to some bugfixes or specific components, this 
may not be possible.
