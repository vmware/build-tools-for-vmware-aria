Name: ssh-maven-plugin
Description: (no description available)
Group Id: com.vmware.pscoe.maven.plugins
Artifact Id: ssh-maven-plugin
Version: 2.26.5-SNAPSHOT
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
      (no description available)

    keystorePassword
      User property: vroKeyPass
      (no description available)

    privateKeyPem
      User property: vroPrivateKeyPem
      (no description available)

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


