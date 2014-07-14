package fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.service.mongodb;

import fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.domain.maven.MavenArtifactDocument;
import fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.service.maven.MavenDocumentBuilderService;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocument;
import org.apache.maven.index.ArtifactInfo;

import java.util.Date;

/**
 * @author Gregory Boissinot
 */
public class MongoDBDocumentBuilderService {

    private final MavenDocumentBuilderService mavenDocumentBuilderService;

    public MongoDBDocumentBuilderService(MavenDocumentBuilderService mavenDocumentBuilderService) {
        this.mavenDocumentBuilderService = mavenDocumentBuilderService;
    }

    public ArtifactDocument buildArtifactObj(ArtifactInfo artifactInfo) {

        MavenArtifactDocument mavenArtifactDocument = mavenDocumentBuilderService.buildArtifactObj(artifactInfo);
        ArtifactDocument mongoDBArtifactObj = new ArtifactDocument();

        mongoDBArtifactObj.setOrg(mavenArtifactDocument.getOrganisation());
        mongoDBArtifactObj.setName(mavenArtifactDocument.getName());
        mongoDBArtifactObj.setVersion(mavenArtifactDocument.getVersion());
        mongoDBArtifactObj.setStatus(mavenArtifactDocument.getStatus());

        mongoDBArtifactObj.setType(mavenArtifactDocument.getType());
        mongoDBArtifactObj.setSha1(mavenArtifactDocument.getSha1());
        mongoDBArtifactObj.setMd5(mavenArtifactDocument.getMd5());
        mongoDBArtifactObj.setDescription(mavenArtifactDocument.getDescription());
        mongoDBArtifactObj.setCreationDate(mavenArtifactDocument.getCreationDate());
        mongoDBArtifactObj.setPublicationDate(new Date());
        mongoDBArtifactObj.setFileSize(mavenArtifactDocument.getFileSize());
        mongoDBArtifactObj.setFileExtension(mavenArtifactDocument.getFileExtension());
        mongoDBArtifactObj.setForce(false);

        return mongoDBArtifactObj;
    }
}
