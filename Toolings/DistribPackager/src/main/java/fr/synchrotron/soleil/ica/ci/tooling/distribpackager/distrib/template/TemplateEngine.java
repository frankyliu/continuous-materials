package fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.template;

import java.io.File;
import java.util.Map;


/**
 * @author Gregory Boissinot
 */
public interface TemplateEngine {

    //public String process(String inputFileName, Map<String, Object> params) throws Throwable;

    public String processTemplate(File templateInputFile, Map<String, Object> params) throws Throwable;

    public String processInternalTemplate(String templateInputFilePath, Map<String, Object> params) throws Throwable;
}
