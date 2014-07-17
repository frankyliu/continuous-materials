package fr.synchrotron.soleil.ica.ci.tooling.distribpackager.gradle;

import java.io.File;

/**
 * @author Gregory Boissinot
 */
public class GradleConfig {

    private File installationDir;

    public GradleConfig(File installationDir) {
        this.installationDir = installationDir;
    }

    public File getInstallationDir() {
        return installationDir;
    }
}
