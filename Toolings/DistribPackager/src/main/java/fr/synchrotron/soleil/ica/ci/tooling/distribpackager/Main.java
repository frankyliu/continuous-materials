package fr.synchrotron.soleil.ica.ci.tooling.distribpackager;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrateur on 15/07/14.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        Main main = new Main();
        main.process();
    }

    private void process() throws IOException {
        File packageDescriptorFile = new File("C:\\Dev\\Soleil\\continuous-materials\\Toolings\\DistribPackager\\src\\main\\resources\\samplePackage.json");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        DistribObj distribObj = objectMapper.readValue(packageDescriptorFile, DistribObj.class);
        System.out.println(distribObj);

        List<String> componentNameList = new ArrayList<>();
        List<DistribComponent> distrib = distribObj.getDistrib();
        for (DistribComponent distribComponent : distrib) {
            componentNameList.add(distribComponent.getName());
        }
        generateGradleBuildScript(componentNameList);
    }

    private void generateGradleBuildScript(List<String> componentNameList) {
        VelocityEngine velocityEngine = new VelocityEngine();
        Velocity.setProperty("runtime.log", "target/velocity.log");
        velocityEngine.init();
        File templateFile = new File("buildgradle.vm");
        Template template = velocityEngine.getTemplate(templateFile.getPath(), "UTF-8");
        VelocityContext velocityContext = new VelocityContext();
        StringWriter resultWriter = new StringWriter();

        velocityContext.put("components", componentNameList);
        template.merge(velocityContext, resultWriter);

        System.out.println(resultWriter.toString());
    }

}
