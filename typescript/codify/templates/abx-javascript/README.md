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

## Development prerequisites

- NodeJS 14+
- Docker

## Set up

```sh
# Install local development dependencies
npm install
```

## Local development

The root of the project contains a `run.js` action runner. It is intended to
provide an invocation environment allowing for static mocks and automated credentials
resoltuibn for proxy calls.

```sh
# Running ABX handlers using mocked inputs
node run.js create_resource.handler abx.input.yaml
node run.js remove_resource.handler abx.input.yaml
```

### Internal calls using `context.request()`

When performing internal calls to vRA using the `context.request()` function,
the action runner automatically prepends the vRA hostname and obtains and caches
an access token. The access token is persisted in the `.env` file and is loaded
every time the action runner is invoked. Also it gets automatically refreshed
prior to expiration so that you don't need to update or refresh it in your logic.
If you wish to re-authenticate, simply remove the `VRA_ACCESS_TOKEN` variable
from `.env`.

### Secrets resolution using `context.getSecret()`

The action runner provides local secrets resolution directly from the inputs, i.e.
if you define an input named `mySecret`, calling `context.getSecret(inputs.mySecret)`
will resolve to the value of `mySecret`.

### Building

The ABX actions might depend on environment-specific libraries and needs to be
packaged for the target ABX environment in which it will run. That's why it
is recommended to build it using a Docker container for this.

```sh
# Build Docker image for project building
docker build -t abx-build:latest --file docker/Dockerfile .

# Build the project for target environment
docker run --rm -it -v $(pwd):/usr/app -w /usr/app abx-build:latest npm run build
# (Using Fish Shell)
docker run --rm -it -v (pwd):/usr/app -w /usr/app abx-build:latest npm run build
```

Once built, the ABX actions will be located in `dist`. They are importable
in vRA from the **Extensibility -> Actions** menu in Cloud Assembly.

## Unit testing

This project comes with a testing framework and a simple unit tests already written
for the sample code. The unit tests can be run by invoking:

```sh
npm run test
```

The command will also produce an HTML coverage report placed in the `coverage` directory.
When developing, aim at keeping code coverage above 80%.
