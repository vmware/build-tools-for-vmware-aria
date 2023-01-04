package com.vmware.pscoe.iac.artifact.model.abx;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.vmware.pscoe.iac.artifact.model.PackageDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class AbxPackageDescriptor extends PackageDescriptor {

    private AbxAction action;

    private static final String PACKAGE_JSON = "package.json";
    private static final String BUNDLE = "bundle.zip";

    public AbxAction getAction() {
        return this.action;
    }

    public void setAction(AbxAction action) {
        this.action = action;
    }

    public static AbxPackageDescriptor getInstance(File filesystemPath) {

        Logger logger = LoggerFactory.getLogger(AbxPackageDescriptor.class);

        AbxPackageDescriptor pd = new AbxPackageDescriptor();

        String packageJsonPath = filesystemPath.getPath() + File.separator + AbxPackageDescriptor.PACKAGE_JSON;

        try (JsonReader reader = new JsonReader(new FileReader(packageJsonPath))) {
            AbxAction action = new Gson().fromJson(reader, AbxAction.class);
            logger.info("Action definition: " + new Gson().toJson(action));
            action.setBundle(new File(filesystemPath, AbxPackageDescriptor.BUNDLE));
            pd.setAction(action);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error reading from file: %s", filesystemPath.getPath()), e);
        }

        return pd;
    }

}
