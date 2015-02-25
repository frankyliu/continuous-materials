import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.runtime.RuntimeConstants
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs

public class ScriptGenerationTask extends DefaultTask {

    class Dependency {
        String GroupId;
        String ArtifactId;
        String Version
        String Classifier;
        String Extension;
    }

    class Project extends Dependency {
    }

    @Input
    def Configuration configuration
    @Input
    def String mainClass
    @Input
    def String templateFilePath
    @Input
    def String destinationDir
    @Input
    def String destinationFileName
    @Input
    def String[] params
    @Input
    def String outputGenerationDir

    @TaskAction
    void execute(IncrementalTaskInputs inputs) {

        logger.info("Generating script the following elements: ");
        logger.info("--> Configuration: " + configuration.name);
        logger.info("--> MainClass: " + mainClass);
        logger.info("--> Template File: " + templateFilePath);
        logger.info("--> Output Dir path: " + destinationDir);
        logger.info("--> Output File name: " + destinationFileName);
        logger.info("--> Parameters: " + params);

        Map<String, String> options = new HashMap<>();
        if (params != null && params.length > 0)
            for (String param : params) {
                String[] part = param.split(":")
                options.put(part[0], part[1]);
            }
        if (mainClass != null && !mainClass.equals("")) {
            options.put("mainClass", mainClass)
        }

        List<String> classpathList = new ArrayList<>();
        def scriptGeneration = new ScriptGeneration(getProject())
        String classpathFileName = scriptGeneration.getGeneratedDirPath() + "/classpath${configuration.name}/classpath.txt"
        new File(classpathFileName).eachLine { classpathLine ->
            classpathList.add(classpathLine);
        }
        options.put("classpathList", classpathList);
        String result = processTemplate(new File(templateFilePath), options)
        new File(outputGenerationDir, destinationDir).mkdirs()
        new File(new File(outputGenerationDir, destinationDir), destinationFileName) << result

        logger.info("Generated.");
    }

    String processTemplate(File templateInputFile, Map<String, Object> params) {

        String content;
        try {

            VelocityEngine velocityEngine = new VelocityEngine();
            velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, templateInputFile.getParent());
            velocityEngine.init();

            //-- Make VelocityContext Engine
            VelocityContext velocityContext = new VelocityContext();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                velocityContext.put(entry.getKey(), entry.getValue());
            }

            StringWriter resultWriter = new StringWriter();
            InputStream input = new FileInputStream(templateInputFile);
            if (input == null) {
                throw new RuntimeException("Template file doesn't exist");
            }

            InputStreamReader reader = new InputStreamReader(input, "UTF-8");

            if (!velocityEngine.evaluate(velocityContext, resultWriter, templateInputFile.getName(), reader)) {
                throw new RuntimeException("Failed to convert the template into html.");
            }

            content = resultWriter.toString();

            resultWriter.flush();

            resultWriter.close();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        return content;
    }
}