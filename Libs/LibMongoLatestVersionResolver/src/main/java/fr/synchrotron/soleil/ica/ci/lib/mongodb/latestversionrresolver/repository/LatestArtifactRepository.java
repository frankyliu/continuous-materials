package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository;

/**
 * @author Gregory Boissinot
 */
public interface LatestArtifactRepository {

    /**
     * Gets the logical name of the artifact metadata repository
     * Mainly used for logging.
     *
     * @return the artifact repository name
     */
    public String getName();

    /**
     * Gets the latest version for an input artifact
     *
     * @param requestedOrg
     * @param requestedName
     * @param requestedType
     * @param requestedStatus
     * @return the latest version, null if none exist for the request artifact
     */
    public String getLatestVersion(String requestedOrg,
                                   String requestedName,
                                   String requestedType,
                                   String requestedStatus);

}
