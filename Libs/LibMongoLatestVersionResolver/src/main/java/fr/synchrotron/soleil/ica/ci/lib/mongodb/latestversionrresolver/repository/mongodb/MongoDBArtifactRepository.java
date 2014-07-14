package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.mongodb;

import com.mongodb.*;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.LatestArtifactRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBDataSource;

import java.util.logging.Logger;

/**
 * @author Gregory Boissinot
 */
public class MongoDBArtifactRepository implements LatestArtifactRepository {

    public static final String MONGODB_ARTIFACTS_LATEST_COLLECTION = "artifacts.latest";
    private static final Logger LOGGER = Logger.getLogger(MongoDBArtifactRepository.class.getName());
    private final MongoDBDataSource mongoDBDatasource;

    public MongoDBArtifactRepository(MongoDBDataSource mongoDBDatasource) {
        this.mongoDBDatasource = mongoDBDatasource;
    }

    @Override
    public String getName() {
        return "MongoDB Repository";
    }

    @Override
    public String getLatestVersion(String requestedOrg,
                                   String requestedName,
                                   String requestedType,
                                   String requestedStatus) {

        if (requestedOrg == null) {
            throw new NullPointerException("A requested organisation is required.");
        }

        if (requestedName == null) {
            throw new NullPointerException("A requested name is required.");
        }

        if (requestedType == null) {
            throw new NullPointerException("A requested type is required.");
        }

        if (requestedStatus == null) {
            throw new NullPointerException("A requested status is required.");
        }

        LOGGER.fine(String.format("Resolving the latest version against '%s'.", getName()));

        String latestVersion = null;
        DBCursor cursor = null;
        try {
            DB mongoDB = mongoDBDatasource.getMongoDB();

            DBCollection coll = mongoDB.getCollection(MONGODB_ARTIFACTS_LATEST_COLLECTION);
            BasicDBObject idDoc =
                    new BasicDBObject("org", requestedOrg)
                            .append("name", requestedName)
                            .append("type", requestedType)
                            .append("status", requestedStatus);

            BasicDBObject query = new BasicDBObject("_id", idDoc);
            cursor = coll.find(query);

            while (cursor.hasNext()) {
                BasicDBObject doc = (BasicDBObject) cursor.next();
                latestVersion = doc.getString("value");
            }

            DBObject err = mongoDB.getLastError();
            if (!((CommandResult) err).ok()) {
                throw ((CommandResult) err).getException();
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return latestVersion;
    }
}
