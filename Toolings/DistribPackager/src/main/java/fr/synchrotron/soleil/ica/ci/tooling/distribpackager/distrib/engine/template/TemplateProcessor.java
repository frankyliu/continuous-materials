package fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.engine.template;

import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.domain.template.TemplateEngine;

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

    public String processTemplate(String templateFileName, Map<String, Object> params) throws Throwable{

        if (templateFileName == null) {
            throw new NullPointerException("A template file name is required.");
        }

        if (params == null) {
            throw new NullPointerException("No parameter(s) set.");
        }

        return templateEngine.processTemplate(templateFileName, params);
    }

    public String processTemplate(File templateFile, Map<String, Object> params) throws Throwable{
        return templateEngine.processTemplate(templateFile, params);
    }

}
