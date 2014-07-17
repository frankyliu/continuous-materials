package fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.template;

import java.io.File;
import java.util.Map;

/**
 * @author Gregory Boissinot
 */
public class TemplateProcessor {

    private final TemplateEngine templateEngine;

    public TemplateProcessor(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String processInternalTemplate(String inputFileName, Map<String, Object> params)throws Throwable{

        //- check parameters
        if (inputFileName == null) {
            throw new NullPointerException("An inputFile is required.");
        }

        if (params == null) {
            throw new NullPointerException("No parameter(s) set.");
        }

        return templateEngine.processInternalTemplate(inputFileName, params);
    }

    public String processTemplate(File templateInputFile, Map<String, Object> params) throws Throwable{
        return templateEngine.processTemplate(templateInputFile, params);
    }
}
