com.vmware.pscoe.maven.plugins:o11n-xml-package-maven-plugin:4.6.1-SNAPSHOT

Name: o11n-xml-package-maven-plugin
Description: Build Tools for VMware Aria provides development and release
  management tools for implementing automation solutions based on the VMware
  Aria Suite and VMware Cloud Director. The solution enables Virtual
  Infrastructure Administrators and Automation Developers to use standard
  DevOps practices for managing and deploying content.
Group Id: com.vmware.pscoe.maven.plugins
Artifact Id: o11n-xml-package-maven-plugin
Version: 4.6.1-SNAPSHOT
Goal Prefix: vro

This plugin has 5 goals:

vro:clean
  Description: (no description available)
  Implementation:
  com.vmware.pscoe.maven.plugins.XmlBasedActionsCleanNodeDepsMojo
  Language: java
  Bound to phase: clean

  Available parameters:

    project (Default: ${project})
      Project handle.

    skipInstallNodeDeps (Default: false)
      User property: skipInstallNodeDeps
      (no description available)

    vroIgnoreFile (Default: .vroignore)
      User property: vroIgnoreFile
      name of the vRO ignore file. Default is ".vroignore"

vro:execute-workflow
  Description: (no description available)
  Implementation: com.vmware.pscoe.maven.plugins.XmlExecuteWorkflowMojo
  Language: java

  Available parameters:

    connectionTimeout (Default: ${vrealize.connection.timeout})
      User property: connectionTimeout
      (no description available)

    id
      Required: true
      User property: id
      (no description available)

    ignoreSslCertificate (Default: false)
      Required: true
      User property: ignoreSslCertificate
      (no description available)

    ignoreSslHostname (Default: false)
      Required: true
      User property: ignoreSslHostname
      (no description available)

    in
      User property: in
      (no description available)

    keystoreCert
      User property: vroCertificatePem
      Certificate of keystore.

    keystorePassword
      User property: vroKeyPass
      password to keystore.

    outputFile
      User property: outputFile
      (no description available)

    outputParameter
      User property: outputParameter
      (no description available)

    privateKeyPem
      User property: vroPrivateKeyPem
      private key to keystore.

    project (Default: ${project})
      Project handle.

    socketTimeout (Default: ${vrealize.socket.timeout})
      User property: socketTimeout
      (no description available)

    ssh (Default: ${ssh.*})
      User property: ssh
      (no description available)

    timeout (Default: 300)
      User property: timeout
      Time in seconds to wait for the vRO workflow execution to complete before
      returning error. Default value: 300 seconds.

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

    vroIgnoreFile (Default: .vroignore)
      User property: vroIgnoreFile
      name of the vRO ignore file. Default is ".vroignore"

    vrops (Default: ${vrops.*})
      User property: vrops
      (no description available)

vro:install-node-deps
  Description: (no description available)
  Implementation:
  com.vmware.pscoe.maven.plugins.XmlBasedActionsInstallNodeDepsMojo
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

    vroIgnoreFile (Default: .vroignore)
      User property: vroIgnoreFile
      name of the vRO ignore file. Default is ".vroignore"

    vrops (Default: ${vrops.*})
      User property: vrops
      (no description available)

vro:package
  Description: (no description available)
  Implementation: com.vmware.pscoe.maven.plugins.XmlBasedProjectPackageMojo
  Language: java
  Bound to phase: package

  Available parameters:

    keystoreCert
      User property: vroCertificatePem
      Certificate of keystore.

    keystorePassword
      User property: vroKeyPass
      password to keystore.

    packageSuffix
      User property: packageSuffix
      (no description available)

    privateKeyPem
      User property: vroPrivateKeyPem
      private key to keystore.

    project (Default: ${project})
      Project handle.

    vroIgnoreFile (Default: .vroignore)
      User property: vroIgnoreFile
      name of the vRO ignore file. Default is ".vroignore"

vro:pull
  Description: (no description available)
  Implementation: com.vmware.pscoe.maven.plugins.XmlBasedProjectPullMojo
  Language: java

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

    packageName
      User property: packageName
      (no description available)

    privateKeyPem
      User property: vroPrivateKeyPem
      private key to keystore.

    project (Default: ${project})
      Project handle.

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

    vroIgnoreFile (Default: .vroignore)
      User property: vroIgnoreFile
      name of the vRO ignore file. Default is ".vroignore"

    vrops (Default: ${vrops.*})
      User property: vrops
      (no description available)

