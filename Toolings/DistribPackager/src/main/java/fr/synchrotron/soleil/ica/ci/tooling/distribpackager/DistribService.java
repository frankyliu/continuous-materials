package fr.synchrotron.soleil.ica.ci.tooling.distribpackager;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.domain.PackageConfig;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.engine.gradle.DistribBuildFileGenerator;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.domain.template.DistribComponent;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.domain.template.DistribObj;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.domain.template.PlatformObj;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.exception.DistribPackagerException;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.domain.gradle.GradleConfig;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.part.classpath.ClasspathObj;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.part.classpath.ComponentClasspathGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gregory Boissinot
 */
public class DistribService {

    private final PackageConfig packageConfig;

    public DistribService(PackageConfig packageConfig) {
        this.packageConfig = packageConfig;
    }

    public void makePackage() throws Throwable {
        final DistribObj distribObj = loadDescriptorFile(packageConfig.getPackageDescriptorFile());
        process(distribObj);
    }

    private DistribObj loadDescriptorFile(File packageDescriptorFile) {

        if (packageDescriptorFile == null) {
            throw new NullPointerException("A package Descriptor File is required.");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        try {
            return objectMapper.readValue(packageDescriptorFile, DistribObj.class);
        } catch (IOException ioe) {
            throw new DistribPackagerException(ioe);
        }
    }

    private void process(DistribObj distribObj) throws Throwable {

        //--1. Mapping each file
        List<DistribComponent> distrib = distribObj.getDistrib();
        for (DistribComponent distribComponent : distrib) {

            Map<String, Object> currentContext = new HashMap<>();

            //-- a. Retrieve classpath
            computeCurrentComponent(distribComponent.getName(), currentContext);

            //-- b. Process Template files from platform

            String mainClasspath = distribComponent.getMainClasspath();
            if (mainClasspath != null) {
                currentContext.put("mainClass", mainClasspath);
            }
            Map<String, Object> extraOptions = distribComponent.getExtraOptions();
            if (extraOptions != null) {
                currentContext.putAll(extraOptions);
            }

            List<PlatformObj> platforms = distribComponent.getPlatforms();
            if (platforms == null) {
                throw new DistribPackagerException("You must specify platform profil to package.");
            }

            for (PlatformObj platform : platforms) {
                // Compute the current component file

                //TODO rename in os name
                System.out.println("Generation Tenplate file for" + platform.getOs());

                GeneratorFileService service = new GeneratorFileService();
                //TODO rename in outputDirectoryName
                File outputDirTemplateFile = new File(packageConfig.getOutputDirFile().getAbsolutePath() + "/generated", platform.getOutputDirectory());
                service.generate(
                        new File(platform.getTemplateFilePath()),
                        currentContext,
                        outputDirTemplateFile,
                        platform.getFileName());

            }
        }


        //--2. Generate the end build.gradle
        generateDistribGradleBuildFile(distribObj);
    }

    private void computeCurrentComponent(String componentName, Map<String, Object> currentContext) throws DistribPackagerException {

        String[] parts = componentName.split(":");
        String artifactId = parts[1];
        //TODO
        final File outputDirecty = new File(packageConfig.getOutputDirFile().getAbsolutePath() + "/tmp/tmpGradleBuildClasspath/" + artifactId);
        outputDirecty.delete();
        outputDirecty.mkdirs();
        ComponentClasspathGenerator componentClasspathGenerator =
                new ComponentClasspathGenerator(outputDirecty.getAbsolutePath(), "build.gradle", new GradleConfig(packageConfig.getGradleInstallationDirFile().getAbsolutePath()));
        ClasspathObj classpathObjForComponent = null;
        try {
            classpathObjForComponent = componentClasspathGenerator.getClasspathObjForComponent(componentName);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        currentContext.put("project", classpathObjForComponent.getProject());
        currentContext.put("dependencies", classpathObjForComponent.getDependencies());

    }

    private void generateDistribGradleBuildFile(DistribObj distribObj) throws Throwable {
        List<String> componentNameList = new ArrayList<>();
        List<DistribComponent> distrib = distribObj.getDistrib();
        for (DistribComponent distribComponent : distrib) {
            componentNameList.add(distribComponent.getName());
        }
        DistribBuildFileGenerator distribBuildFileGenerator = new DistribBuildFileGenerator(packageConfig.getOutputDirFile(), "build.gradle");
        distribBuildFileGenerator.generate(componentNameList);
    }
}
