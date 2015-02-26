import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs

public class ApplyTemplateTask extends DefaultTask {

    //Init script
    def String generationDir = project.buildDir.path + File.separatorChar + ScriptGeneration.TEMPLATE_GENERATED_DIR;

    @TaskAction
    public void execute(IncrementalTaskInputs inputs) {
        ////Erase at execution the previous value with all project information
        generationDir = project.buildDir.path + File.separatorChar + ScriptGeneration.TEMPLATE_GENERATED_DIR;
    }
}