package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository;

/**
 * @author Gregory Boissinot
 */
public class NullArtifactRepository implements LatestArtifactRepository {

    @Override
    public String getName() {
        return "Null Repository (No Repository)";
    }

    @Override
    public String getLatestVersion(String requestedOrg, String requestedName, String requestedType, String requestedStatus) {

        if (requestedOrg == null) {
            throw new NullPointerException("An organisation is required.");
        }

        if (requestedName == null) {
            throw new NullPointerException("A name is required.");
        }

        if (requestedType == null) {
            throw new NullPointerException("A type is required.");
        }

        if (requestedStatus == null) {
            throw new NullPointerException("A status is required.");
        }

        return null;
    }

}
