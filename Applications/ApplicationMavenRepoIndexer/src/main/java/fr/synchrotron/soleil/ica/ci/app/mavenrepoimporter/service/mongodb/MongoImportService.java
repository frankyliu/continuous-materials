package fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.service.mongodb;


import fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.service.MavenIndexerRetriever;
import fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.service.mongodb.integration.MongoDBIntegrationService;
import org.apache.maven.index.context.IndexingContext;

/**
 * @author Gregory Boissinot
 */
public class MongoImportService {

    private final String[] repoURLs;
    private final MavenIndexerRetriever mavenIndexerRetriever;
    private final MongoDBIntegrationService mongoDBIntegrationService;

    public MongoImportService(MavenIndexerRetriever mavenIndexerRetriever, String[] repoURLs, MongoDBIntegrationService mongoDBIntegrationService) {
        this.mavenIndexerRetriever = mavenIndexerRetriever;
        this.repoURLs = repoURLs;
        this.mongoDBIntegrationService = mongoDBIntegrationService;
    }

    public void importArtifacts() throws Exception {
        for (String repoURL : repoURLs) {
            importArtifacts(repoURL);
        }
    }

    private void importArtifacts(String repoURL) throws Exception {
        IndexingContext repoMavenContext = mavenIndexerRetriever.getMavenIndex(repoURL);

        System.out.println("-----------------------");
        System.out.println(" PERSIST WITH MONGODB");
        mongoDBIntegrationService.insert(repoMavenContext, repoURL);
        System.out.println("-----------------------");
    }
}
