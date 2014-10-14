import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration

public class ScriptGeneration {

    public static final CONFIGURATION_NAME = "scriptGeneration"
    public static final String GENERATED_CLASSPATH_DIR = Project.DEFAULT_BUILD_DIR_NAME + "/classpathDir"

    private Project project;

    private static final String TEMPLATE_GENERATED_DIR = Project.DEFAULT_BUILD_DIR_NAME + "/generatedScripts"

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

    void applyTemplateFileWithMainClass(
            Configuration configuration,
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
        currentApplyTemplateFileTask.outputGenerationDir = TEMPLATE_GENERATED_DIR
        currentApplyTemplateFileTask.getTaskDependencies().add(project.tasks[TaskNames.TASK_BUILD_CLASSPATH])
        project.tasks[TaskNames.TASK_APPLY_TEMPLATE].getTaskDependencies().add(currentApplyTemplateFileTask)
    }


}