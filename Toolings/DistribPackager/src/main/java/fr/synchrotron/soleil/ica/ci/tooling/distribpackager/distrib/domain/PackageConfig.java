package fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.domain;

import java.io.File;

/**
 * @author Gregory Boissinot
 */
public class PackageConfig {

    private final File packageDescriptorFile;

    private final File outputDirFile;

    private final File gradleInstallationDirFile;

    public PackageConfig(String packageDescFilePath,
                          String outputDirPath,
                          String gradleInstallationDirPath) {

        this.packageDescriptorFile = new File(packageDescFilePath);
        this.outputDirFile = new File(outputDirPath);
        this.gradleInstallationDirFile = new File(gradleInstallationDirPath);
    }

    public File getPackageDescriptorFile() {
        return packageDescriptorFile;
    }

    public File getOutputDirFile() {
        return outputDirFile;
    }

    public File getGradleInstallationDirFile() {
        return gradleInstallationDirFile;
    }
}
