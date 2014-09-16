package fr.synchrotron.soleil.ica.ci.tooling.distribpackager.part.classpath;

import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.domain.gradle.GradleConfig;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.domain.gradle.ProjectConfig;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.engine.gradle.GradleTaskRunnerService;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.engine.template.TemplateProcessor;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.engine.template.VelocityTemplateEngine;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.exception.DistribPackagerException;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.*;

/**
 * @author Gregory Boissinot
 */
public class ComponentClasspathGenerator {

    private static final String CLASSPATH_GRADLE_BUILD_FILE = "build.gradle.classpath.vm";
    private static final String TASK_NAME_PRINTCLASSPATH = "printClasspath";

    private String outputDirecty;

    private String outputBuildFileName;

    private GradleConfig gradleConfig;

    public ComponentClasspathGenerator(String outputDirecty, String outputBuildFileName, GradleConfig gradleConfig) {
        this.outputDirecty = outputDirecty;
        this.outputBuildFileName = outputBuildFileName;
        this.gradleConfig = gradleConfig;
    }

    public ClasspathObj getClasspathObjForComponent(String component) throws Throwable {
        ProjectClassPathObj projectClassPathObj = getProjectClassPathObj(component);
        List<DependencyClasspathObj> dependencyClasspathObjList = getClasspathFromComponent(component);
        return new ClasspathObj(projectClassPathObj, dependencyClasspathObjList);
    }

    private ProjectClassPathObj getProjectClassPathObj(String componentName) {
        //TODO check
        String[] parts = componentName.split(":");
        return new ProjectClassPathObj(parts[1], parts[2]);
    }

    private List<DependencyClasspathObj> getClasspathFromComponent(String fullComponentName) throws Throwable {
        //-- 1. Generate the build file to run

        Map<String, Object> params = new HashMap<>();
        params.put("components", Arrays.asList(new String[]{fullComponentName}));
        TemplateProcessor templateProcessor = new TemplateProcessor(new VelocityTemplateEngine());
        String content = templateProcessor.processTemplate(new File(this.getClass().getResource("/" + CLASSPATH_GRADLE_BUILD_FILE).toURI()), params);
//        System.out.println(content);

        try {
            new File(outputDirecty).mkdirs();
            File file = new File(outputDirecty, outputBuildFileName);
            file.createNewFile();
            final FileWriter outputFileWriter = new FileWriter(file, false);
            IOUtils.write(content, outputFileWriter);
            outputFileWriter.close();
        } catch (IOException ioe) {
            throw new DistribPackagerException(ioe);
        }

        //-- 2. Run the build file
        ProjectConfig projectConfig = new ProjectConfig("build.gradle", outputDirecty);
        GradleTaskRunnerService gradleTaskRunnerService = new GradleTaskRunnerService(gradleConfig, projectConfig);
        gradleTaskRunnerService.runTask(TASK_NAME_PRINTCLASSPATH);

        //--3. Extract the generated classpath from the gradle run
        String classpathContent = null;
        try {
            File classpathResultFile = new File(outputDirecty, "classpathResult.txt");
            classpathContent = IOUtils.toString(new FileInputStream(classpathResultFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<DependencyClasspathObj> classpathObjs = new ArrayList<>();
        StringReader stringReader = new StringReader(classpathContent);
        BufferedReader bufferedReader = new BufferedReader(stringReader);
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                //TODO check
                String[] split = line.split(":");
                //TODO check
                DependencyClasspathObj dependencyClasspathObj = new DependencyClasspathObj(split[1], split[2]);
                classpathObjs.add(dependencyClasspathObj);
            }

        } catch (IOException ioe) {
            throw new DistribPackagerException(ioe);
        }

        return classpathObjs;
    }
}
