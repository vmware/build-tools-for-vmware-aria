# Pushing

Maven command for pushing code to Orchestrator.

## Overview

This will push all local content from `./src` folder to the remote Orchestrator Server for the given profile.

## Table Of Contents

1. [Usage](#usage)

### Usage

To deploy the code developed in the local project or checked out from source control to a live server, you can use the `vrealize:push` command.

```bash
mvn package vrealize:push -P{profile}
```
