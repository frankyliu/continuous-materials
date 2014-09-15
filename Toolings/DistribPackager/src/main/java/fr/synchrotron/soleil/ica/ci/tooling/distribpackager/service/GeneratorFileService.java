package fr.synchrotron.soleil.ica.ci.tooling.distribpackager.service;

import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.template.TemplateProcessor;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.template.VelocityTemplateEngine;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.exception.DistribPackagerException;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * @author Gregory Boissinot
 */
public class GeneratorFileService {

    public void generate(
            File templateFile,
            Map<String, Object> context,
            File outputDirecty,
            String outputFileName) throws Throwable {


        outputDirecty.mkdirs();

        TemplateProcessor templateProcessor = new TemplateProcessor(new VelocityTemplateEngine());
        String content = templateProcessor.processTemplate(templateFile, context);
        System.out.println(content);

        FileWriter fileWriter = null;
        try {
            outputDirecty.createNewFile();
            File file = new File(outputDirecty, outputFileName);
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
