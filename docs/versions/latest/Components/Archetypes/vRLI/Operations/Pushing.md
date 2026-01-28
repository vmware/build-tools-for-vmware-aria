# Pushing

Maven command for pushing Operations for Logs Content

## Overview

This will push all local content described in `./content.yaml` and available in `./src` folder to the remote Operations for Logs Server for the given profile.

## Table Of Contents

1. [Usage](#usage)

### Usage

To deploy the code developed in the local project or checked out from source control to a live server, you can use the `vrealize:push` command.

> **Note**: This ignores `content.yaml`

```bash
mvn package vrealize:push -P{profile}
```
