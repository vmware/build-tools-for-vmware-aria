package com.vmware.pscoe.iac.artifact.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtilities {

    public static void unzip(File zipFile, File outputLocation) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile.getAbsolutePath()))) {
            ZipEntry ze = null;

            while ((ze = zis.getNextEntry()) != null) {

                String fileName = ze.getName();
                File newFile = new File(outputLocation, fileName);

                new File(newFile.getParent()).mkdirs();

                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    byte[] buffer = new byte[1024];

                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }

                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
