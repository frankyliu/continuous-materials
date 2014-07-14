package fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.service.maven;

import fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.domain.maven.MavenArtifactDocument;
import fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.domain.maven.MavenArtifactType;
import org.apache.maven.index.ArtifactInfo;

import java.util.Date;

/**
 * @author Gregory Boissinot
 */
public class MavenDocumentBuilderService {

    private static final String ARTIFACT_RELEASE_STATUS = "RELEASE";

    public MavenArtifactDocument buildArtifactObj(ArtifactInfo artifactInfo) {
        MavenArtifactDocument mavenArtifactDocument = new MavenArtifactDocument();
        mavenArtifactDocument.setOrganisation(artifactInfo.groupId);
        mavenArtifactDocument.setName(artifactInfo.artifactId);
        mavenArtifactDocument.setVersion(artifactInfo.version);
        final String classifier = artifactInfo.classifier;
        if (classifier == null) {
            mavenArtifactDocument.setType(MavenArtifactType.PRIMARY.getType());
        } else {
            mavenArtifactDocument.setType(classifier);
        }
        mavenArtifactDocument.setStatus(ARTIFACT_RELEASE_STATUS);
        mavenArtifactDocument.setFileExtension(artifactInfo.fextension);
        mavenArtifactDocument.setFileSize(artifactInfo.size);
        mavenArtifactDocument.setCreationDate(new Date(artifactInfo.lastModified));

        return mavenArtifactDocument;
    }
}
