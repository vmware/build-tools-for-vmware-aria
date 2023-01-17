package com.vmware.pscoe.iac.artifact.store.vrang;

import java.io.File;

public interface IVraNgStore {
    void importContent(File sourceDirectory);

    void exportContent();

}
