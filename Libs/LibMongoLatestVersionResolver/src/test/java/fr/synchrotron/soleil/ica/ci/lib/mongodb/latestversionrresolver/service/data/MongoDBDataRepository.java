package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service.data;

import com.github.fakemongo.Fongo;
import com.mongodb.DB;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.LatestArtifactRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.mongodb.MongoDBArtifactRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBDataSource;
import org.apache.commons.io.IOUtils;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class MongoDBDataRepository {

    private DB mongoDB;

    public void setup() throws IOException {
        Fongo fongo = new Fongo("testMongoServer");
        mongoDB = fongo.getDB("repo");
        Jongo jongo = new Jongo(mongoDB);
        MongoCollection artifactsLatestCollection =
                jongo.getCollection(MongoDBArtifactRepository.MONGODB_ARTIFACTS_LATEST_COLLECTION);
        loadMongoDBData(artifactsLatestCollection);
    }

    public void cleanupDatabase() throws IOException {
        if (mongoDB != null) {
            mongoDB.dropDatabase();
        }
    }

    private void loadMongoDBData(MongoCollection artifactsLatestCollection) throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("artifacts.latest.data");
        if (inputStream == null) {
            throw new IllegalArgumentException("Can't find the artifacts.latest file.");
        }
        final List<String> mongoDBDocs = IOUtils.readLines(inputStream);
        for (String mongoDBDoc : mongoDBDocs) {
            if (mongoDBDoc != null && !mongoDBDoc.trim().isEmpty()) {
                artifactsLatestCollection.insert(mongoDBDoc);
            }
        }
        inputStream.close();
    }

    public LatestArtifactRepository getArtifactRepository() {
        return new MongoDBArtifactRepository(new InMemoryMongoDBDataSource());
    }

    private class InMemoryMongoDBDataSource implements MongoDBDataSource {
        @Override
        public DB getMongoDB() {
            return mongoDB;
        }
    }


}
