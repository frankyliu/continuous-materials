package fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.domain.template;

import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.exception.DistribPackagerException;

import java.io.File;
import java.util.Map;


/**
 * @author Gregory Boissinot
 */
public interface TemplateEngine {

    public String processTemplate(String templateContent, Map<String, Object> params) throws DistribPackagerException;

    public String processTemplate(File templateFile, Map<String, Object> params) throws DistribPackagerException;

}
