package com.vmware.pscoe.iac.artifact.store.cs;

import java.io.File;

public interface ICsStore {
    void importContent(File sourceDirectory);

    void exportContent();

}
