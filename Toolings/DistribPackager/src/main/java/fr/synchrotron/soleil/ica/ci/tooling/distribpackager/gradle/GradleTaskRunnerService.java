package fr.synchrotron.soleil.ica.ci.tooling.distribpackager.gradle;

import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

/**
 * @author Gregory Boissinot
 */
public class GradleTaskRunnerService {

    private GradleConfig gradleConfig;

    private ProjectConfig projectConfig;

    public GradleTaskRunnerService(GradleConfig gradleConfig, ProjectConfig projectConfig) {
        this.gradleConfig = gradleConfig;
        this.projectConfig = projectConfig;
    }

    public void runTask(String taskName) {

        if (taskName == null || taskName.isEmpty()) {
            throw new NullPointerException("No task name to execute defined.");
        }

        GradleConnector gradleConnector = GradleConnector.newConnector();
        gradleConnector.useInstallation(gradleConfig.getInstallationDirFile());
        gradleConnector.forProjectDirectory(projectConfig.getProjectDirFile());
        ProjectConnection connection = gradleConnector.connect();
        try {
            connection.newBuild().forTasks(taskName).run();
        } finally {
            connection.close();
        }

    }
}
