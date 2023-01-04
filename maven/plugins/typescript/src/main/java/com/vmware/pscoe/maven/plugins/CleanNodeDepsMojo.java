package com.vmware.pscoe.maven.plugins;

import org.apache.maven.plugins.annotations.*;


@Mojo(name = "clean", defaultPhase = LifecyclePhase.CLEAN)
public class CleanNodeDepsMojo extends AbstractCleanNodeDepsMojo {
}
