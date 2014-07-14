package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.domain.MavenInputArtifact;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.domain.MavenOutputArtifact;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.LatestArtifactRepository;
import org.junit.Assert;

/**
 * @author Gregory Boissinot
 */
public abstract class AbstractVersionTest {

    private MavenVersionResolverService mavenVersionResolverService;

    protected AbstractVersionTest() {
        mavenVersionResolverService = new MavenVersionResolverService(
                new ArtifactVersionResolverService(getArtifactRepository()));
    }

    protected abstract LatestArtifactRepository getArtifactRepository();

    protected String resolveVersion(String value) {
        final String TEST_GROUPID = "testGroupId";
        final String TEST_ARTIFACTID = "testArtifactId";
        return resolveVersion(TEST_GROUPID, TEST_ARTIFACTID, value);
    }

    protected String resolveVersion(String groupId, String artifactId, String verison) {
        final MavenOutputArtifact mavenOutputArtifact = mavenVersionResolverService
                .resolveArtifact(buildMavenInputArtifact(groupId, artifactId, verison));
        Assert.assertNotNull(mavenOutputArtifact);
        return mavenOutputArtifact.getVersion();
    }

    private MavenInputArtifact buildMavenInputArtifact(String groupId, String artifactId, String version) {
        return new MavenInputArtifact(groupId, artifactId, version);
    }

}
