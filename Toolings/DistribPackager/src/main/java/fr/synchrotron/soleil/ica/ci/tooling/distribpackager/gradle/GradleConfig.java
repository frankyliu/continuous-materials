package fr.synchrotron.soleil.ica.ci.tooling.distribpackager.gradle;

import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.exception.DistribPackagerException;

import java.io.File;

/**
 * @author Gregory Boissinot
 */
public class GradleConfig {

    private final File installationDirFile;

    public GradleConfig(String installationDir) {
        if (installationDir == null) {
            throw new DistribPackagerException("An installation directory is required.");
        }
        this.installationDirFile = new File(installationDir);
        if (!installationDirFile.exists()) {
            throw new DistribPackagerException("A valid installation directory is required.");
        }
    }

    public File getInstallationDirFile() {
        return installationDirFile;
    }
}
