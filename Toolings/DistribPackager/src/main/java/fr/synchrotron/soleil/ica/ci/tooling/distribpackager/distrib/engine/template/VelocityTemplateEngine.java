package fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.engine.template;

import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.domain.template.TemplateEngine;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.exception.DistribPackagerException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;

import java.io.*;
import java.util.Map;

/**
 * @author Gregory Boissinot
 */
public class VelocityTemplateEngine implements TemplateEngine {

    @Override
    public String processTemplate(File templateInputFile, Map<String, Object> params) throws DistribPackagerException {

        //- check parameters
        if (templateInputFile == null) {
            throw new NullPointerException("An template inputFile  is required.");
        }

        if (params == null) {
            throw new NullPointerException("No parameter(s) set.");
        }

        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, templateInputFile.getParent());

        velocityEngine.init();

        //-- Retrieve template
        Template template;
        try {
            template = velocityEngine.getTemplate(templateInputFile.getName(), "UTF-8");
        } catch (ResourceNotFoundException rne) {
            throw new DistribPackagerException(rne);
        }

        //-- Make VelocityContext Engine
        VelocityContext velocityContext = new VelocityContext();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            velocityContext.put(entry.getKey(), entry.getValue());
        }

        StringWriter resultWriter = new StringWriter();

        InputStream input;
        try {
            input = new FileInputStream(templateInputFile);
        } catch (FileNotFoundException fne) {
            throw new DistribPackagerException(fne);
        }
        if (input == null) {
            throw new DistribPackagerException("Template file doesn't exist");
        }

        InputStreamReader reader = new InputStreamReader(input);

        if (!velocityEngine.evaluate(velocityContext, resultWriter, templateInputFile.getName(), reader)) {
            throw new DistribPackagerException("Failed to convert the template into html.");
        }

        String content = resultWriter.toString();

        resultWriter.flush();
        try {
            resultWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

}
