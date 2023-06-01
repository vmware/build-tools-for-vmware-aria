# [Project Name]

[Write a short description about the purpose of this project.]

# Development

This project comes with preconfigured rules for the following tools:

- [EditorConfig](https://editorconfig.org/)
- [ESLint](https://eslint.org/)
- [Prettier](https://prettier.io/)

When developing, they ensure consistency and static code analyis where applicable.

## VSCode Integration

The project also comes with a `.vscode` set of rules for [VSCode](https://code.visualstudio.com/) integration.
You can install the following extensions in your VSCode for handy integration:

- [EditorConfig for VS Code](https://marketplace.visualstudio.com/items?itemName=EditorConfig.EditorConfig)
- [ESLint](https://marketplace.visualstudio.com/items?itemName=dbaeumer.vscode-eslint)
- [Prettier - Code formatter](https://marketplace.visualstudio.com/items?itemName=esbenp.prettier-vscode)

# Codify integration

```bash
# Upload
codify upload --source src/actions          # Upload actions
codify upload --source src/workflows        # Upload workflows
codify upload --source src/configurations   # Upload configurations
codify upload --source src/cloudtemplates   # Upload cloud templates
codify upload --source src/subscriptions    # Upload subscriptions
codify upload --source src/abx              # Upload ABX actions

# Download
codify download --target src/actions --action "com.vmware.actions/Action"
codify download --target src/workflows --workflow "Path/To/Workflow"
codify download --target src/configurations --config "Path/To/Configuration"
codify download --target src/cloudtemplates --template "Cloud Template Name"
codify download --target src/subscriptions --subscription "Subscription Name"
codify download --target src/abx --abx "ABX Name"
```
