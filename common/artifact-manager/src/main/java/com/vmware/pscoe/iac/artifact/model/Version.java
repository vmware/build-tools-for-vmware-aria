package com.vmware.pscoe.iac.artifact.model;

import org.apache.commons.lang3.StringUtils;

public class Version implements Comparable<Version> {
    private static final String SNAPSHOT_DELIMITER = "-";
    private static final String VERSION_DELIMITER = "\\.";
    private static final int MAJOR_VERSION_OFFSET = 0;
    private static final int MINOR_VERSION_OFFSET = 1;

    public final String version;

    public Version(String version) {
        this.version = version;
    }

    public String getString(){
        return this.version;
    }

    public String getVersion() {
        return this.version;
    }

    public Integer getMajorVersion() {
        return this.version.equalsIgnoreCase("cloud")
                ? new Integer(Integer.MAX_VALUE)
                : this.parseVersion(MAJOR_VERSION_OFFSET);
    }

    public Integer getMinorVersion() {
        return this.version.equalsIgnoreCase("cloud")
                ? new Integer(Integer.MAX_VALUE)
                : this.parseVersion(MINOR_VERSION_OFFSET);
    }

    private Integer parseVersion(int offset) {
        String[] verData = StringUtils.isEmpty(this.version)? new String[] {} : this.version.split(VERSION_DELIMITER);
        String versionString = verData.length > offset - 1 ? verData[offset] : "";
        if (StringUtils.isEmpty(versionString)) {
            return null;
        }
        Integer retVal = null;
        try {
            retVal = Integer.valueOf(versionString);
        } catch (Exception e) {
            return null;
        }

        return retVal;
    }

    @Override
    public int compareTo(Version version) {
        if (version == null) {
            throw new NullPointerException("Object to be compared is Null.");
        }

        String a = this.getString();
        String b = version.getString();

        // cloud > anything
        if (!"cloud".equalsIgnoreCase(a) && "cloud".equalsIgnoreCase(b)) {
            return -1;
        } else if ("cloud".equalsIgnoreCase(a) && "cloud".equalsIgnoreCase(b)) {
            return 0;
        } else if ("cloud".equalsIgnoreCase(a) && !"cloud".equalsIgnoreCase(b)) {
            return 1;
        }

        // 1.0.0-SNAPSHOT
        String[] aVersionNottation = a.split(SNAPSHOT_DELIMITER);
        String[] bVersionNottation = b.split(SNAPSHOT_DELIMITER);

        // 1.0.0
        String[] aVersion = aVersionNottation[0].split(VERSION_DELIMITER);
        String[] bVersion = bVersionNottation[0].split(VERSION_DELIMITER);

        int compareTo = 0;
        for (int i= 0 ; i < aVersion.length && i < bVersion.length ; i++) {
            // compare versions number by number, going from major to minor
            compareTo = Integer.parseInt(aVersion[i], 10) - Integer.parseInt(bVersion[i], 10);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        // version < version.minor
        compareTo = aVersion.length - bVersion.length;
        if (compareTo != 0) {
            return compareTo;
         }

        // version > version-SNAPSHOT
        return bVersionNottation.length - aVersionNottation.length;
    }

}
