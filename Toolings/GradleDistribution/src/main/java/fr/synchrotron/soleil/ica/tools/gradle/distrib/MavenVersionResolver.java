package fr.synchrotron.soleil.ica.tools.gradle.distrib;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.mongodb.MongoDBArtifactRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service.ArtifactVersionResolverService;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service.MavenVersionResolverService;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.BasicMongoDBDataSource;

import java.util.Map;

/**
 * @author Gregory Boissinot
 */
public class MavenVersionResolver {

    private Map<String, String> configMongo;

    public MavenVersionResolver(Map<String, String> configMongo) {
        this.configMongo = configMongo;
    }

    public String getLatestVersion(String group, String name) {
        final BasicMongoDBDataSource mongoDBDatasource = new BasicMongoDBDataSource(configMongo);
        final MongoDBArtifactRepository artifactRepository = new MongoDBArtifactRepository(mongoDBDatasource);
        final ArtifactVersionResolverService artifactVersionResolverService = new ArtifactVersionResolverService(artifactRepository);
        MavenVersionResolverService resolverService = new MavenVersionResolverService(artifactVersionResolverService);
        return resolverService.getLatestArtifact(group, name);
    }

}
