package fr.synchrotron.soleil.ica.ci.app.mavenrepoindexer.service.mongodb.integration;

import fr.synchrotron.soleil.ica.ci.app.mavenrepoindexer.domain.maven.MavenArtifactType;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocument;
import org.springframework.integration.annotation.Filter;

/**
 * @author Gregory Boissinot
 */
public class PrimaryArtifactFilter {

    @Filter
    @SuppressWarnings("unused")
    public boolean filteringBinaryArtifacts(ArtifactDocument artifactObj) {
        return (MavenArtifactType.PRIMARY.getType().equals(artifactObj.getType()));
    }
}
