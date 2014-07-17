package fr.synchrotron.soleil.ica.ci.tooling.distribpackager.part.classpath;

import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.template.TemplateProcessor;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.template.VelocityTemplateEngine;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.exception.DistribException;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.gradle.GradleConfig;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.gradle.GradleTaskRunnerService;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.gradle.ProjectConfig;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.*;

/**
 * @author Gregory Boissinot
 */
public class ComponentClasspathGenerator {

    private static final String CLASSPATH_GRADLE_BUILD_FILE = "build.gradle.classpath.vm";

    private File outputDirecty;

    private String outputBuildFileName;

    public ComponentClasspathGenerator(File outputDirecty, String outputBuildFieName) {
        this.outputDirecty = outputDirecty;
        this.outputBuildFileName = outputBuildFieName;
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
        String content = templateProcessor.processInternalTemplate(CLASSPATH_GRADLE_BUILD_FILE, params);
        System.out.println(content);

        try {
            outputDirecty.mkdirs();
            File file = new File(outputDirecty, outputBuildFileName);
            file.createNewFile();
            final FileWriter outputFileWriter = new FileWriter(file, false);
            IOUtils.write(content, outputFileWriter);
            outputFileWriter.close();
        } catch (IOException ioe) {
            throw new DistribException(ioe);
        }

        //-- 2. Run the build file
        //TODO
        GradleConfig gradleConfig = new GradleConfig(new File("/Users/gregory/Integ/gradle-1.11/"));
        ProjectConfig projectConfig = new ProjectConfig(new File("build.gradle"), outputDirecty);
        GradleTaskRunnerService gradleTaskRunnerService = new GradleTaskRunnerService(gradleConfig, projectConfig);
        gradleTaskRunnerService.runTask("printClasspath");

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
            throw new DistribException(ioe);
        }

        return classpathObjs;
    }
}
