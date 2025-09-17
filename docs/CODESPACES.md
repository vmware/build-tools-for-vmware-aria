# GitHub Codespaces Setup

This repository is configured to work with GitHub Codespaces, providing a complete development environment in the cloud.

## Getting Started with Codespaces

### Option 1: Quick Start
1. Navigate to the [Build Tools for VMware Aria repository](https://github.com/vmware/build-tools-for-vmware-aria)
2. Click the green "Code" button
3. Select the "Codespaces" tab
4. Click "Create codespace on main"

### Option 2: Via GitHub Codespaces Dashboard
Visit [https://github.com/codespaces](https://github.com/codespaces) to see all your existing codespaces and create new ones.

## Development Environment

The Codespace comes pre-configured with:
- **Java**: OpenJDK 17 (required for the project)
- **Node.js**: Version 22.x (as specified in .node-version)
- **Maven**: Version 3.9.x (required for building)
- **Git**: Latest version
- **VS Code Extensions**: Java development pack, TypeScript support, and JSON tools

## Initial Setup

After your Codespace starts, the environment will automatically run a health check to verify all dependencies are properly installed. You can also run this manually:

```bash
curl -o- https://raw.githubusercontent.com/vmware/build-tools-for-vmware-aria/main/health.sh | bash
```

## Building the Project

Once your Codespace is ready, you can build the project using the standard Maven commands:

```bash
# Update ~/.m2/settings.xml with the packaging profile (see main README.md)
# Then build:
mvn clean install -D modules.plugins
mvn clean install -D modules.tools
mvn clean package -D modules.repository
```

## Managing Your Codespaces

- **View all codespaces**: [https://github.com/codespaces](https://github.com/codespaces)
- **Stop a codespace**: Use the Codespaces dashboard or VS Code command palette
- **Delete a codespace**: Via the Codespaces dashboard
- **Configure settings**: Access via your GitHub settings > Codespaces

## Useful Resources

- [GitHub Codespaces Documentation](https://docs.github.com/en/codespaces)
- [VS Code in Codespaces](https://docs.github.com/en/codespaces/developing-in-codespaces/using-visual-studio-code-in-a-codespace)
- [Managing Codespaces](https://docs.github.com/en/codespaces/managing-your-codespaces)