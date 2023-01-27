Name: vcd-ng-package-maven-plugin
Description: (no description available)
Group Id: com.vmware.pscoe.maven.plugins
Artifact Id: vcd-ng-package-maven-plugin
Version: 2.28.1-SNAPSHOT
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
      (no description available)

    keystorePassword
      User property: vroKeyPass
      (no description available)

    privateKeyPem
      User property: vroPrivateKeyPem
      (no description available)

    project (Default: ${project})
      (no description available)

    skipInstallNodeDeps (Default: false)
      User property: skipInstallNodeDeps
      (no description available)

    socketTimeout (Default: ${vrealize.socket.timeout})
      User property: socketTimeout
      (no description available)

    ssh (Default: ${ssh.*})
      User property: ssh
      (no description available)

    vcd (Default: ${vcd.*})
      User property: vcd
      (no description available)

    vra (Default: ${vra.*})
      User property: vra
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


