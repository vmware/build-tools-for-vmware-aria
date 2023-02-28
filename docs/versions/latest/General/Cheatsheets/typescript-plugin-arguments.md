Name: o11n-typescript-package-maven-plugin
Description: (no description available)
Group Id: com.vmware.pscoe.maven.plugins
Artifact Id: o11n-typescript-package-maven-plugin
Version: 2.29.2-SNAPSHOT
Goal Prefix: vro

This plugin has 5 goals:

vro:clean
  Description: (no description available)
  Implementation: com.vmware.pscoe.maven.plugins.CleanNodeDepsMojo
  Language: java
  Bound to phase: clean

  Available parameters:

    project (Default: ${project})
      (no description available)

    skipInstallNodeDeps (Default: false)
      User property: skipInstallNodeDeps
      (no description available)

vro:compile
  Description: (no description available)
  Implementation: com.vmware.pscoe.maven.plugins.TypescriptCompileMojo
  Language: java
  Bound to phase: compile

  Available parameters:

    emitHeader (Default: false)
      User property: vrotsc.emitHeader
      (no description available)

    filesChanged
      User property: files
      (no description available)

    project (Default: ${project})
      (no description available)

vro:install-node-deps
  Description: (no description available)
  Implementation: com.vmware.pscoe.maven.plugins.InstallNodeDepsMojo
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

vro:package
  Description: (no description available)
  Implementation: com.vmware.pscoe.maven.plugins.TypescriptPackageMojo
  Language: java
  Bound to phase: package

  Available parameters:

    keystoreCert
      User property: vroCertificatePem
      (no description available)

    keystorePassword
      User property: vroKeyPass
      (no description available)

    packageSuffix
      User property: packageSuffix
      (no description available)

    privateKeyPem
      User property: vroPrivateKeyPem
      (no description available)

    project (Default: ${project})
      (no description available)

vro:run-vro-tests
  Description: (no description available)
  Implementation: com.vmware.pscoe.maven.plugins.TypescriptTestMojo
  Language: java
  Bound to phase: test

  Available parameters:

    project (Default: ${project})
      (no description available)

    skipTests (Default: false)
      User property: skipTests
      (no description available)

    test (Default: ${test.*})
      User property: test
      (no description available)


