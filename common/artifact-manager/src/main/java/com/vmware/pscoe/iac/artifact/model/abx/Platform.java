package com.vmware.pscoe.iac.artifact.model.abx;

public class Platform {
    public String action;
    public String entrypoint;
    public String runtime;

    public String[] tags;               // optional
    public Integer memoryLimitMb;       // optional
    public Integer timeoutSec;          // optional
    public String provider;             // optional
}
