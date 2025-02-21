com.vmware.pscoe.maven.plugins:vcd-ng-package-maven-plugin:4.0.1-SNAPSHOT

Name: vcd-ng-package-maven-plugin
Description: Build Tools for VMware Aria provides development and release
  management tools for implementing automation solutions based on the VMware
  Aria Suite and VMware Cloud Director. The solution enables Virtual
  Infrastructure Administrators and Automation Developers to use standard
  DevOps practices for managing and deploying content.
Group Id: com.vmware.pscoe.maven.plugins
Artifact Id: vcd-ng-package-maven-plugin
Version: 4.0.1-SNAPSHOT
Goal Prefix: vcd-ng

This plugin has 2 goals:

vcd-ng:install-node-deps
  Description: (no description available)
  Implementation: com.vmware.pscoe.maven.plugins.VcdNgInstallNodeDepsMojo
  Language: java
  Bound to phase: initialize

  Available parameters:

    connectionTimeout (Default: ${vrealize.connection.timeout})
      User property: connectionTimeout
      (no description available)

    ignoreSslCertificate (Default: false)
      Required: true
      User property: ignoreSslCertificate
      (no description available)

    ignoreSslHostname (Default: false)
      Required: true
      User property: ignoreSslHostname
      (no description available)

    keystoreCert
      User property: vroCertificatePem
      Certificate of keystore.

    keystorePassword
      User property: vroKeyPass
      password to keystore.

    privateKeyPem
      User property: vroPrivateKeyPem
      private key to keystore.

    project (Default: ${project})
      The external project that is built with VMware Aria Build Tools.

    skipInstallNodeDeps (Default: false)
      User property: skipInstallNodeDeps
      Boolean indicating whether the Node dependencies must be installed.

    socketTimeout (Default: ${vrealize.socket.timeout})
      User property: socketTimeout
      (no description available)

    ssh (Default: ${ssh.*})
      User property: ssh
      (no description available)

    vcd (Default: ${vcd.*})
      User property: vcd
      (no description available)

    vrang (Default: ${vrang.*})
      User property: vrang
      (no description available)

    vrli (Default: ${vrli.*})
      User property: vrli
      (no description available)

    vro (Default: ${vro.*})
      User property: vro
      (no description available)

    vrops (Default: ${vrops.*})
      User property: vrops
      (no description available)

vcd-ng:package
  Description: (no description available)
  Implementation: com.vmware.pscoe.maven.plugins.VcdNgPackageMojo
  Language: java
  Bound to phase: package

  Available parameters:

    project (Default: ${project})
      (no description available)

