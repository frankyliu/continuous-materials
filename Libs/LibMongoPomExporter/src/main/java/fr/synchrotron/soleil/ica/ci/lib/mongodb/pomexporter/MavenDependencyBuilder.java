package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDependency;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDependencyExclusion;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service.ArtifactVersionResolverService;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Exclusion;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class MavenDependencyBuilder {

    private ArtifactVersionResolverService resolverService;

    public MavenDependencyBuilder(ArtifactVersionResolverService resolverService) {
        this.resolverService = resolverService;
    }

    public Dependency getDependency(ArtifactDependency artifactDependency) {
        Dependency dependency = new Dependency();
        dependency.setGroupId(artifactDependency.getOrg());
        dependency.setArtifactId(artifactDependency.getName());

        final String latestVersion = resolverService.getLatestVersion(artifactDependency.getOrg(), artifactDependency.getName());
        if (latestVersion == null) {
            //Case of third party or not tracked;
            dependency.setVersion(artifactDependency.getVersion());
        } else {
            dependency.setVersion(latestVersion);
        }

        dependency.setScope(artifactDependency.getScope());
        List<ArtifactDependencyExclusion> artifactDependencyExclusions = artifactDependency.getExclusions();
        List<Exclusion> exclusions = new ArrayList<Exclusion>();
        for (ArtifactDependencyExclusion artifactDependencyExclusion : artifactDependencyExclusions) {
            Exclusion ex = new Exclusion();
            ex.setGroupId(artifactDependencyExclusion.getGroupId());
            ex.setArtifactId(artifactDependencyExclusion.getArtifactId());
            exclusions.add(ex);
        }
        dependency.setExclusions(exclusions);

        return dependency;
    }
}
