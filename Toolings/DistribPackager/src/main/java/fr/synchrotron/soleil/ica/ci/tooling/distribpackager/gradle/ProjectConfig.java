package fr.synchrotron.soleil.ica.ci.tooling.distribpackager.gradle;

import java.io.File;


/**
 * @author Gregory Boissinot
 */
public class ProjectConfig {

    private File gradleBuildFile;

    private File projectDir;

    public ProjectConfig(File gradleBuildFile, File projectDir) {
        this.gradleBuildFile = gradleBuildFile;
        this.projectDir = projectDir;
    }

    public File getGradleBuildFile() {
        return gradleBuildFile;
    }

    public File getProjectDir() {
        return projectDir;
    }
}
