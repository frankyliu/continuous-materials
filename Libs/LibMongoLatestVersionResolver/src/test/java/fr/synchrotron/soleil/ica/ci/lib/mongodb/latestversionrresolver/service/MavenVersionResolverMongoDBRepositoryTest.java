package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.LatestArtifactRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service.data.MongoDBDataRepository;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Gregory Boissinot
 */
public class MavenVersionResolverMongoDBRepositoryTest extends AbstractVersionTest {

    private static MongoDBDataRepository mongoDBDataRepository = new MongoDBDataRepository();

    @BeforeClass
    public static void setupData() throws IOException {
        mongoDBDataRepository.setup();
    }

    public static void cleanup() throws IOException {
        mongoDBDataRepository.cleanupDatabase();
    }

    @Override
    protected LatestArtifactRepository getArtifactRepository() {
        return mongoDBDataRepository.getArtifactRepository();
    }

    @Test
    public void testFixVersion() {
        Assert.assertEquals("X.Y.Z", resolveVersion("X.Y.Z"));
    }

    @Test
    public void latestNotManagedVersionOrStatus() {

        Assert.assertEquals("X.Y.Z.build", resolveVersion("X.Y.Z.build"));
        Assert.assertEquals("X.Y.Z.BUILD", resolveVersion("X.Y.Z.BUILD"));

        Assert.assertEquals("X.Y.Z.integration", resolveVersion("X.Y.Z.integration"));
        Assert.assertEquals("X.Y.Z.INTEGRATION", resolveVersion("X.Y.Z.INTEGRATION"));

        Assert.assertEquals("X.Y.Z.release", resolveVersion("X.Y.Z.release"));
        Assert.assertEquals("X.Y.Z.RELEASE", resolveVersion("X.Y.Z.RELEASE"));

        Assert.assertEquals("X.Y.Z.anyMavenStatus", resolveVersion("X.Y.Z.anyMavenStatus"));
        Assert.assertEquals("X.Y.Z.ANYMAVENSTATUS", resolveVersion("X.Y.Z.ANYMAVENSTATUS"));
    }

    @Test
    public void latestBuildStatus() {
        Assert.assertEquals("1.7.BUILD", resolveVersion("org1", "name1", "latest.build"));
        Assert.assertEquals("1.6.INTEGRATION", resolveVersion("org2", "name2", "latest.build"));
        Assert.assertEquals("1.5.RELEASE", resolveVersion("org3", "name3", "latest.build"));
    }

    @Test
    public void latestIntegrationStatus() {
        Assert.assertEquals("1.6.INTEGRATION", resolveVersion("org1", "name1", "latest.integration"));
        Assert.assertEquals("1.6.INTEGRATION", resolveVersion("org2", "name2", "latest.integration"));
        Assert.assertEquals("1.5.RELEASE", resolveVersion("org3", "name3", "latest.integration"));
    }

    @Test
    public void latestReleaseStatus() {
        Assert.assertEquals("1.5.RELEASE", resolveVersion("org1", "name1", "latest.release"));
        Assert.assertEquals("1.5.RELEASE", resolveVersion("org2", "name2", "latest.release"));
        Assert.assertEquals("1.5.RELEASE", resolveVersion("org3", "name3", "latest.release"));

        Assert.assertEquals("1.5.RELEASE", resolveVersion("org1", "name1", "latest"));
        Assert.assertEquals("1.5.RELEASE", resolveVersion("org2", "name2", "latest"));
        Assert.assertEquals("1.5.RELEASE", resolveVersion("org3", "name3", "latest"));

    }

}
