package fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.service.mongodb.integration;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocument;

/**
 * @author Gregory Boissinot
 */
public class ArtifactErrorLogger {

    public void log(ArtifactDocument artifactObj) {
        System.out.println("Error inserting " + artifactObj);
    }
}
