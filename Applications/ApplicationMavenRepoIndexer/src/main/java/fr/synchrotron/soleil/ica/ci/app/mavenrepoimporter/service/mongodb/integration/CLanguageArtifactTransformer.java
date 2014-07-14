package fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.service.mongodb.integration;

import fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.domain.maven.MavenCNameElement;
import fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.service.maven.artifact.c.ArtifactNameExtractor;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.ArtifactDocumentForC;
import org.springframework.integration.annotation.Transformer;

/**
 * @author Gregory Boissinot
 */
public class CLanguageArtifactTransformer {

    private ArtifactNameExtractor artifactNameExtractor;

    public CLanguageArtifactTransformer(ArtifactNameExtractor artifactNameExtractor) {
        this.artifactNameExtractor = artifactNameExtractor;
    }

    @Transformer
    @SuppressWarnings("unused")
    public ArtifactDocument addCMetadata(ArtifactDocument artifactObj, String repoURL) {

        if (repoURL.contains("native")) {
            String artifactOriginalName = artifactObj.getName();
            if (!artifactOriginalName.startsWith("super-")) {
                final MavenCNameElement cNameElement = artifactNameExtractor.extractMetadataFromName(artifactOriginalName);
                if (cNameElement != null) {
                    ArtifactDocumentForC artifactDocumentForC
                            = new ArtifactDocumentForC(
                            cNameElement.getArchi(),
                            cNameElement.getPlatform(),
                            cNameElement.getCompiler(),
                            cNameElement.getTypeDep(),
                            cNameElement.getMod());
                    artifactObj.setcLanguage(artifactDocumentForC);
                    artifactObj.setName(cNameElement.getName());
                }
            }
        }

        return artifactObj;
    }

}