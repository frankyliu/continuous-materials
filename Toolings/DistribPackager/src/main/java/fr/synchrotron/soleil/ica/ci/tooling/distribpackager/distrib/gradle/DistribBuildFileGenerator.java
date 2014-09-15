package fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.gradle;


import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.template.TemplateProcessor;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.template.VelocityTemplateEngine;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.exception.DistribPackagerException;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gregory Boissinot
 */
public class DistribBuildFileGenerator {

    private static final String DISTRIB_GRADLE_BUILD_FILE = "build.gradle.vm";

    private File outputDirecty;

    private String outputBuildFileName;

    public DistribBuildFileGenerator(File outputDirecty, String outputBuildFieName) {
        this.outputDirecty = outputDirecty;
        this.outputBuildFileName = outputBuildFieName;
    }

    public void generate(List<String/*org:artifactId*/> componentNameList) throws Throwable {

        Map<String, Object> params = new HashMap<>();
        params.put("components", componentNameList);
        TemplateProcessor templateProcessor = new TemplateProcessor(new VelocityTemplateEngine());
        String content = templateProcessor.processInternalTemplate(DISTRIB_GRADLE_BUILD_FILE, params);
        System.out.println(content);

        FileWriter fileWriter = null;
        try {
            outputDirecty.createNewFile();
            File file = new File(outputDirecty, outputBuildFileName);
            file.createNewFile();
            fileWriter = new FileWriter(file, false);
            IOUtils.write(content, fileWriter);

        } catch (IOException ioe) {
            throw new DistribPackagerException(ioe);
        } finally {
            IOUtils.closeQuietly(fileWriter);
        }
    }

}
