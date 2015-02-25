import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration

public class ScriptGeneration {

    public static final String CONFIGURATION_NAME = "scriptGeneration"

    private static final String TEMPLATE_GENERATED_DIR = "generatedScripts"
    private static final String GENERATED_CLASSPATH_DIR = "classpathDir"

    private Project project;

    ScriptGeneration(Project project) {
        this.project = project;
    }

    void applyTemplateFile(
            Configuration configuration,
            String templateFilePath,
            String destinationDir,
            String destinationFileName,
            String... params) {
        applyTemplateFileWithMainClass(configuration, "", templateFilePath, destinationDir, destinationFileName, params)
    }

    void applyTemplateFileWithMainClass(Configuration configuration,
                                        String mainClass,
                                        String templateFilePath,
                                        String destinationDir,
                                        String destinationFileName,
                                        String... params) {

        project.ext.nbGeneration = project.ext.nbGeneration + 1;
        Task currentApplyTemplateFileTask = project.task(TaskNames.TASK_APPLY_TEMPLATE_STARTER_NAME + "${project.ext.nbGeneration}", type: ScriptGenerationTask);
        currentApplyTemplateFileTask.configuration = configuration;
        currentApplyTemplateFileTask.mainClass = mainClass;
        currentApplyTemplateFileTask.templateFilePath = templateFilePath;
        currentApplyTemplateFileTask.destinationDir = destinationDir;
        currentApplyTemplateFileTask.destinationFileName = destinationFileName;
        if (params == null) {
            currentApplyTemplateFileTask.params = new String[0];
        } else {
            currentApplyTemplateFileTask.params = params;
        }
        currentApplyTemplateFileTask.outputGenerationDir = getBuildDirPath() + File.separatorChar + TEMPLATE_GENERATED_DIR
        currentApplyTemplateFileTask.getTaskDependencies().add(project.tasks[TaskNames.TASK_BUILD_CLASSPATH])
        project.tasks[TaskNames.TASK_APPLY_TEMPLATE].getTaskDependencies().add(currentApplyTemplateFileTask)
    }

    private String getBuildDirPath() {
        return project.buildDir.path;
    }

    public String getTemplateFilePath() {
        return getBuildDirPath() + File.separatorChar + TEMPLATE_GENERATED_DIR;
    }

    public String getGeneratedDirPath() {
        return getBuildDirPath() + File.separatorChar + GENERATED_CLASSPATH_DIR;
    }

}