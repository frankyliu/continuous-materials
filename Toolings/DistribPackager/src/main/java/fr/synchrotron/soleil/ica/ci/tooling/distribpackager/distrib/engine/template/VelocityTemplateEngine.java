package fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.engine.template;

import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.domain.template.TemplateEngine;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.exception.DistribPackagerException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.*;
import java.util.Map;

/**
 * @author Gregory Boissinot
 */
public class VelocityTemplateEngine implements TemplateEngine {


    @Override
    public String processTemplate(File templateInputFile, Map<String, Object> params) throws DistribPackagerException {

        String content = null;
        try {

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
            Template template = velocityEngine.getTemplate(templateInputFile.getName(), "UTF-8");

            //-- Make VelocityContext Engine
            VelocityContext velocityContext = new VelocityContext();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                velocityContext.put(entry.getKey(), entry.getValue());
            }

            StringWriter resultWriter = new StringWriter();
            InputStream input = new FileInputStream(templateInputFile);
            if (input == null) {
                throw new DistribPackagerException("Template file doesn't exist");
            }

            InputStreamReader reader = new InputStreamReader(input);

            if (!velocityEngine.evaluate(velocityContext, resultWriter, templateInputFile.getName(), reader)) {
                throw new DistribPackagerException("Failed to convert the template into html.");
            }

            content = resultWriter.toString();

            resultWriter.flush();

            resultWriter.close();
        } catch (Throwable e) {
            throw new DistribPackagerException(e);
        }

        return content;
    }

    @Override
    public String processTemplate(String templateFileName, Map<String, Object> params) throws DistribPackagerException {

        String content = null;
        try {
            //- check parameters
            if (templateFileName == null) {
                throw new NullPointerException("An template file name  is required.");
            }

            if (params == null) {
                throw new NullPointerException("No parameter(s) set.");
            }

            VelocityEngine velocityEngine = new VelocityEngine();
            velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
            velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());


            velocityEngine.init();

            //-- Retrieve template
            Template template;
            try {
                template = velocityEngine.getTemplate(templateFileName, "UTF-8");
            } catch (ResourceNotFoundException rne) {
                throw new DistribPackagerException(rne);
            }

            //-- Make VelocityContext Engine
            VelocityContext velocityContext = new VelocityContext();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                velocityContext.put(entry.getKey(), entry.getValue());
            }

            StringWriter resultWriter = new StringWriter();

            InputStream input = this.getClass().getClassLoader().getResourceAsStream(templateFileName);
            if (input == null) {
                throw new DistribPackagerException("Template file doesn't exist");
            }

            InputStreamReader reader = new InputStreamReader(input);

            if (!velocityEngine.evaluate(velocityContext, resultWriter, templateFileName, reader)) {
                throw new DistribPackagerException("Failed to convert the template into html.");
            }

            content = resultWriter.toString();

            resultWriter.flush();

            resultWriter.close();
        } catch (Throwable e) {
            throw new DistribPackagerException(e);
        }

        return content;
    }

}
