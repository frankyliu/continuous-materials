package fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.template;

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
    public String processTemplate(File templateInputFile, Map<String, Object> params) throws Throwable {

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

        InputStream input = new FileInputStream(templateInputFile);
        if (input == null) {
            throw new IOException("Template file doesn't exist");
        }

        InputStreamReader reader = new InputStreamReader(input);

        if (!velocityEngine.evaluate(velocityContext, resultWriter, templateInputFile.getName(), reader)) {
            throw new DistribPackagerException("Failed to convert the template into html.");
        }
//        template.merge(velocityContext, resultWriter);

        String content = resultWriter.toString();

        resultWriter.flush();
        try {
            resultWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    @Override
    public String processInternalTemplate(String templateInputFilePath, Map<String, Object> params) throws Throwable {

        //- check parameters
        if (templateInputFilePath == null) {
            throw new NullPointerException("An template inputFile Name is required.");
        }

        if (params == null) {
            throw new NullPointerException("No parameter(s) set.");
        }

        VelocityEngine velocityEngine = new VelocityEngine();
        //Velocity.setProperty("runtime.log", "target/velocity.log");
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

        velocityEngine.init();

        //-- Retrieve template
        Template template;
        try {
            template = velocityEngine.getTemplate(templateInputFilePath, "UTF-8");
        } catch (ResourceNotFoundException rne) {
            throw new DistribPackagerException(rne);
        }

        //-- Make VelocityContext Engine
        VelocityContext velocityContext = new VelocityContext();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            velocityContext.put(entry.getKey(), entry.getValue());
        }

        StringWriter resultWriter = new StringWriter();


        InputStream input = this.getClass().getClassLoader().getResourceAsStream(templateInputFilePath);
        if (input == null) {
            throw new IOException("Template file doesn't exist");
        }

        InputStreamReader reader = new InputStreamReader(input);

        if (!velocityEngine.evaluate(velocityContext, resultWriter, templateInputFilePath, reader)) {
            throw new DistribPackagerException("Failed to convert the template into html.");
        }
//        template.merge(velocityContext, resultWriter);

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
