package com.vmware.pscoe.maven.plugins;

import org.apache.maven.plugins.annotations.*;

@Mojo(name = "install-node-deps", defaultPhase = LifecyclePhase.INITIALIZE, requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM)
public class JsBasedActionsInstallNodeDepsMojo extends AbstractInstallNodeDepsMojo {
}
