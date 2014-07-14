package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.mongodb.MongoDBArtifactRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBDataSource;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertNotNull;

/**
 * @author Gregory Boissinot
 */
public class MongoDBArtifactRepositoryTest {

    private static LatestArtifactRepository artifactRepository;

    @BeforeClass
    public static void setup() {
        artifactRepository = new MongoDBArtifactRepository(Mockito.mock(MongoDBDataSource.class));
    }

    @Test
    public void testName() {
        assertNotNull(artifactRepository.getName());
    }

    @Test(expected = NullPointerException.class)
    public void testNullOrg() {
        artifactRepository.getLatestVersion(null, "aName", "aType", "aStatus");
    }

    @Test(expected = NullPointerException.class)
    public void testNullName() {
        artifactRepository.getLatestVersion("anOrg", null, "aType", "aStatus");
    }

    @Test(expected = NullPointerException.class)
    public void testNullType() {
        artifactRepository.getLatestVersion("anOrg", "aName", null, "aStatus");
    }

    @Test(expected = NullPointerException.class)
    public void testNullStatus() {
        artifactRepository.getLatestVersion("anOrg", "aName", "aType", null);
    }
}
