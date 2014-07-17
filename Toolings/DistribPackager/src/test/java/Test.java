import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.template.TemplateProcessor;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.distrib.template.VelocityTemplateEngine;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.part.classpath.DependencyClasspathObj;
import fr.synchrotron.soleil.ica.ci.tooling.distribpackager.part.classpath.ProjectClassPathObj;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrateur on 17/07/14.
 */
public class Test {

    public static void main(String[] args) throws Throwable {

        Test test = new Test();
        test.process();
    }

    private void process() throws Throwable {
        Map<String, Object> classPathContext = new HashMap<>();

        ProjectClassPathObj projectClassPathObj = new ProjectClassPathObj("toto", "1.0");
        classPathContext.put("project", projectClassPathObj);

        List<DependencyClasspathObj> dependencies = new ArrayList<>();
        dependencies.add(new DependencyClasspathObj("artti1", "v1"));
        dependencies.add(new DependencyClasspathObj("artti2", "v2"));
        dependencies.add(new DependencyClasspathObj("artti3", "v3"));
        classPathContext.put("dependencies", dependencies);

        String templateFile = "template.txt";

        TemplateProcessor templateProcessor = new TemplateProcessor(new VelocityTemplateEngine());
        String content = templateProcessor.processInternalTemplate(templateFile, classPathContext);
        System.out.println(content);
    }
}
