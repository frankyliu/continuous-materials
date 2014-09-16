package fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.domain.template;

import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.exception.DistribPackagerException;

import java.io.File;
import java.util.Map;


/**
 * @author Gregory Boissinot
 */
public interface TemplateEngine {

    public String processTemplate(File templateInputFile, Map<String, Object> params) throws DistribPackagerException;

}
