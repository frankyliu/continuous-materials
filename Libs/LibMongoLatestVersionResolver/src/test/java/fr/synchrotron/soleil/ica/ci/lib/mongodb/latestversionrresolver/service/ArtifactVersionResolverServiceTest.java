package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.NullArtifactRepository;
import fr.synchrotron.soleil.ica.ci.lib.workflow.DefaultWorkflow;
import fr.synchrotron.soleil.ica.ci.lib.workflow.WorkflowEngine;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Gregory Boissinot
 */
public class ArtifactVersionResolverServiceTest {

    @Test(expected = NullPointerException.class)
    public void tesNullArtifactRepositoryConstructorOneParameter() {
        new ArtifactVersionResolverService(null);
    }

    @Test
    public void tesArtifactRepositoryConstructorOneParameter() {
        new ArtifactVersionResolverService(new NullArtifactRepository());
        assertTrue(true);
    }

    @Test
    public void tesArtifactRepositoryConstructorTwoParameters() {
        new ArtifactVersionResolverService(new NullArtifactRepository(), new DefaultWorkflow());
        assertTrue(true);
    }

    @Test(expected = NullPointerException.class)
    public void tesNullWorkflowConstructorTwoParameters() {
        new ArtifactVersionResolverService(new NullArtifactRepository(), null);
    }

    @Test(expected = NullPointerException.class)
    public void getLastVersionStatusWithOrgNull() {
        ArtifactVersionResolverService resolverService =
                new ArtifactVersionResolverService(new NullArtifactRepository(), new DefaultWorkflow());
        resolverService.getLatestVersion(null, "aName", "aStatus");
    }

    @Test(expected = NullPointerException.class)
    public void getLastVersionStatusWithNameNull() {
        ArtifactVersionResolverService resolverService =
                new ArtifactVersionResolverService(new NullArtifactRepository(), new DefaultWorkflow());
        resolverService.getLatestVersion("anOrg", null, "aStatus");
    }

    @Test(expected = NullPointerException.class)
    public void getLastVersionStatusWithStatusNull() {
        ArtifactVersionResolverService resolverService =
                new ArtifactVersionResolverService(new NullArtifactRepository(), new DefaultWorkflow());
        resolverService.getLatestVersion("anOrg", "aName", null);
    }

    @Test(expected = NullPointerException.class)
    public void getLastVersionWithOrgNull() {
        ArtifactVersionResolverService resolverService =
                new ArtifactVersionResolverService(new NullArtifactRepository(), new DefaultWorkflow());
        resolverService.getLatestVersion(null, "aName");
    }

    @Test(expected = NullPointerException.class)
    public void getLastVersionWithNameNull() {
        ArtifactVersionResolverService resolverService =
                new ArtifactVersionResolverService(new NullArtifactRepository(), new DefaultWorkflow());
        resolverService.getLatestVersion("anOrg", null);
    }

}
