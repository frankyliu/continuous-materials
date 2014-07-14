package fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.service.mongodb.integration;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocument;
import org.springframework.integration.annotation.Transformer;

/**
 * @author Gregory Boissinot
 */
public class ThirdPartyArtifactEnricher {

    @Transformer
    @SuppressWarnings("unused")
    public ArtifactDocument enrich(ArtifactDocument artifactObj, String repoURL) {
        if (repoURL.contains("thirdparty")) {
            artifactObj.setThirdParty(true);
        }
        return artifactObj;
    }
}
