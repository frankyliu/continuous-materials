package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.LatestArtifactRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.NullArtifactRepository;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Boissinot
 */
public class MavenVersionResolverNullRepositoryTest extends AbstractVersionTest {

    @Override
    protected LatestArtifactRepository getArtifactRepository() {
        return new NullArtifactRepository();
    }

    @Test
    public void testFixVersion() {
        Assert.assertEquals("X.Y.Z", resolveVersion("X.Y.Z"));
    }

    @Test
    public void buildStatus() {
        Assert.assertEquals("X.Y.Z.build", resolveVersion("X.Y.Z.build"));
        Assert.assertEquals("X.Y.Z.BUILD", resolveVersion("X.Y.Z.BUILD"));
        Assert.assertEquals("latest.build", resolveVersion("latest.build"));
        Assert.assertEquals("latest.BUILD", resolveVersion("latest.BUILD"));
    }

    @Test
    public void integrationStatus() {
        Assert.assertEquals("X.Y.Z.integration", resolveVersion("X.Y.Z.integration"));
        Assert.assertEquals("X.Y.Z.INTEGRATION", resolveVersion("X.Y.Z.INTEGRATION"));
        Assert.assertEquals("latest.integration", resolveVersion("latest.integration"));
        Assert.assertEquals("latest.INTEGRATION", resolveVersion("latest.INTEGRATION"));
    }

    @Test
    public void releaseStatus() {
        Assert.assertEquals("X.Y.Z.release", resolveVersion("X.Y.Z.release"));
        Assert.assertEquals("X.Y.Z.RELEASE", resolveVersion("X.Y.Z.RELEASE"));
        Assert.assertEquals("latest.release", resolveVersion("latest.release"));
        Assert.assertEquals("latest.RELEASE", resolveVersion("latest.RELEASE"));
    }

    @Test
    public void otherStatus() {
        Assert.assertEquals("X.Y.Z.anyMavenStatus", resolveVersion("X.Y.Z.anyMavenStatus"));
        Assert.assertEquals("X.Y.Z.ANYMAVENSTATUS", resolveVersion("X.Y.Z.ANYMAVENSTATUS"));
        Assert.assertEquals("latest.anyLevercaseStatus", resolveVersion("latest.anyLevercaseStatus"));
        Assert.assertEquals("latest.ANYUPPERCASESTATUS", resolveVersion("latest.ANYUPPERCASESTATUS"));
    }

}
