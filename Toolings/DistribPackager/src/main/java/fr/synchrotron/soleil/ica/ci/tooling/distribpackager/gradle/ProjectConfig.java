package fr.synchrotron.soleil.ica.ci.tooling.distribpackager.gradle;

import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.exception.DistribPackagerException;

import java.io.File;


/**
 * @author Gregory Boissinot
 */
public class ProjectConfig {

    private final File gradleBuildFile;

    private final File projectDirFile;

    public ProjectConfig(String gradleBuildFileName, String projectDir) {

        if (gradleBuildFileName == null) {
            throw new DistribPackagerException("A gradle build file name is required.");
        }
        this.gradleBuildFile = new File(gradleBuildFileName);
        if (!gradleBuildFile.exists()) {
            throw new DistribPackagerException(String.format("The given Gradle build file '%s' doesn't exist.", gradleBuildFileName));
        }

        if (projectDir == null) {
            throw new DistribPackagerException("A project directory is required.");
        }
        this.projectDirFile = new File(projectDir);
        if (!projectDirFile.exists()) {
            throw new DistribPackagerException(String.format("The project directory '%s' doesn't exist.", projectDir));
        }
    }

    public File getGradleBuildFile() {
        return gradleBuildFile;
    }

    public File getProjectDirFile() {
        return projectDirFile;
    }
}
