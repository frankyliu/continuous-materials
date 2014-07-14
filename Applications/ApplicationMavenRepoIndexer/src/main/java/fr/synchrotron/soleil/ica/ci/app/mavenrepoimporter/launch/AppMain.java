package fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.launch;

import fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.service.mongodb.MongoImportService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Gregory Boissinot
 */
public class AppMain {

    public static void main(String[] args) throws Exception {

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.getEnvironment().setActiveProfiles("mongodb");
        applicationContext.register(JavaConfig.class);
        applicationContext.refresh();

        MongoImportService mongoImportService = applicationContext.getBean(MongoImportService.class);
        mongoImportService.importArtifacts();
    }


}
