package fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.service.maven.artifact.c;

import fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.domain.maven.MavenCNameElement;

/**
 * @author Gregory Boissinot
 */
public class ArtifactNameExtractor {

    public static final String ARTIFACT_NAME_SEPARATOR = "-";

    public MavenCNameElement extractMetadataFromName(String name) {

        if (name == null) {
            throw new NullPointerException("An artifact name is required.");
        }

        MavenCNameElement mavenCNameElement = new MavenCNameElement();
        final String[] partNames = name.split(ARTIFACT_NAME_SEPARATOR);

        if ((partNames.length == 5)) {
            mavenCNameElement.setName(partNames[0]);
            mavenCNameElement.setArchi(partNames[1]);
            mavenCNameElement.setPlatform(partNames[2]);
            mavenCNameElement.setCompiler(partNames[3]);
            mavenCNameElement.setMod(partNames[4]);
            return mavenCNameElement;
        }

        if (partNames.length >= 6) {
            mavenCNameElement.setName(partNames[0]);
            mavenCNameElement.setArchi(partNames[1]);
            mavenCNameElement.setPlatform(partNames[2]);
            mavenCNameElement.setCompiler(partNames[3]);
            mavenCNameElement.setTypeDep(partNames[4]);
            mavenCNameElement.setMod(partNames[5]);
            if (partNames.length >= 8) {
                String version = partNames[6];
                String classifier = partNames[7];
                mavenCNameElement.setVersion(version);
                mavenCNameElement.setClassifier(classifier);
            }
            return mavenCNameElement;
        }

        return null;
    }
}


/*

nxextractor-i386-Linux-gcc-release"

CrashReporting-i386-Linux-gcc-static-release-1.0.3-i386-Linux-g%2B%2B-static.nar
CrashReporting-i386-Linux-gcc-static-release-1.0.3-noarch.nar
CrashReporting-i386-Linux-gcc-static-release-1.0.3.pom


YAT4Tango-i386-Linux-gcc-static-release-1.6.7-tango8-i386-Linux-g%2B%2B-static.nar
YAT4Tango-i386-Linux-gcc-static-release-1.6.7-tango8-noarch.nar
YAT4Tango-i386-Linux-gcc-static-release-1.6.7-tango8.pom

YAT4Tango-i386-Linux-gcc-shared-release-1.6.9b-tango8-i386-Linux-g%2B%2B-shared.nar
YAT4Tango-i386-Linux-gcc-shared-release-1.6.9b-tango8-noarch.nar
YAT4Tango-i386-Linux-gcc-shared-release-1.6.9b-tango8.pom

HDF5-x86-Windows-msvc-shared-release-1.8.3-1-x86-Windows-msvc-shared.nar
HDF5-x86-Windows-msvc-shared-release-1.8.3-1.pom


PlxApi-x86-Windows-msvc-shared-release-6.0.0-noarch.nar
PlxApi-x86-Windows-msvc-shared-release-6.0.0-x86-Windows-msvc-shared.nar
PlxApi-x86-Windows-msvc-shared-release-6.0.0.pom

python2.7-i386-Linux-gcc-shared-release-2.7.5-i386-Linux-g%2B%2B-shared.nar
python2.7-i386-Linux-gcc-shared-release-2.7.5-noarch.nar
python2.7-i386-Linux-gcc-shared-release-2.7.5.pom

ACE-x86-Windows-msvc-shared-debug-1.0.0-noarch.nar
ACE-x86-Windows-msvc-shared-debug-1.0.0-x86-Windows-msvc-shared.nar
ACE-x86-Windows-msvc-shared-debug-1.0.0.pom
 */

