# [Project Name]

[Write a short description about the purpose of this project.]

# Development

The ABX actions in this project are PowerShell-based and can be developed and tested locally.
The following procedure can serve as a guide for setting up a local development environment for ABX.

## VSCode Integration

The project also comes with a `.vscode` set of rules for [VSCode](https://code.visualstudio.com/) integration.
You can install the following extensions in your VSCode for handy integration:

-   [EditorConfig for VS Code](https://marketplace.visualstudio.com/items?itemName=EditorConfig.EditorConfig)
-   [PowerShell](https://marketplace.visualstudio.com/items?itemName=ms-vscode.PowerShell)

It also comes with preconfigured debug settings located in `.vscode/launch.json`.

## Development prerequisites

-   PowerShell Core 7+
-   Python 3.7+
-   Docker

## Local development

The root of the project contains a `run.ps1` action runner. It is intended to
provide an invocation environment allowing for static mocks and automated credentials
resoltuibn for proxy calls.

```sh
# Running ABX handlers using mocked inputs
pwsh run.ps1 create_resource.handler abx.input.json
pwsh run.ps1 remove_resource.handler abx.input.json
```

### Building

The ABX actions might depend on environment-specific libraries and needs to be
packaged for the target ABX environment in which it will run. That's why it
is recommended to build it using a Docker container for this.

```sh
# Build Docker image for project building
docker build -t abx-build:latest --file docker/Dockerfile .

# Build the project for target environment
docker run --rm -it -v $(pwd):/usr/app -w /usr/app abx-build:latest python setup.py bundle
# (Using Fish Shell)
docker run --rm -it -v (pwd):/usr/app -w /usr/app abx-build:latest python setup.py bundle
```

Once built, the ABX actions will be located in `dist`. They are importable
in vRA from the **Extensibility -> Actions** menu in Cloud Assembly.
