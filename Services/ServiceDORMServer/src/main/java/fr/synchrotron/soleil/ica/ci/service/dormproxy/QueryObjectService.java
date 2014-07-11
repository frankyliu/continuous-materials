package fr.synchrotron.soleil.ica.ci.service.dormproxy;

import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;

/**
 * @author Gregory Boissinot
 */
public class QueryObjectService {
    private  final Logger logger = LoggerFactory.getLogger(QueryObjectService.class);
    //sample: orgPart1/orgPart2/orgPart3/name/version/name-version.pom
    //TODO Check validation Maven name (check name for example)
    public JsonObject getMavenQueryObject(String queryPath) {

        JsonObject queryObject = new JsonObject();

        //Remove the endPath that finishes with .pom
        final int endIndex = queryPath.lastIndexOf("/");
        if (endIndex == -1) {
            throw new IllegalStateException(queryPath + " is not a valid pom query.");
        }
        String queryArtifact = queryPath.substring(0, endIndex);

        //Extract version
        final int endIndexVersion = queryArtifact.lastIndexOf("/");
        if (endIndexVersion == -1) {
            throw new IllegalStateException(queryPath + " is not a valid pom query.");
        }
        String version = queryArtifact.substring(endIndexVersion + 1);
        String status;
        //TODO Use service lib
        if (version.endsWith("-SNAPSHOT")) {
            status = "INTEGRATION";
            version = version.substring(0, version.indexOf("-SNAPSHOT"));
        } else if (version.endsWith("RELEASE")) {
            status = "RELEASE";
            version = version.substring(0, version.lastIndexOf("."));
        } else if (version.endsWith("INTEGRATION")) {
            status = "INTEGRATION";
            version = version.substring(0, version.lastIndexOf("."));
        } else {
            status = "RELEASE";
        }
        queryArtifact = queryArtifact.substring(0, endIndexVersion);


        //Extract name
        final int endIndexName = queryArtifact.lastIndexOf("/");
        if (endIndexName == -1) {
            logger.error(queryPath + " is not a valid pom query.");
            throw new IllegalStateException(queryPath + " is not a valid pom query.");
        }
        String name = queryArtifact.substring(endIndexName + 1);
        queryArtifact = queryArtifact.substring(0, endIndexName);

        //Extract organization
        String org = queryArtifact.replaceAll("/", ".");

        queryObject.putString("org", org);
        queryObject.putString("name", name);
        queryObject.putString("version", version);
        queryObject.putString("status", status);
        return queryObject;
    }
}
