package fr.synchrotron.soleil.ica.ci.lib.mongodb.repository;

import com.mongodb.WriteConcern;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocumentKey;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.repository.exception.NoDocumentException;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.repository.exception.NonUniqueDocumentException;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBDataSource;
import org.jongo.MongoCollection;

import java.util.Iterator;

/**
 * @author Gregory Boissinot
 */
public class ArtifactRepository extends AbstractRepository {

    public ArtifactRepository(MongoDBDataSource mongoDBDataSource) {
        super(mongoDBDataSource);
    }

    public ArtifactDocument findArtifactDocument(ArtifactDocumentKey artifactDocumentKey) {

        MongoCollection artifacts = jongo.getCollection(ArtifactDocument.MONGO_ARTIFACTS_COLLECTION_NAME);
        String query = getStringQuery(artifactDocumentKey);

        final Iterable<ArtifactDocument> artifactDocuments = artifacts.find(query).as(ArtifactDocument.class);
        final Iterator<ArtifactDocument> artifactDocumentIterator = artifactDocuments.iterator();
        if (!artifactDocumentIterator.hasNext()) {
            throw new NoDocumentException("At least one Artifact document must match criteria.");
        }

        final ArtifactDocument artifactDocument = artifactDocumentIterator.next();
        if (artifactDocumentIterator.hasNext()) {
            throw new NonUniqueDocumentException("Only one Artifact document must be returned.");
        }

        return artifactDocument;
    }

    public boolean isArtifactDocumentAlreadyExists(ArtifactDocumentKey artifactDocumentKey) {
        MongoCollection artifacts = jongo.getCollection(ArtifactDocument.MONGO_ARTIFACTS_COLLECTION_NAME);
        return artifacts.count(getStringQuery(artifactDocumentKey)) != 0;
    }

    public void updateArtifactDocument(ArtifactDocument artifactDocument) {
        MongoCollection artifacts = jongo.getCollection(ArtifactDocument.MONGO_ARTIFACTS_COLLECTION_NAME);
        artifacts.withWriteConcern(WriteConcern.SAFE);
        artifacts.update(getStringQuery(artifactDocument.getKey())).with(artifactDocument);
    }

    public void insertArtifactDocument(ArtifactDocument artifactDocument) {
        MongoCollection artifacts = jongo.getCollection(ArtifactDocument.MONGO_ARTIFACTS_COLLECTION_NAME);
        artifacts.insert(artifactDocument);
    }

    public void deleteArtifactsCollection() {
        MongoCollection projectsCollection = jongo.getCollection(ArtifactDocument.MONGO_ARTIFACTS_COLLECTION_NAME);
        projectsCollection.remove();
    }

}
