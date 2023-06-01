# [Project Name]

[Write a short description about the purpose of this project.]

# Development

The ABX actions in this project are Python-based and can be developed and tested locally.
The following procedure can serve as a guide for setting up a local development environment for ABX.

## VSCode Integration

The project also comes with a `.vscode` set of rules for [VSCode](https://code.visualstudio.com/) integration.
You can install the following extensions in your VSCode for handy integration:

- [EditorConfig for VS Code](https://marketplace.visualstudio.com/items?itemName=EditorConfig.EditorConfig)
- [Python](https://marketplace.visualstudio.com/items?itemName=ms-python.python)
- [Pylance](https://marketplace.visualstudio.com/items?itemName=ms-python.vscode-pylance)

It also comes with preconfigured debug settings located in `.vscode/launch.json`.

## Development prerequisites

- Python 3.10
- Docker

## Set up

```sh
# Create and load virtualenv
virtualenv .virtualenv -p python3.10
source .virtualenv/bin/activate
# (Using Fish Shell)
source .virtualenv/bin/activate.fish

# Install local development dependencies
pip install -r requirements/dev.txt
```

## Local development

The root of the project contains a `run.py` action runner. It is intended to
provide an invocation environment allowing for static mocks and automated credentials
resoltuibn for proxy calls. You can get help by running `python run.py --help`.

```sh
# Running ABX handlers using mocked inputs
python run.py create_resource.handler abx.input.yaml
python run.py remove_resource.handler abx.input.yaml
```

### Internal calls using `context.request()`

When performing internal calls to vRA using the `context.request()` function,
the action runner automatically prepends the vRA hostname and obtains and caches
an access token. The access token is persisted in the `.env` file and is loaded
every time the action runner is invoked. Also it gets automatically refreshed
prior to expiration so that you don't need to update or refresh it in your logic.
If you wish to re-authenticate, simply remove the `VRA_ACCESS_TOKEN` variable
from `.env`.

### Secrets resolution using `context.get_secret()`

The action runner provides local secrets resolution directly from the inputs, i.e.
if you define an input named `mySecret`, calling `context.get_secret(inputs.mySecret)`
will resolve to the value of `mySecret`.

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

## Unit testing

This project comes with a testing framework and a simple unit tests already written
for the sample code. The unit tests can be run by invoking:

```sh
coverage run -m pytest && coverage report -m
```

The output will also display the code coverage for the collected files.
When developing, aim at keeping code coverage above 80%.

If you want to generate HTML coverage report, just run:

```sh
coverage html
```

The report will be placed in the `coverage` directory.
