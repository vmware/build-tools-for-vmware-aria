com.vmware.pscoe.maven.plugins:ssh-maven-plugin:4.6.1-SNAPSHOT

Name: ssh-maven-plugin
Description: Build Tools for VMware Aria provides development and release
  management tools for implementing automation solutions based on the VMware
  Aria Suite and VMware Cloud Director. The solution enables Virtual
  Infrastructure Administrators and Automation Developers to use standard
  DevOps practices for managing and deploying content.
Group Id: com.vmware.pscoe.maven.plugins
Artifact Id: ssh-maven-plugin
Version: 4.6.1-SNAPSHOT
Goal Prefix: ssh

This plugin has 2 goals:

ssh:pull
  Description: (no description available)
  Implementation: com.vmware.pscoe.maven.plugins.SshPullMojo
  Language: java

  Available parameters:

    connectionTimeout (Default: ${vrealize.connection.timeout})
      User property: connectionTimeout
      (no description available)

    dryrun (Default: false)
      User property: dryrun
      Dry run or not. Default value is false.

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
      The project that is built with the tools.

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

ssh:push
  Description: (no description available)
  Implementation: com.vmware.pscoe.maven.plugins.SshPushMojo
  Language: java
  Bound to phase: pre-integration-test

  Available parameters:

    connectionTimeout (Default: ${vrealize.connection.timeout})
      User property: connectionTimeout
      (no description available)

    dryrun (Default: false)
      User property: dryrun
      (no description available)

    filesChanged
      User property: files
      (no description available)

    ignoreSslCertificate (Default: false)
      Required: true
      User property: ignoreSslCertificate
      (no description available)

    ignoreSslHostname (Default: false)
      Required: true
      User property: ignoreSslHostname
      (no description available)

    includeDependencies (Default: true)
      Required: true
      User property: includeDependencies
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

