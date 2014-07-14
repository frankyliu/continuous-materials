package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Gregory Boissinot
 */
public class NullArtifactRepositoryTest {

    private static LatestArtifactRepository artifactRepository;

    @BeforeClass
    public static void setup() {
        artifactRepository = new NullArtifactRepository();
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

    @Test
    public void testNullResult() {
        assertNull(artifactRepository.getLatestVersion("anOrg", "aName", "aType", "aStatus"));
    }
}
